/**
 * 
 */
package es.caib.ripea.core.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import es.caib.ripea.core.api.dto.DocumentEnviamentEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentNotificacioEstatEnumDto;
import es.caib.ripea.core.api.dto.PermisDto;
import es.caib.ripea.core.api.dto.PrincipalTipusEnumDto;
import es.caib.ripea.core.api.dto.TascaEstatEnumDto;
import es.caib.ripea.core.entity.CarpetaEntity;
import es.caib.ripea.core.entity.ContingutEntity;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.DocumentNotificacioEntity;
import es.caib.ripea.core.entity.DocumentPortafirmesEntity;
import es.caib.ripea.core.entity.ExecucioMassivaEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.ExpedientTascaEntity;
import es.caib.ripea.core.entity.MetaNodeEntity;
import es.caib.ripea.core.entity.UsuariEntity;
import es.caib.ripea.plugin.usuari.DadesUsuari;

/**
 * Mètodes per a l'enviament de correus.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class EmailHelper {

	private static final String PREFIX_RIPEA = "[RIPEA]";

	@Autowired
	private CacheHelper cacheHelper;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private PermisosHelper permisosHelper;
	@Autowired
	private ExpedientHelper expedientHelper;
	@Autowired
	private JavaMailSender mailSender;

	public void contingutAgafatSensePermis(
			ContingutEntity contingut,
			UsuariEntity usuariOriginal,
			UsuariEntity usuariNou) {
		logger.debug("Enviant correu electrònic per a contingut agafat sense permis (" +
			"contingutId=" + contingut.getId() + ")");
		SimpleMailMessage missatge = new SimpleMailMessage();
		missatge.setFrom(getRemitent());
		String tipus = "desconegut";
		if (contingut instanceof ExpedientEntity) {
			tipus = "expedient";
		} else if (contingut instanceof DocumentEntity) {
			tipus = "document";
		} else if (contingut instanceof CarpetaEntity) {
			tipus = "carpeta";
		}
		if (usuariOriginal.getEmail() != null) {
			missatge.setTo(usuariOriginal.getEmail());
			missatge.setSubject(PREFIX_RIPEA + " Element de l'escriptori agafat per un altre usuari: (" + tipus + ") " + contingut.getNom());
			missatge.setText(
					"Informació de l'element de l'escriptori:\n" +
					"\tEntitat: " + contingut.getEntitat().getNom() + "\n" +
					"\tTipus: " + tipus + "\n" +
					"\tNom: " + contingut.getNom() + "\n\n" + 
					"\tPersona que ho ha agafat: " + usuariNou.getNom() + "(" + usuariNou.getCodi() + ").");
			mailSender.send(missatge);
		}
	}

	public void execucioMassivaFinalitzada(
			ExecucioMassivaEntity em) {
		logger.debug("Enviant correu electrònic per a execució massiva finalitzada (" + 
			"execucioMassivaId=" + em.getId() + ")");
		SimpleMailMessage missatge = new SimpleMailMessage();
		if (emplenarDestinatariAmbUsuari(
				missatge,
				em.getCreatedBy().getCodi())) {
			missatge.setFrom(getRemitent());
			missatge.setSubject(PREFIX_RIPEA + " Execucio massiva finalitzada: " + em.getTipus());
			missatge.setText(
					"Execució massiva finalitzada:\n" +
					"\tId: " + em.getId() + "\n" +
					"\tTipus: " + em.getTipus() + "\n" +
					"\tData Inici: " + em.getDataInici() + "\n" +
					"\tData Fi: " + em.getDataFi() + "\n" +
					"\tContinguts: " + em.getContinguts().size() + "\n");
			mailSender.send(missatge);
		}
	}

	public void canviEstatDocumentPortafirmes(
			DocumentPortafirmesEntity documentPortafirmes) {
		logger.debug("Enviant correu electrònic per a canvi d'estat de document al portafirmes (" +
			"documentPortafirmesId=" + documentPortafirmes.getId() + ")");
		DocumentEntity document = documentPortafirmes.getDocument();
		ExpedientEntity expedient = document.getExpedient();
		Set<DadesUsuari> responsables = getGestors(expedient);
		List<String> destinataris = new ArrayList<String>();
		for (DadesUsuari responsable: responsables) {
			if (responsable != null && (responsable.getEmail() != null && ! responsable.getEmail().isEmpty())) {
				destinataris.add(responsable.getEmail());
			}
		}
		SimpleMailMessage missatge = new SimpleMailMessage();
		missatge.setFrom(getRemitent());
		missatge.setTo(destinataris.toArray(new String[destinataris.size()]));
		missatge.setSubject(PREFIX_RIPEA + " Canvi d'estat de document enviat a portafirmes");
		String estat = (documentPortafirmes.getEstat() == DocumentEnviamentEstatEnumDto.PROCESSAT) ? "FIRMAT" : documentPortafirmes.getEstat().toString();
		String text = "Informació del document:\n" +
				"\tEntitat: " + expedient.getEntitat().getNom() + "\n" +
				"\tExpedient nom: " + expedient.getNom() + "\n" +
				"\tExpedient núm.: " + expedientHelper.calcularNumero(expedient) + "\n" +
				"\tDocument nom: " + document.getNom() + "\n" +
				"\tDocument tipus.: " + document.getMetaDocument().getNom() + "\n" +
				"\tDocument fitxer: " + document.getFitxerNom() + "\n\n" +
				"Estat del document:" + estat + "\n";

		missatge.setText(text);
		logger.debug(missatge.toString());
		mailSender.send(missatge);
	}

	public void canviEstatNotificacio(
			DocumentNotificacioEntity documentNotificacio,
			DocumentEnviamentEstatEnumDto estatAnterior) {
		logger.debug("Enviant correu electrònic per a canvi d'estat de notificació (" +
			"documentNotificacioId=" + documentNotificacio.getId() + ")");
		DocumentEntity document = documentNotificacio.getDocument();
		ExpedientEntity expedient = document.getExpedient();
		Set<DadesUsuari> responsables = getGestors(expedient);
		List<String> destinataris = new ArrayList<String>();
		for (DadesUsuari responsable: responsables) {
			if (responsable != null && (responsable.getEmail() != null && ! responsable.getEmail().isEmpty())) {
				destinataris.add(responsable.getEmail());
			}
		}
		SimpleMailMessage missatge = new SimpleMailMessage();
		missatge.setFrom(getRemitent());
		missatge.setTo(destinataris.toArray(new String[destinataris.size()]));
		missatge.setSubject(PREFIX_RIPEA + " Canvi d'estat de notificació");
		String estat = (documentNotificacio.getEstat() == DocumentEnviamentEstatEnumDto.PROCESSAT) ? "ENTREGAT" : documentNotificacio.getEstat().toString();
		missatge.setText(
				"Informació del document:\n" +
				"\tEntitat: " + expedient.getEntitat().getNom() + "\n" +
				"\tExpedient nom: " + expedient.getNom() + "\n" +
				"\tExpedient núm.: " + expedientHelper.calcularNumero(expedient) + "\n" +
				"\tDocument nom: " + document.getNom() + "\n" +
				"\tDocument tipus.: " + document.getMetaDocument().getNom() + "\n" +
				"\tDocument fitxer: " + document.getFitxerNom() + "\n\n" +
				"Estat anterior:" + estatAnterior + "\n" +
				"Estat actual:" + estat + "\n");
		mailSender.send(missatge);
	}
	
	public void canviEstatNotificacio(
			DocumentNotificacioEntity documentNotificacio,
			DocumentNotificacioEstatEnumDto estatAnterior) {
		logger.debug("Enviant correu electrònic per a canvi d'estat de notificació (" +
			"documentNotificacioId=" + documentNotificacio.getId() + ")");
		DocumentEntity document = documentNotificacio.getDocument();
		ExpedientEntity expedient = document.getExpedient();
		Set<DadesUsuari> responsables = getGestors(expedient);
		List<String> destinataris = new ArrayList<String>();
		for (DadesUsuari responsable: responsables) {
			if (responsable != null && (responsable.getEmail() != null && ! responsable.getEmail().isEmpty())) {
				destinataris.add(responsable.getEmail());
			}
		}
		if (!destinataris.isEmpty()) {
			SimpleMailMessage missatge = new SimpleMailMessage();
			missatge.setFrom(getRemitent());
			missatge.setTo(destinataris.toArray(new String[destinataris.size()]));
			missatge.setSubject(PREFIX_RIPEA + " Canvi d'estat de notificació");
			String estat = (documentNotificacio.getNotificacioEstat().toString());
			missatge.setText(
					"Informació del document:\n" +
					"\tEntitat: " + expedient.getEntitat().getNom() + "\n" +
					"\tExpedient nom: " + expedient.getNom() + "\n" +
					"\tExpedient núm.: " + expedientHelper.calcularNumero(expedient) + "\n" +
					"\tDocument nom: " + document.getNom() + "\n" +
					"\tDocument tipus.: " + document.getMetaDocument().getNom() + "\n" +
					"\tDocument fitxer: " + document.getFitxerNom() + "\n\n" +
					"Estat anterior:" + estatAnterior.toString() + "\n" +
					"Estat actual:" + estat + "\n");
			mailSender.send(missatge);
		} else {
			logger.error("No s'ha trobat cap destinatari per enviar el correu d'avís...");
		}
	}
	
	
	public void enviarEmailCanviarEstatTasca(
			ExpedientTascaEntity expedientTascaEntity,
			TascaEstatEnumDto estatAnterior) {
		logger.debug("Enviant correu electrònic per a canvis de tasca (" +
			"tascaId=" + expedientTascaEntity.getId() + ")");


		enviarEmailCanviarEstatTasca(expedientTascaEntity, estatAnterior, getGestorsEmails(expedientTascaEntity.getExpedient()), false);
		
		if (expedientTascaEntity.getResponsable().getEmail() != null && !expedientTascaEntity.getResponsable().getEmail().isEmpty()) {
			enviarEmailCanviarEstatTasca(expedientTascaEntity, estatAnterior, Arrays.asList(expedientTascaEntity.getResponsable().getEmail()), true);
		}

	}	
	
	
	
	private Set<DadesUsuari> getGestors(ExpedientEntity expedient) {
		Set<DadesUsuari> responsables = new HashSet<DadesUsuari>();
		List<PermisDto> permisos = permisosHelper.findPermisos(
				expedient.getMetaNode().getId(),
				MetaNodeEntity.class);
		for (PermisDto permis: permisos) {
			if (permis.isWrite()) {
				if (PrincipalTipusEnumDto.USUARI == permis.getPrincipalTipus()) {
					responsables.add(
							pluginHelper.dadesUsuariFindAmbCodi(permis.getPrincipalNom()));
				}
				if (PrincipalTipusEnumDto.ROL == permis.getPrincipalTipus()) {
					responsables.addAll(
							pluginHelper.dadesUsuariFindAmbGrup(permis.getPrincipalNom()));
				}
			}
		}
		return responsables;
	}
	
	private List<String> getGestorsEmails(
			ExpedientEntity expedientEntity) {

		Set<DadesUsuari> responsables = getGestors(expedientEntity);
		List<String> destinataris = new ArrayList<String>();
		for (DadesUsuari responsable : responsables) {
			if (responsable != null && (responsable.getEmail() != null && ! responsable.getEmail().isEmpty())) {
				destinataris.add(responsable.getEmail());
			}
		}
		return destinataris;
	}

	
	private void enviarEmailCanviarEstatTasca(
			ExpedientTascaEntity expedientTascaEntity,
			TascaEstatEnumDto estatAnterior,
			List<String> destinataris,
			boolean destinitariHasPermisTasca) {
		logger.debug("Enviant correu electrònic per a canvis de tasca (" +
			"tascaId=" + expedientTascaEntity.getId() + ")");

		SimpleMailMessage missatge = new SimpleMailMessage();
		missatge.setFrom(getRemitent());
		missatge.setTo(destinataris.toArray(new String[destinataris.size()]));
		
		TascaEstatEnumDto estat = expedientTascaEntity.getEstat();
		
		String rebutjMotiu = "";
		if (estat == TascaEstatEnumDto.REBUTJADA) {
			rebutjMotiu = "\tMotiu: " + expedientTascaEntity.getMotiuRebuig() + "\n";
		}
		
		String enllacTramitar = "";
		if (destinitariHasPermisTasca && (estatAnterior == null || expedientTascaEntity.getEstat() == TascaEstatEnumDto.INICIADA || expedientTascaEntity.getEstat() == TascaEstatEnumDto.PENDENT)) {
			enllacTramitar = "Pot accedir a la tasca utilizant el següent enllaç: " + PropertiesHelper.getProperties().getProperty("es.caib.ripea.base.url") + "/usuariTasca/" + expedientTascaEntity.getId() + "/tramitar" + "\n";
		}
		logger.debug(destinataris.toArray(new String[destinataris.size()]).toString());
		if (estatAnterior == null) {
			missatge.setSubject(PREFIX_RIPEA + " Nova tasca: " + expedientTascaEntity.getMetaExpedientTasca().getNom());
			missatge.setText(					
					"S'ha creat una nova tasca a RIPEA:\n" +
					"\tNom: " + expedientTascaEntity.getMetaExpedientTasca().getNom() + "\n" +
					"\tDescripció: " + expedientTascaEntity.getMetaExpedientTasca().getDescripcio() + "\n" +
					"\tEstat: " + estat + "\n" +
					enllacTramitar);
		} else {

			missatge.setSubject(PREFIX_RIPEA + " Canvi d'estat de la tasca: " + expedientTascaEntity.getMetaExpedientTasca().getNom());
			missatge.setText(					
					"S'ha modificat l'estat de la tasca a RIPEA:\n" +
							"\tNom: " + expedientTascaEntity.getMetaExpedientTasca().getNom() + "\n" +
							"\tDescripció: " + expedientTascaEntity.getMetaExpedientTasca().getDescripcio() + "\n" +
							"\tEstat anterior:" + estatAnterior + "\n" +
							"\tEstat actual:" + estat + "\n" + 
							rebutjMotiu +
							enllacTramitar);
		}
		logger.debug(missatge.toString());
		mailSender.send(missatge);
	}
	

	private boolean emplenarDestinatariAmbUsuari(
			MailMessage mailMessage,
			String usuariCodi) {
		DadesUsuari dadesUsuari = cacheHelper.findUsuariAmbCodi(usuariCodi);
		if (dadesUsuari != null && dadesUsuari.getEmail() != null) {
			mailMessage.setTo(dadesUsuari.getEmail());
			return true;
		} else {
			return false;
		}
	}

	private String getRemitent() {
		return PropertiesHelper.getProperties().getProperty("es.caib.ripea.email.remitent");
	}

	private static final Logger logger = LoggerFactory.getLogger(EmailHelper.class);

}
