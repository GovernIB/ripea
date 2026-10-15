package es.caib.ripea.service.helper;

import java.net.InetAddress;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.ExecucioMassivaEntity;
import es.caib.ripea.persistence.repository.ExecucioMassivaContingutRepository;
import es.caib.ripea.persistence.repository.ExecucioMassivaRepository;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.ExecucioMassivaEstatDto;
import es.caib.ripea.service.intf.dto.ExecucioMassivaProcesEstatDto;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.exception.PermissionDeniedException;
import es.caib.ripea.service.intf.exception.ValidationException;
import es.caib.ripea.service.permission.ExtendedPermission;

/**
 * Cua d'execucions massives: quina agafa cada node, el batec que demostra que segueix viva
 * i la cancel·lació.
 *
 * Una execució només la processa el node que l'ha reclamada amb un update condicional (atòmic a
 * Oracle i a PostgreSQL). Si el node cau, la fila queda EN_CURS: la torna a agafar el mateix node
 * en arrencar o qualsevol altre quan el batec caduca, i continua pels elements pendents.
 */
@Component
public class ExecucioMassivaCuaHelper {

	/** Cada quant el procés renova el batec de l'execució que té en curs. */
	public static final long HEARTBEAT_INTERVAL_MS = 60 * 1000L;
	/** Sense batec durant aquest temps, un altre node pot reprendre l'execució. */
	private static final long HEARTBEAT_CADUCITAT_MS = 5 * HEARTBEAT_INTERVAL_MS;
	/** Candidates que es proven per cicle si un altre node s'avança a reclamar-les. */
	private static final int MAX_CANDIDATES = 5;

	private static final String NODE = calcularNode();

	@Autowired private ExecucioMassivaRepository execucioMassivaRepository;
	@Autowired private ExecucioMassivaContingutRepository execucioMassivaContingutRepository;
	@Autowired private EntityComprovarHelper entityComprovarHelper;
	@Autowired private PermisosHelper permisosHelper;
	@Autowired private EmailHelper emailHelper;
	@Autowired private ExecucioMassivaHelper execucioMassivaHelper;
	@Autowired private MessageHelper messageHelper;

	public static String getNode() {
		return NODE;
	}

	/**
	 * Reclama per a aquest node la següent execució a processar.
	 * @return l'id de l'execució reclamada, o null si no n'hi ha cap de disponible.
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Long reclamarExecucioMassivaPerProcessar(Date ara) {
		Date limitHeartbeat = new Date(ara.getTime() - HEARTBEAT_CADUCITAT_MS);
		List<Long> candidates = execucioMassivaRepository.findIdsProcessables(
				ara,
				ExecucioMassivaProcesEstatDto.PENDENT,
				ExecucioMassivaProcesEstatDto.EN_CURS,
				NODE,
				limitHeartbeat,
				PageRequest.of(0, MAX_CANDIDATES));
		for (Long id : candidates) {
			int reclamades = execucioMassivaRepository.reclamar(
					id,
					ara,
					ExecucioMassivaProcesEstatDto.PENDENT,
					ExecucioMassivaProcesEstatDto.EN_CURS,
					NODE,
					limitHeartbeat);
			if (reclamades == 1) {
				return id;
			}
		}
		return null;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void actualitzarHeartbeat(Long execucioMassivaId) {
		execucioMassivaRepository.updateHeartbeat(
				execucioMassivaId,
				NODE,
				ExecucioMassivaProcesEstatDto.EN_CURS,
				new Date());
	}

	/** Es consulta en una transacció pròpia per veure la cancel·lació que s'hagi confirmat mentre es processa. */
	@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
	public boolean isCancelacioSolicitada(Long execucioMassivaId) {
		return execucioMassivaRepository.getCancelacioData(execucioMassivaId) != null;
	}

	/**
	 * Si l'usuari pot cancel·lar l'execució segons el rol, sense consultar permisos: només per decidir què es
	 * mostra a la interfície. {@link #cancelar} ho torna a comprovar tot.
	 */
	public static boolean potCancelar(String creador, String usuariActual, String rolActual) {
		return (creador != null && creador.equals(usuariActual)) || BaseConfig.ROLE_ADMIN.equals(rolActual);
	}

	/**
	 * Cancel·la una execució massiva. Pot fer-ho el creador o un administrador de l'entitat (rol IPA_ADMIN amb
	 * permís d'administració sobre l'entitat; l'administrador d'òrgan i el de lectura no).
	 *
	 * Si cap node l'ha agafada, es tanca a l'acte. Si està en curs, només es deixa constància de la petició: el
	 * procés ho comprova abans de cada element, i l'element que s'està processant acaba normalment.
	 *
	 * @return CANCELADA si s'ha tancat a l'acte, EN_CURS si s'ha sol·licitat la cancel·lació.
	 */
	@Transactional
	public ExecucioMassivaProcesEstatDto cancelar(Long entitatId, Long execucioMassivaId, String rolActual) {

		entityComprovarHelper.comprovarEntitat(entitatId, false, false, false, false, false);
		ExecucioMassivaEntity execucioMassiva = execucioMassivaRepository.findById(execucioMassivaId).orElse(null);
		if (execucioMassiva == null || !execucioMassiva.getEntitat().getId().equals(entitatId)) {
			throw new NotFoundException(execucioMassivaId, ExecucioMassivaEntity.class);
		}

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String usuari = auth.getName();
		String creador = execucioMassiva.getCreatedBy().orElse(null);
		boolean esCreador = usuari.equals(creador);
		if (!esCreador && !isAdministradorEntitat(entitatId, rolActual, auth)) {
			throw new PermissionDeniedException(execucioMassivaId, ExecucioMassivaEntity.class, usuari, "CANCELAR");
		}
		if (!execucioMassiva.getTipus().isCancelable()) {
			throw new ValidationException(messageHelper.getMessage("accio.massiva.cancelar.error.tipus"));
		}
		if (execucioMassiva.getDataFi() != null) {
			throw new ValidationException(messageHelper.getMessage("accio.massiva.cancelar.error.finalitzada"));
		}

		Date ara = new Date();
		int cancelades = execucioMassivaRepository.cancelarPendent(
				execucioMassivaId,
				ara,
				usuari,
				ExecucioMassivaProcesEstatDto.PENDENT,
				ExecucioMassivaProcesEstatDto.CANCELADA);
		if (cancelades == 1) {
			execucioMassivaContingutRepository.cancelarPendents(
					execucioMassivaId,
					ara,
					ExecucioMassivaEstatDto.ESTAT_PENDENT,
					ExecucioMassivaEstatDto.ESTAT_CANCELAT);
			//Els updates han buidat el context de persistència: es torna a llegir amb els valors nous
			ExecucioMassivaEntity cancelada = execucioMassivaRepository.findById(execucioMassivaId).get();
			//Mateixa neteja que en tancar-la el procés (ZIP temporal d'IMPORTAR_DOCS)
			execucioMassivaHelper.esborrarFitxerTemporalImportacio(cancelada);
			//Si l'ha cancel·lada un administrador, el creador se n'ha d'assabentar
			if (!esCreador) {
				emailHelper.execucioMassivaFinalitzada(cancelada);
			}
			return ExecucioMassivaProcesEstatDto.CANCELADA;
		}

		//Ja l'ha agafada un node: s'atura en acabar l'element en curs
		int sollicitades = execucioMassivaRepository.solicitarCancelacio(execucioMassivaId, ara, usuari);
		if (sollicitades == 0) {
			//O ja s'havia sol·licitat (es respon igual), o ha acabat just ara
			ExecucioMassivaEntity actual = execucioMassivaRepository.findById(execucioMassivaId).get();
			if (actual.getDataFi() != null) {
				throw new ValidationException(messageHelper.getMessage("accio.massiva.cancelar.error.finalitzada"));
			}
		}
		logger.info("S'ha sol·licitat la cancel·lació de l'execució massiva en curs (id=" + execucioMassivaId
				+ ", usuari=" + usuari + ", node=" + execucioMassiva.getNode() + ")");
		return ExecucioMassivaProcesEstatDto.EN_CURS;
	}

	private boolean isAdministradorEntitat(Long entitatId, String rolActual, Authentication auth) {
		//Només ADMINISTRATION: l'administrador de lectura (ADMINISTRATION_READ) no pot fer canvis
		return BaseConfig.ROLE_ADMIN.equals(rolActual) && permisosHelper.isGrantedAll(
				entitatId,
				EntitatEntity.class,
				new Permission[] { ExtendedPermission.ADMINISTRATION },
				auth);
	}

	private static String calcularNode() {
		String node = System.getProperty("jboss.node.name");
		if (node == null || node.isEmpty()) {
			try {
				node = InetAddress.getLocalHost().getHostName();
			} catch (Exception ex) {
				node = "desconegut";
			}
		}
		return node.length() > 255 ? node.substring(0, 255) : node;
	}

	private static final Logger logger = LoggerFactory.getLogger(ExecucioMassivaCuaHelper.class);
}
