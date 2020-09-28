/**
 * 
 */
package es.caib.ripea.core.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.jopendocument.dom.spreadsheet.SpreadSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Persistable;
import org.springframework.data.domain.Sort;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.ws.backofficeintegracio.AnotacioRegistreId;
import es.caib.distribucio.ws.backofficeintegracio.Estat;
import es.caib.ripea.core.api.dto.ArxiuFirmaDto;
import es.caib.ripea.core.api.dto.ArxiuFirmaPerfilEnumDto;
import es.caib.ripea.core.api.dto.ArxiuFirmaTipusEnumDto;
import es.caib.ripea.core.api.dto.DocumentEstatEnumDto;
import es.caib.ripea.core.api.dto.ExpedientComentariDto;
import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.dto.ExpedientEstatEnumDto;
import es.caib.ripea.core.api.dto.ExpedientFiltreDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioEstatEnumDto;
import es.caib.ripea.core.api.dto.ExpedientSelectorDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.LogObjecteTipusEnumDto;
import es.caib.ripea.core.api.dto.LogTipusEnumDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.exception.ExpedientTancarSenseDocumentsDefinitiusException;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.exception.ValidationException;
import es.caib.ripea.core.api.service.ExpedientService;
import es.caib.ripea.core.entity.CarpetaEntity;
import es.caib.ripea.core.entity.ContingutEntity;
import es.caib.ripea.core.entity.DadaEntity;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientComentariEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.ExpedientEstatEntity;
import es.caib.ripea.core.entity.ExpedientPeticioEntity;
import es.caib.ripea.core.entity.MetaDadaEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.MetaNodeEntity;
import es.caib.ripea.core.entity.RegistreAnnexEntity;
import es.caib.ripea.core.entity.UsuariEntity;
import es.caib.ripea.core.firma.DocumentFirmaServidorFirma;
import es.caib.ripea.core.helper.ContingutHelper;
import es.caib.ripea.core.helper.ContingutLogHelper;
import es.caib.ripea.core.helper.ConversioTipusHelper;
import es.caib.ripea.core.helper.CsvHelper;
import es.caib.ripea.core.helper.DateHelper;
import es.caib.ripea.core.helper.DistribucioHelper;
import es.caib.ripea.core.helper.DocumentHelper;
import es.caib.ripea.core.helper.EmailHelper;
import es.caib.ripea.core.helper.EntityComprovarHelper;
import es.caib.ripea.core.helper.ExpedientHelper;
import es.caib.ripea.core.helper.ExpedientPeticioHelper;
import es.caib.ripea.core.helper.MessageHelper;
import es.caib.ripea.core.helper.MetaExpedientHelper;
import es.caib.ripea.core.helper.PaginacioHelper;
import es.caib.ripea.core.helper.PaginacioHelper.Converter;
import es.caib.ripea.core.helper.PermisosHelper;
import es.caib.ripea.core.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.ripea.core.helper.PluginHelper;
import es.caib.ripea.core.helper.UsuariHelper;
import es.caib.ripea.core.repository.AlertaRepository;
import es.caib.ripea.core.repository.ContingutRepository;
import es.caib.ripea.core.repository.DadaRepository;
import es.caib.ripea.core.repository.DocumentRepository;
import es.caib.ripea.core.repository.ExpedientComentariRepository;
import es.caib.ripea.core.repository.ExpedientEstatRepository;
import es.caib.ripea.core.repository.ExpedientPeticioRepository;
import es.caib.ripea.core.repository.ExpedientRepository;
import es.caib.ripea.core.repository.MetaExpedientRepository;
import es.caib.ripea.core.security.ExtendedPermission;
import es.caib.ripea.plugin.firmaservidor.FirmaServidorPlugin.TipusFirma;

/**
 * Implementació dels mètodes per a gestionar expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class ExpedientServiceImpl implements ExpedientService {

	@Autowired
	private MetaExpedientRepository metaExpedientRepository;
	@Autowired
	private ExpedientRepository expedientRepository;
	@Autowired
	private ExpedientComentariRepository expedientComentariRepository;
	@Autowired
	private ExpedientEstatRepository expedientEstatRepository;
	@Autowired
	private ExpedientPeticioRepository expedientPeticioRepository;
	@Autowired
	private DocumentRepository documentRepository;
	@Autowired
	private DadaRepository dadaRepository;
	@Autowired
	private AlertaRepository alertaRepository;
	@Autowired
	private ContingutRepository contingutRepository;
	@Autowired
	private ExpedientHelper expedientHelper;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private PermisosHelper permisosHelper;
	@Autowired
	private ContingutHelper contingutHelper;
	@Autowired
	private PaginacioHelper paginacioHelper;
	@Autowired
	private UsuariHelper usuariHelper;
	@Autowired
	private EmailHelper emailHelper;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private CsvHelper csvHelper;
	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private MessageHelper messageHelper;
	@Autowired
	private ExpedientPeticioHelper expedientPeticioHelper;
	@Autowired
	private ContingutLogHelper contingutLogHelper;
	@Autowired
	private DocumentHelper documentHelper;
	@Autowired
	private MetaExpedientHelper metaExpedientHelper;
	@Autowired
	private DocumentFirmaServidorFirma documentFirmaServidorFirma;
	
	@Transactional
	@Override
	public ExpedientDto create(
			Long entitatId,
			Long metaExpedientId,
			Long metaExpedientDominiId,
			Long pareId,
			Integer any,
			Long sequencia,
			String nom,
			Long expedientPeticioId,
			boolean associarInteressats,
			Long grupId) {
		logger.debug(
				"Creant nou expedient (" + "entitatId=" + entitatId + ", " + "metaExpedientId=" + metaExpedientId +
						", " + "pareId=" + pareId + ", " + "any=" + any + ", " + "sequencia=" + sequencia + ", " +
						"nom=" + nom + ", " + "expedientPeticioId=" + expedientPeticioId + ")");
		// if expedient comes from distribucio
		ExpedientPeticioEntity expedientPeticioEntity = null;
		if (expedientPeticioId != null) {
			expedientPeticioEntity = expedientPeticioRepository.findOne(expedientPeticioId);
		}
		// create expedient in db and in arxiu
		ExpedientEntity expedient = expedientHelper.create(
				entitatId,
				metaExpedientId,
				metaExpedientDominiId,
				pareId,
				any,
				sequencia,
				nom,
				expedientPeticioId,
				associarInteressats,
				grupId);
		contingutLogHelper.logCreacio(expedient, false, false);
		if (expedient.getAgafatPer() != null) {
			contingutLogHelper.log(expedient, LogTipusEnumDto.AGAFAR, null, null, false, false);
		}

		ExpedientDto expedientDto = toExpedientDto(expedient, true);
		contingutHelper.arxiuPropagarModificacio(expedient, null, false, false, null);
		boolean processatOk = true;
		// if expedient comes from distribucio
		if (expedientPeticioId != null) {
			for (RegistreAnnexEntity registeAnnexEntity : expedientPeticioEntity.getRegistre().getAnnexos()) {
				try {
					DocumentEntity createdDoc = expedientHelper.crearDocFromAnnex(
							registeAnnexEntity.getId(),
							expedientPeticioEntity.getId());
					contingutLogHelper.logCreacio(createdDoc, true, true);
				} catch (Exception e) {
					processatOk = false;
					logger.info(ExceptionUtils.getStackTrace(e));
					expedientHelper.updateRegistreAnnexError(
							registeAnnexEntity.getId(),
							ExceptionUtils.getStackTrace(e));

				}
			}
			canviEstatToProcessatPendent(expedientPeticioEntity);
			if (processatOk) {
				notificarICanviEstatToProcessatNotificat(expedientPeticioEntity.getId());
			}
		}
		expedientDto.setProcessatOk(processatOk);
		return expedientDto;
	}

	@Override
	public boolean incorporar(Long entitatId, Long expedientId, Long expedientPeticioId, boolean associarInteressats) {
		logger.debug(
				"Incorporant a l'expedient existent (" + "entitatId=" + entitatId + ", " + "expedientId=" +
						expedientId + ", " + "expedientPeticioId=" + expedientPeticioId + ")");

		ExpedientPeticioEntity expedientPeticioEntity = expedientPeticioRepository.findOne(expedientPeticioId);

		expedientHelper.relateExpedientWithPeticioAndSetAnnexosPendentNewTransaction(expedientPeticioId, expedientId);

		expedientHelper.associateInteressats(expedientId, entitatId, expedientPeticioId);

		boolean processatOk = true;
		for (RegistreAnnexEntity registeAnnexEntity : expedientPeticioEntity.getRegistre().getAnnexos()) {
			try {

				boolean throwException1 = false;
				if (throwException1)
					throw new RuntimeException("EXCEPION BEFORE INCORPORAR !!!!!! ");

				DocumentEntity createdDoc = expedientHelper.crearDocFromAnnex(
						registeAnnexEntity.getId(),
						expedientPeticioEntity.getId());
				contingutLogHelper.logCreacio(createdDoc, true, true);

			} catch (Exception e) {
				processatOk = false;
				logger.error(ExceptionUtils.getStackTrace(e));
				expedientHelper.updateRegistreAnnexError(registeAnnexEntity.getId(), ExceptionUtils.getStackTrace(e));
			}
		}

		canviEstatToProcessatPendent(expedientPeticioEntity);

		if (processatOk) {
			notificarICanviEstatToProcessatNotificat(expedientPeticioEntity.getId());
		}

		return processatOk;
	}

	public void canviEstatToProcessatPendent(ExpedientPeticioEntity expedientPeticioEntity) {

		expedientPeticioHelper.canviEstatExpedientPeticio(
				expedientPeticioEntity.getId(),
				ExpedientPeticioEstatEnumDto.PROCESSAT_PENDENT);

	}

	public void notificarICanviEstatToProcessatNotificat(Long expedientPeticioId) {

		ExpedientPeticioEntity expedientPeticioEntity = expedientPeticioRepository.findOne(expedientPeticioId);
		AnotacioRegistreId anotacioRegistreId = new AnotacioRegistreId();
		anotacioRegistreId.setClauAcces(expedientPeticioEntity.getClauAcces());
		anotacioRegistreId.setIndetificador(expedientPeticioEntity.getIdentificador());

		try {
			// change state of registre in DISTRIBUCIO to BACK_PROCESSADA
			DistribucioHelper.getBackofficeIntegracioServicePort().canviEstat(anotacioRegistreId, Estat.PROCESSADA, "");

			// change state of expedient peticion to processat and notificat to DISTRIBUCIO
			expedientPeticioHelper.canviEstatExpedientPeticio(
					expedientPeticioEntity.getId(),
					ExpedientPeticioEstatEnumDto.PROCESSAT_NOTIFICAT);

		} catch (Exception e) {
			expedientHelper.updateNotificarError(expedientPeticioEntity.getId(), ExceptionUtils.getStackTrace(e));
		}

	}

	@Override
	public boolean retryCreateDocFromAnnex(Long registreAnnexId, Long expedientPeticioId) {

		boolean processatOk = true;
		try {
			DocumentEntity createdDoc = expedientHelper.crearDocFromAnnex(registreAnnexId, expedientPeticioId);
			contingutLogHelper.logCreacio(createdDoc, true, true);
			expedientHelper.updateRegistreAnnexError(registreAnnexId, null);
		} catch (Exception e) {
			processatOk = false;
			logger.debug(ExceptionUtils.getStackTrace(e));
			expedientHelper.updateRegistreAnnexError(registreAnnexId, ExceptionUtils.getStackTrace(e));
		}

		notificarICanviEstatToProcessatNotificat(expedientPeticioId);

		return processatOk;
	}

	@Override
	public boolean retryNotificarDistribucio(Long expedientPeticioId) {

		ExpedientPeticioEntity expedientPeticioEntity = new ExpedientPeticioEntity();

		boolean processatOk = true;
		try {
			expedientPeticioEntity = expedientPeticioRepository.findOne(expedientPeticioId);

			AnotacioRegistreId anotacioRegistreId = new AnotacioRegistreId();
			anotacioRegistreId.setClauAcces(expedientPeticioEntity.getClauAcces());
			anotacioRegistreId.setIndetificador(expedientPeticioEntity.getIdentificador());

			// change state of registre in DISTRIBUCIO to BACK_PROCESSADA
			DistribucioHelper.getBackofficeIntegracioServicePort().canviEstat(anotacioRegistreId, Estat.PROCESSADA, "");
			expedientPeticioEntity.updateNotificaDistError(null);

		} catch (Exception e) {
			expedientPeticioEntity.updateNotificaDistError(ExceptionUtils.getStackTrace(e));
			processatOk = false;
		}

		return processatOk;
	}

	@Transactional
	@Override
	public ExpedientDto update(Long entitatId, Long id, String nom) {
		logger.debug(
				"Actualitzant dades de l'expedient (" + "entitatId=" + entitatId + ", " + "id=" + id + ", " + "nom=" +
						nom + ")");
		contingutHelper.comprovarContingutDinsExpedientModificable(entitatId, id, false, true, false, false);
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				id,
				false,
				false,
				true,
				false,
				false);
		contingutHelper.comprovarNomValid(expedient.getPare(), nom, id, ExpedientEntity.class);
		String nomOriginal = expedient.getNom();
		expedient.update(nom);
		contingutLogHelper.log(
				expedient,
				LogTipusEnumDto.MODIFICACIO,
				(!nomOriginal.equals(expedient.getNom())) ? expedient.getNom() : null,
				null,
				false,
				false);
		ExpedientDto dto = toExpedientDto(expedient, true);
		contingutHelper.arxiuPropagarModificacio(expedient, null, false, false, null);
		return dto;
	}

	@Transactional
	@Override
	public ExpedientDto update(Long entitatId, Long id, String nom, int any, Long metaExpedientDominiId) {
		logger.debug(
				"Actualitzant dades de l'expedient (" + "entitatId=" + entitatId + ", " + "id=" + id + ", " + "nom=" +
						nom + ")");
		contingutHelper.comprovarContingutDinsExpedientModificable(entitatId, id, false, true, false, false);
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				id,
				false,
				false,
				true,
				false,
				false);
		contingutHelper.comprovarNomValid(expedient.getPare(), nom, id, ExpedientEntity.class);
		String nomOriginal = expedient.getNom();
		expedient.update(nom);
		contingutLogHelper.log(
				expedient,
				LogTipusEnumDto.MODIFICACIO,
				(!nomOriginal.equals(expedient.getNom())) ? expedient.getNom() : null,
				null,
				false,
				false);

		int anyOriginal = expedient.getAny();
		expedient.updateAny(any);
		contingutLogHelper.log(
				expedient,
				LogTipusEnumDto.MODIFICACIO,
				(anyOriginal != (expedient.getAny())) ? String.valueOf(expedient.getAny()) : null,
				null,
				false,
				false);

		ExpedientDto dto = toExpedientDto(expedient, true);
		contingutHelper.arxiuPropagarModificacio(expedient, null, false, false, null);
		return dto;
	}

	@Transactional(readOnly = true)
	@Override
	public ExpedientDto findById(Long entitatId, Long id) {
		logger.debug("Obtenint l'expedient (" + "entitatId=" + entitatId + ", " + "id=" + id + ")");
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				id,
				false,
				true,
				false,
				false,
				false);
		return toExpedientDto(expedient, true);
	}

	@Transactional(readOnly = true)
	public ExpedientDto findByMetaExpedientAndPareAndNomAndEsborrat(
			Long entitatId,
			Long metaExpedientId,
			Long pareId,
			String nom,
			int esborrat) {
		logger.debug(
				"Consultant expedient (" + "entitatId=" + entitatId + ", " + "metaExpedientId=" + metaExpedientId +
						", " + "pareId=" + pareId + ", " + "nom=" + nom + ", " + "esborrat=" + esborrat + ")");

		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, true, false, false);
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
				entitat,
				metaExpedientId,
				false,
				false,
				true,
				false);

		ContingutEntity contingutPare = null;
		if (pareId != null) {
			contingutPare = contingutHelper.comprovarContingutDinsExpedientModificable(
					entitatId,
					pareId,
					false,
					false,
					true,
					false);
		}
		ExpedientEntity expedient = expedientRepository.findByMetaExpedientAndPareAndNomAndEsborrat(
				metaExpedient,
				contingutPare,
				nom,
				esborrat);
		return expedient == null ? null : toExpedientDto(expedient, true);
	}

	@Transactional
	@Override
	public boolean publicarComentariPerExpedient(Long entitatId, Long expedientId, String text) {
		logger.debug(
				"Obtenint els comentaris pel contingut (" + "entitatId=" + entitatId + ", " + "nodeId=" + expedientId +
						")");
		entityComprovarHelper.comprovarEntitat(entitatId, false, false, true);
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				expedientId,
				false,
				false,
				true,
				false,
				false);
		// truncam a 1024 caracters
		if (text.length() > 1024)
			text = text.substring(0, 1024);
		ExpedientComentariEntity comentari = ExpedientComentariEntity.getBuilder(expedient, text).build();
		expedientComentariRepository.save(comentari);
		return true;
	}

	@Transactional(readOnly = true)
	@Override
	public List<ExpedientComentariDto> findComentarisPerContingut(Long entitatId, Long expedientId) {
		logger.debug(
				"Obtenint els comentaris pel expedient (" + "entitatId=" + entitatId + ", " + "nodeId=" + expedientId +
						")");
		entityComprovarHelper.comprovarEntitat(entitatId, false, false, true);
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				expedientId,
				false,
				true,
				false,
				false,
				false);

		List<ExpedientComentariEntity> expcoms = expedientComentariRepository.findByExpedientOrderByCreatedDateAsc(
				expedient);

		return conversioTipusHelper.convertirList(expcoms, ExpedientComentariDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public boolean hasWritePermission(Long expedientId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		if (comprovarPermisRead) {
//			boolean granted = permisosHelper.isGrantedAll(
//					metaNode.getId(),
//					MetaNodeEntity.class,
//					new Permission[] {ExtendedPermission.READ},
//					auth);
//			if (!granted) {
//				throw new SecurityException("Sense permisos per accedir al node ("
//						+ "id=" + nodeId + ", "
//						+ "usuari=" + auth.getName() + ")");
//			}
//		}

		ExpedientEntity expedient = expedientRepository.findOne(expedientId);

		boolean granted = permisosHelper.isGrantedAll(
				expedient.getMetaExpedient().getId(),
				MetaNodeEntity.class,
				new Permission[] { ExtendedPermission.WRITE },
				auth);

		if (!granted && entityComprovarHelper.hasEstatWritePermissons(expedient.getExpedientEstat().getId())) {
			granted = true;
		}

		return granted;

	}

	@Transactional(readOnly = true)
	@Override
	public PaginaDto<ExpedientDto> findAmbFiltreUser(
			Long entitatId,
			ExpedientFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug(
				"Consultant els expedients segons el filtre per usuaris (" + "entitatId=" + entitatId + ", " +
						"filtre=" + filtre + ", " + "paginacioParams=" + paginacioParams + ")");
		entityComprovarHelper.comprovarEntitat(entitatId, true, false, false);
		return findAmbFiltrePaginat(entitatId, filtre, paginacioParams, false, true);
	}

	@Transactional(readOnly = true)
	@Override
	public PaginaDto<ExpedientDto> findAmbFiltreNoRelacionat(
			Long entitatId,
			ExpedientFiltreDto filtre,
			Long expedientId,
			PaginacioParamsDto paginacioParams) {
		logger.debug(
				"Consultant els expedients segons el filtre per usuaris (" + "entitatId=" + entitatId + ", " +
						"filtre=" + filtre + ", " + "paginacioParams=" + paginacioParams +
						"id del expedient relacionat" + expedientId + ")");
		entityComprovarHelper.comprovarEntitat(entitatId, true, false, false);

		return findAmbFiltrePaginat(entitatId, filtre, paginacioParams, expedientId);
	}

	@Transactional
	@Override
	public List<ExpedientDto> findByEntitatAndMetaExpedient(Long entitatId, Long metaExpedientId) {
		logger.debug(
				"Consultant els expedients(" + "entitatId=" + entitatId + ", " + "metaExpedientId=" + metaExpedientId +
						")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, true, false, false);
		MetaExpedientEntity metaExpedient = null;
		if (metaExpedientId != null) {

			metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
					entitat,
					metaExpedientId,
					true,
					false,
					false,
					false);
		}

		List<ContingutEntity> expedientsEnt = contingutRepository.findByEntitatAndMetaExpedient(entitat, metaExpedient);

		List<ExpedientDto> expedientsDto = new ArrayList<>();

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		permisosHelper.isGrantedAll(
				metaExpedientId,
				MetaNodeEntity.class,
				new Permission[] { ExtendedPermission.WRITE },
				auth);

		// if meta expedient has write permissions add all expedients
		if (entityComprovarHelper.hasMetaExpedientWritePermissons(metaExpedientId)) {

			for (ContingutEntity exp : expedientsEnt) {
				expedientsDto.add(
						(ExpedientDto)contingutHelper.toContingutDto(exp, true, true, true, false, true, false, false));

			}
		} else { // if not add only expedients having estat with permisions

			for (ContingutEntity exp : expedientsEnt) {
				ExpedientEntity expedient = (ExpedientEntity)exp;
				if (expedient.getExpedientEstat() != null &&
						entityComprovarHelper.hasEstatWritePermissons(expedient.getExpedientEstat().getId())) {
					expedientsDto.add(
							(ExpedientDto)contingutHelper.toContingutDto(
									exp,
									true,
									true,
									true,
									false,
									true,
									false,
									false));
				}
			}
		}

		return expedientsDto;
	}

	@Transactional(readOnly = true)
	@Override
	public List<ExpedientSelectorDto> findPerUserAndTipus(Long entitatId, Long metaExpedientId) {
		logger.debug(
				"Consultant els expedients segons el tipus per usuaris (" + "entitatId=" + entitatId + ", " +
						"metaExpedientId=" + metaExpedientId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, true, false, false);
		MetaExpedientEntity metaExpedient = null;
		if (metaExpedientId != null) {
			metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
					entitat,
					metaExpedientId,
					false,
					true,
					false,
					false);
		}
		List<MetaExpedientEntity> metaExpedientsPermesos = metaExpedientRepository.findByEntitatOrderByNomAsc(entitat);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		permisosHelper.filterGrantedAll(metaExpedientsPermesos, new ObjectIdentifierExtractor<MetaExpedientEntity>() {

			@Override
			public Long getObjectIdentifier(MetaExpedientEntity metaExpedient) {
				return metaExpedient.getId();
			}

		}, MetaNodeEntity.class, new Permission[] { ExtendedPermission.READ }, auth);
		if (!metaExpedientsPermesos.isEmpty()) {
			return conversioTipusHelper.convertirList(
					expedientRepository.findByEntitatAndMetaExpedientOrderByNomAsc(
							entitat,
							metaExpedientsPermesos,
							metaExpedient == null,
							metaExpedient),
					ExpedientSelectorDto.class);
		} else {
			return new ArrayList<ExpedientSelectorDto>();
		}
	}

	@Transactional(readOnly = true)
	@Override
	public List<Long> findIdsAmbFiltre(Long entitatId, ExpedientFiltreDto filtre) throws NotFoundException {
		logger.debug(
				"Consultant els ids d'expedient segons el filtre (" + "entitatId=" + entitatId + ", " + "filtre=" +
						filtre + ")");
		entityComprovarHelper.comprovarEntitat(entitatId, true, false, false);
		return findIdsAmbFiltrePaginat(entitatId, filtre, false, true);
	}

	@Transactional
	@Override
	public void agafarUser(Long entitatId, Long id) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug(
				"Agafant l'expedient com a usuari (" + "entitatId=" + entitatId + ", " + "id=" + id + ", " + "usuari=" +
						auth.getName() + ")");

		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				id,
				false,
				false,
				true,
				false,
				false);

		ExpedientEntity expedientSuperior = contingutHelper.getExpedientSuperior(expedient, false, false, false);
		if (expedientSuperior != null) {
			logger.error("No es pot agafar un expedient no arrel (id=" + id + ")");
			throw new ValidationException(id, ExpedientEntity.class, "No es pot agafar un expedient no arrel");
		}
		// Agafa l'expedient. Si l'expedient pertany a un altre usuari li pren
		UsuariEntity usuariOriginal = expedient.getAgafatPer();
		UsuariEntity usuariNou = usuariHelper.getUsuariAutenticat();
		expedient.updateAgafatPer(usuariNou);
		if (usuariOriginal != null) {
			// Avisa a l'usuari que li han pres
			emailHelper.contingutAgafatPerAltreUsusari(expedient, usuariOriginal, usuariNou);
		}
		contingutLogHelper.log(expedient, LogTipusEnumDto.AGAFAR, null, null, false, false);
	}

	@Transactional
	@Override
	public void agafarAdmin(Long entitatId, Long arxiuId, Long id, String usuariCodi) {
		logger.debug(
				"Agafant l'expedient com a administrador (" + "entitatId=" + entitatId + ", " + "arxiuId=" + arxiuId +
						", " + "id=" + id + ", " + "usuariCodi=" + usuariCodi + ")");
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				id,
				false,
				false,
				false,
				false,
				false);
		ExpedientEntity expedientSuperior = contingutHelper.getExpedientSuperior(expedient, false, false, false);
		if (expedientSuperior != null) {
			throw new ValidationException(id, ExpedientEntity.class, "No es pot agafar un expedient no arrel");
		}
		// Agafa l'expedient. Si l'expedient pertany a un altre usuari li pren
		UsuariEntity usuariOriginal = expedient.getAgafatPer();
		UsuariEntity usuariNou = usuariHelper.getUsuariAutenticat();
		expedient.updateAgafatPer(usuariNou);
		if (usuariOriginal != null) {
			// Avisa a l'altre l'usuari que li han pres
			emailHelper.contingutAgafatPerAltreUsusari(expedient, usuariOriginal, usuariNou);
		}
		contingutLogHelper.log(expedient, LogTipusEnumDto.AGAFAR, null, null, false, false);
	}

	@Transactional
	@Override
	public void alliberarUser(Long entitatId, Long id) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug(
				"Alliberant l'expedient com a usuari (" + "entitatId=" + entitatId + ", " + "id=" + id + ", " +
						"usuari=" + auth.getName() + ")");
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				id,
				true,
				false,
				false,
				false,
				false);
		expedient.updateAgafatPer(null);
		contingutLogHelper.log(expedient, LogTipusEnumDto.ALLIBERAR, null, null, false, false);
	}

	@Transactional
	@Override
	public void alliberarAdmin(Long entitatId, Long id) {
		logger.debug(
				"Alliberant l'expedient com a administrador (" + "entitatId=" + entitatId + ", " + "id=" + id + ")");
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				id,
				false,
				false,
				false,
				false,
				false);
		expedient.updateAgafatPer(null);
		contingutLogHelper.log(expedient, LogTipusEnumDto.ALLIBERAR, null, null, false, false);
	}

	@Transactional
	@Override
	public void tancar(Long entitatId, Long id, String motiu, Long[] documentsPerFirmar) {
		logger.debug(
				"Tancant l'expedient (" + "entitatId=" + entitatId + ", " + "id=" + id + "," + "motiu=" + motiu + ")");
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				id,
				true,
				false,
				true,
				false,
				false);
		boolean hiHaEsborranysPerFirmar = documentsPerFirmar != null && documentsPerFirmar.length > 0;
		if (!documentHelper.hasAnyDocumentDefinitiu(expedient) && !hiHaEsborranysPerFirmar) {
			throw new ExpedientTancarSenseDocumentsDefinitiusException();
		}
		expedient.updateEstat(ExpedientEstatEnumDto.TANCAT, motiu);
		expedient.updateExpedientEstat(null);
		contingutLogHelper.log(expedient, LogTipusEnumDto.TANCAMENT, null, null, false, false);
		if (pluginHelper.isArxiuPluginActiu()) {
			List<DocumentEntity> esborranys = documentRepository.findByExpedientAndEstatAndEsborrat(
					expedient,
					DocumentEstatEnumDto.REDACCIO,
					0);
			// Firmam els documents seleccionats
			if (hiHaEsborranysPerFirmar) {
				for (Long documentPerFirmar : documentsPerFirmar) {
					DocumentEntity document = documentRepository.getOne(documentPerFirmar);
					if (document != null) {
						FitxerDto fitxer = documentHelper.getFitxerAssociat(document, null);
						ArxiuFirmaDto arxiuFirma = documentFirmaServidorFirma.firmar(document, fitxer, motiu);
						pluginHelper.arxiuDocumentGuardarFirmaCades(document, fitxer, Arrays.asList(arxiuFirma));
					} else {
						throw new NotFoundException(documentPerFirmar, DocumentEntity.class);
					}
				}
			}
			// Eliminam de l'expedient els esborranys que no s'han firmat
			for (DocumentEntity esborrany : esborranys) {
				boolean trobat = false;
				for (Long documentPerFirmarId : documentsPerFirmar) {
					if (documentPerFirmarId.longValue() == esborrany.getId().longValue()) {
						trobat = true;
						break;
					}
				}
				if (!trobat) {
					documentRepository.delete(esborrany);
				}
			}
			pluginHelper.arxiuExpedientTancar(expedient);
		}
	}

	@Transactional
	@Override
	public void reobrir(Long entitatId, Long id) {
		logger.debug("Reobrint l'expedient (" + "entitatId=" + entitatId + ", " + "id=" + id + ")");
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				id,
				true,
				false,
				true,
				false,
				false);
		expedient.updateEstat(ExpedientEstatEnumDto.OBERT, null);
		contingutLogHelper.log(expedient, LogTipusEnumDto.REOBERTURA, null, null, false, false);
		if (pluginHelper.isArxiuPluginActiu()) {
			pluginHelper.arxiuExpedientReobrir(expedient);
		}
	}

	@Transactional
	@Override
	@SuppressWarnings("serial")
	public void relacioCreate(Long entitatId, final Long id, final Long relacionatId) {
		logger.debug(
				"Relacionant l'expedient (" + "entitatId=" + entitatId + ", " + "id=" + id + ", " + "relacionatId=" +
						relacionatId + ")");
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				id,
				true,
				false,
				true,
				false,
				false);
		ExpedientEntity toRelate = entityComprovarHelper.comprovarExpedient(
				entitatId,
				relacionatId,
				true,
				false,
				true,
				false,
				false);

		boolean alreadyRelatedTo = false;
		for (ExpedientEntity relacionatPer : toRelate.getRelacionatsAmb()) {
			if (relacionatPer.getId().equals(expedient.getId())) {
				alreadyRelatedTo = true;
			}
		}
		// checking if inverse relation doesnt already exist
		if (alreadyRelatedTo) {
			throw new ValidationException("Expedient ja relacionat");
		}

		expedient.addRelacionat(toRelate);
		contingutLogHelper.log(expedient, LogTipusEnumDto.MODIFICACIO, new Persistable<String>() {

			@Override
			public String getId() {
				return id + "#" + relacionatId;
			}

			@Override
			public boolean isNew() {
				return false;
			}

		},
				LogObjecteTipusEnumDto.RELACIO,
				LogTipusEnumDto.CREACIO,
				id.toString(),
				relacionatId.toString(),
				false,
				false);
	}

	@Transactional
	@Override
	@SuppressWarnings("serial")
	public boolean relacioDelete(Long entitatId, final Long id, final Long relacionatId) {
		logger.debug(
				"Esborrant la relació de l'expedient amb un altre expedient (" + "entitatId=" + entitatId + ", " +
						"id=" + id + ", " + "relacionatId=" + relacionatId + ")");
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				id,
				true,
				false,
				true,
				false,
				false);
		ExpedientEntity relacionat = entityComprovarHelper.comprovarExpedient(
				entitatId,
				relacionatId,
				true,
				false,
				true,
				false,
				false);
		boolean trobat = true;
		if (expedient.getRelacionatsAmb().contains(relacionat)) {
			expedient.removeRelacionat(relacionat);
		} else if (relacionat.getRelacionatsAmb().contains(expedient)) {
			relacionat.removeRelacionat(expedient);
		} else {
			trobat = false;
		}
		if (trobat) {
			contingutLogHelper.log(expedient, LogTipusEnumDto.MODIFICACIO, new Persistable<String>() {

				@Override
				public String getId() {
					return id + "#" + relacionatId;
				}

				@Override
				public boolean isNew() {
					return false;
				}

			},
					LogObjecteTipusEnumDto.RELACIO,
					LogTipusEnumDto.ELIMINACIO,
					id.toString(),
					relacionatId.toString(),
					false,
					false);
		}
		return trobat;
	}

	@Transactional(readOnly = true)
	@Override
	public List<ExpedientDto> relacioFindAmbExpedient(Long entitatId, Long expedientId) {
		logger.debug(
				"Obtenint la llista d'expedients relacionats (" + "entitatId=" + entitatId + ", " + "expedientId=" +
						expedientId + ")");
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				expedientId,
				false,
				true,
				false,
				false,
				false);
		List<ExpedientEntity> relacionats = new ArrayList<ExpedientEntity>();
		relacionats.addAll(expedient.getRelacionatsAmb());
		relacionats.addAll(expedient.getRelacionatsPer());
		Collections.sort(relacionats, new Comparator<ExpedientEntity>() {

			@Override
			public int compare(ExpedientEntity e1, ExpedientEntity e2) {
				return e1.getNom().compareTo(e2.getNom());
			}

		});
		List<ExpedientDto> relacionatsDto = new ArrayList<ExpedientDto>();
		for (ExpedientEntity e : relacionats)
			relacionatsDto.add(toExpedientDto(e, false));
		return relacionatsDto;
	}

	@Transactional(readOnly = true)
	@Override
	public FitxerDto exportacio(
			Long entitatId,
			Long metaExpedientId,
			Collection<Long> expedientIds,
			String format) throws IOException {
		logger.debug(
				"Exportant informació dels expedients (" + "entitatId=" + entitatId + ", " + "metaExpedientId=" +
						metaExpedientId + ", " + "expedientIds=" + expedientIds + ", " + "format=" + format + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, true, false, false);
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
				entitat,
				metaExpedientId,
				true,
				false,
				false,
				false);
		List<ExpedientEntity> expedients = expedientRepository.findByEntitatAndAndMetaNodeAndIdInOrderByIdAsc(
				metaExpedient.getEntitat(),
				metaExpedient,
				expedientIds);
		List<MetaDadaEntity> metaDades = dadaRepository.findDistinctMetaDadaByNodeIdInOrderByMetaDadaCodiAsc(
				expedientIds);
		List<DadaEntity> dades = dadaRepository.findByNodeIdInOrderByNodeIdAscMetaDadaCodiAsc(expedientIds);
		int numColumnes = 5 + metaDades.size();
		String[] columnes = new String[numColumnes];
		columnes[0] = messageHelper.getMessage("expedient.service.exportacio.numero");
		columnes[1] = messageHelper.getMessage("expedient.service.exportacio.titol");
		columnes[2] = messageHelper.getMessage("expedient.service.exportacio.estat");
		columnes[3] = messageHelper.getMessage("expedient.service.exportacio.datcre");
		columnes[4] = messageHelper.getMessage("expedient.service.exportacio.idnti");
		for (int i = 0; i < metaDades.size(); i++) {
			MetaDadaEntity metaDada = metaDades.get(i);
			columnes[5 + i] = metaDada.getNom() + " (" + metaDada.getCodi() + ")";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		List<String[]> files = new ArrayList<String[]>();
		int dadesIndex = 0;
		for (ExpedientEntity expedient : expedients) {
			String[] fila = new String[numColumnes];
			fila[0] = expedientHelper.calcularNumero(expedient);
			fila[1] = expedient.getNom();
			fila[2] = expedient.getEstat().name();
			fila[3] = sdf.format(expedient.getCreatedDate().toDate());
			fila[4] = expedient.getNtiIdentificador();
			if (!dades.isEmpty()) {
				DadaEntity dadaActual = dades.get(dadesIndex);
				if (dadaActual.getNode().getId().equals(expedient.getId())) {
					for (int i = 0; i < metaDades.size(); i++) {
						MetaDadaEntity metaDada = metaDades.get(i);
						int dadesIndexIncrement = 1;
						while (dadaActual.getNode().getId().equals(expedient.getId())) {
							if (dadaActual.getMetaDada().getCodi().equals(metaDada.getCodi())) {
								break;
							}
							dadaActual = dades.get(dadesIndex + dadesIndexIncrement++);
						}
						if (dadaActual.getMetaDada().getCodi().equals(metaDada.getCodi())) {
							fila[5 + i] = dadaActual.getValorComString();
						}
					}
				}
			}
			files.add(fila);
		}
		FitxerDto fitxer = new FitxerDto();
		if ("ODS".equalsIgnoreCase(format)) {
			Object[][] filesArray = files.toArray(new Object[files.size()][numColumnes]);
			TableModel model = new DefaultTableModel(filesArray, columnes);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			SpreadSheet.createEmpty(model).getPackage().save(baos);
			fitxer.setNom("exportacio.ods");
			fitxer.setContentType("application/vnd.oasis.opendocument.spreadsheet");
			fitxer.setContingut(baos.toByteArray());
		} else if ("CSV".equalsIgnoreCase(format)) {
			fitxer.setNom("exportacio.csv");
			fitxer.setContentType("text/csv");
			StringBuilder sb = new StringBuilder();
			csvHelper.afegirLinia(sb, columnes, ';');
			for (String[] fila : files) {
				csvHelper.afegirLinia(sb, fila, ';');
			}
			fitxer.setContingut(sb.toString().getBytes());
		} else {
			throw new ValidationException("Format de fitxer no suportat: " + format);
		}
		return fitxer;
	}

	@Override
	@Transactional
	public FitxerDto exportIndexExpedient(Long entitatId, Long expedientId) throws IOException {
		logger.debug(
				"Exportant índex de l'expedient (" + "entitatId=" + entitatId + ", " + "expedientId=" + expedientId +
						")");
		EntitatEntity entitatActual = entityComprovarHelper.comprovarEntitat(entitatId, true, false, false);
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				expedientId,
				false,
				true,
				false,
				false,
				false);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(baos);

		List<ContingutEntity> continguts = contingutRepository.findByPareAndEsborrat(
				expedient,
				0,
				new Sort("createdDate"));
		long num = 0;
		for (ContingutEntity contingut : continguts) {
			if (contingut instanceof DocumentEntity) {
				DocumentEntity document = (DocumentEntity)contingut;
				FitxerDto fitxer = documentHelper.getFitxerAssociat(document, null);
				String nomDocument = ((num += 10) / 10.0) + " " + fitxer.getNom();

				contingutHelper.crearNovaEntrada(nomDocument, fitxer, zos);
				if (document.isFirmat()) {
					String documentExportacioEni = pluginHelper.arxiuDocumentExportar(document);
					if (documentExportacioEni != null) {
						FitxerDto exportacioEni = new FitxerDto();
						exportacioEni.setNom("ENI_documents/" + nomDocument + "_exportacio_ENI.xml");
						exportacioEni.setContentType("application/xml");
						exportacioEni.setContingut(documentExportacioEni.getBytes());

						contingutHelper.crearNovaEntrada(exportacioEni.getNom(), exportacioEni, zos);
					}
				}
			}
			if (contingut instanceof CarpetaEntity) {
				List<String> estructuraCarpetes = new ArrayList<String>();
				List<DocumentEntity> documentsCarpetaActual = new ArrayList<DocumentEntity>();
				ContingutEntity carpetaActual = contingut;
				while (carpetaActual instanceof CarpetaEntity) {
					boolean darreraCarpeta = true;
					estructuraCarpetes.add(carpetaActual.getNom());
					for (ContingutEntity contingutCarpetaActual : carpetaActual.getFills()) {
						if (contingutCarpetaActual instanceof CarpetaEntity) {
							carpetaActual = contingutCarpetaActual;
							darreraCarpeta = false;
						} else {
							documentsCarpetaActual.add((DocumentEntity)contingutCarpetaActual);
						}
					}
					String nomEstructuraCarpetes = "";
					for (String carpeta : estructuraCarpetes) {
						nomEstructuraCarpetes += carpeta + "/";
					}
					for (DocumentEntity document : documentsCarpetaActual) {
						FitxerDto fitxer = documentHelper.getFitxerAssociat(document, null);
						num += (document.getNom() == documentsCarpetaActual.get(0).getNom()) ? 10 : 1; // primer
																										// document
						String nomDocument = (num / 10.0) + " " + fitxer.getNom();
						String nomCarpeta = nomEstructuraCarpetes + nomDocument;
						contingutHelper.crearNovaEntrada(nomCarpeta, fitxer, zos);
						if (document.isFirmat()) {
							String documentExportacioEni = pluginHelper.arxiuDocumentExportar(document);
							if (documentExportacioEni != null) {
								FitxerDto exportacioEni = new FitxerDto();
								exportacioEni.setNom("ENI_documents/" + nomDocument + "_exportacio_ENI.xml");
								exportacioEni.setContentType("application/xml");
								exportacioEni.setContingut(documentExportacioEni.getBytes());

								contingutHelper.crearNovaEntrada(exportacioEni.getNom(), exportacioEni, zos);
							}
						}
					}
					documentsCarpetaActual = new ArrayList<DocumentEntity>();
					if (darreraCarpeta)
						break;
				}
			}
		}
		String expedientExportacioEni = pluginHelper.arxiuExpedientExportar(expedient);
		if (expedientExportacioEni != null) {
			FitxerDto exportacioEni = new FitxerDto();
			exportacioEni.setNom(expedient.getNom() + "_exportacio_ENI.xml");
			exportacioEni.setContentType("application/xml");
			exportacioEni.setContingut(expedientExportacioEni.getBytes());
			contingutHelper.crearNovaEntrada(exportacioEni.getNom(), exportacioEni, zos);
		}

		FitxerDto indexDoc = contingutHelper.generarIndex(entitatActual, expedient);
		contingutHelper.crearNovaEntrada(indexDoc.getNom(), indexDoc, zos);
		FitxerDto indexPdf = pluginHelper.conversioConvertirPdf(indexDoc, null);
		contingutHelper.crearNovaEntrada(indexPdf.getNom(), indexPdf, zos);
		zos.close();

		FitxerDto resultat = new FitxerDto();
		resultat.setNom(
				messageHelper.getMessage("expedient.service.exportacio.index") + " " + expedient.getNom() + ".zip");
		resultat.setContentType("application/zip");
		resultat.setContingut(baos.toByteArray());

		return resultat;
	}

	@Override
	@Transactional
	public FitxerDto exportIndexExpedients(Long entitatId, Collection<Long> expedientIds) throws IOException {
		logger.debug(
				"Exportant índex dels expedients seleccionats (" + "entitatId=" + entitatId + ", " + "expedientIds=" +
						expedientIds + ")");
		entityComprovarHelper.comprovarEntitat(entitatId, true, false, false);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(baos);

		for (Long expedientId : expedientIds) {
			FitxerDto resultat = exportIndexExpedient(entitatId, expedientId);
			contingutHelper.crearNovaEntrada(resultat.getNom(), resultat, zos);
		}
		zos.close();
		FitxerDto resultat = new FitxerDto();
		resultat.setNom(messageHelper.getMessage("expedient.service.exportacio.index") + ".zip");
		resultat.setContentType("application/zip");
		resultat.setContingut(baos.toByteArray());

		return resultat;
	}

	private PaginaDto<ExpedientDto> findAmbFiltrePaginat(
			Long entitatId,
			ExpedientFiltreDto filtre,
			PaginacioParamsDto paginacioParams,
			boolean accesAdmin,
			boolean comprovarAccesMetaExpedients) {
		return findAmbFiltrePaginat(entitatId, filtre, paginacioParams, null);
	}

	private PaginaDto<ExpedientDto> findAmbFiltrePaginat(
			Long entitatId,
			ExpedientFiltreDto filtre,
			PaginacioParamsDto paginacioParams,
			Long expedientId) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, true, false, false);
		MetaExpedientEntity metaExpedient = null;

		if (filtre.getMetaExpedientId() != null) {
			metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
					entitat,
					filtre.getMetaExpedientId(),
					true,
					false,
					false,
					false);
		}

		List<MetaExpedientEntity> metaExpedientsPermesos;
		if (filtre.getOrganGestorId() != null) {
			metaExpedientsPermesos = metaExpedientHelper.findAmbOrganGestorPermis(
					entitatId,
					filtre.getOrganGestorId(),
					new Permission[] { ExtendedPermission.READ },
					false);
					new Permission[] {ExtendedPermission.READ},
					false,
					null);
		} else {
			metaExpedientsPermesos = metaExpedientHelper.findAmbEntitatPermis(
					entitatId,
					new Permission[] { ExtendedPermission.READ },
					false);
					entitatId, 
					new Permission[] {ExtendedPermission.READ},
					false,
					null);
		}

		if (!metaExpedientsPermesos.isEmpty()) {

			UsuariEntity agafatPer = null;
			if (filtre.isMeusExpedients()) {
				agafatPer = usuariHelper.getUsuariAutenticat();
			}

			// estats
			ExpedientEstatEnumDto chosenEstatEnum = null;
			ExpedientEstatEntity chosenEstat = null;
			Long estatId = filtre.getExpedientEstatId();
			if (estatId != null) {
				if (estatId.intValue() <= 0) { // if estat is 0 or less the given estat is enum
					int estatIdInt = -estatId.intValue();
					chosenEstatEnum = ExpedientEstatEnumDto.values()[estatIdInt];
				} else { // given estat is estat from database
					chosenEstat = expedientEstatRepository.findOne(estatId);
				}
			}

			// relacionar expedient view
			List<ExpedientEntity> expedientsToBeExluded;
			boolean esNullExpedientsToBeExcluded = false;
			if (expedientId != null) {
				expedientsToBeExluded = new ArrayList<>();
				// expedient for which "Relacionar expedient" view is shown
				ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
						entitatId,
						expedientId,
						false,
						false,
						true,
						false,
						false);
				expedientsToBeExluded.addAll(expedient.getRelacionatsAmb());
				expedientsToBeExluded.addAll(expedient.getRelacionatsPer());
				expedientsToBeExluded.add(expedient);
			} else {
				esNullExpedientsToBeExcluded = true;
				expedientsToBeExluded = null; // repository does not accept empty list but it accepts null value
			}

			boolean esNullRolsCurrentUser = false;
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			List<String> rolsCurrentUser = new ArrayList<String>();
			for (GrantedAuthority ga : auth.getAuthorities())
				rolsCurrentUser.add(ga.getAuthority());
			if (rolsCurrentUser.isEmpty()) {
				rolsCurrentUser = null; // repository does not accept empty list but it accepts null value
				esNullRolsCurrentUser = true;
			}

			Page<ExpedientEntity> paginaExpedients;
			Map<String, String[]> ordenacioMap = new HashMap<String, String[]>();
			ordenacioMap.put("numero", new String[] { "codi", "any", "sequencia" });

			paginaExpedients = expedientRepository.findByEntitatAndFiltre(
					entitat,
					metaExpedientsPermesos,
					metaExpedient == null,
					metaExpedient,
					filtre.getNumero() == null || "".equals(filtre.getNumero().trim()),
					filtre.getNumero() == null ? "" : filtre.getNumero(),
					filtre.getNom() == null || filtre.getNom().isEmpty(),
					filtre.getNom() == null ? "" : filtre.getNom(),
					filtre.getDataCreacioInici() == null,
					filtre.getDataCreacioInici(),
					filtre.getDataCreacioFi() == null,
					DateHelper.toDateFinalDia(filtre.getDataCreacioFi()),
					filtre.getDataTancatInici() == null,
					filtre.getDataTancatInici(),
					filtre.getDataTancatFi() == null,
					filtre.getDataTancatFi(),
					chosenEstatEnum == null,
					chosenEstatEnum,
					chosenEstat == null,
					chosenEstat,
					agafatPer == null,
					agafatPer,
					filtre.getSearch() == null,
					filtre.getSearch() == null ? "" : filtre.getSearch(),
					filtre.getTipusId() == null,
					filtre.getTipusId(),
					esNullExpedientsToBeExcluded,
					expedientsToBeExluded,
					filtre.getInteressat() == null || filtre.getInteressat().isEmpty(),
					filtre.getInteressat(),
					filtre.getMetaExpedientDominiValor() == null || filtre.getMetaExpedientDominiValor().isEmpty(),
					filtre.getMetaExpedientDominiValor(),
					esNullRolsCurrentUser,
					rolsCurrentUser,
					paginacioHelper.toSpringDataPageable(paginacioParams, ordenacioMap));

			PaginaDto<ExpedientDto> result = paginacioHelper.toPaginaDto(
					paginaExpedients,
					ExpedientDto.class,
					new Converter<ExpedientEntity, ExpedientDto>() {

						@Override
						public ExpedientDto convert(ExpedientEntity source) {
							return toExpedientDto(source, true);
						}

					});
			for (ExpedientDto expedient : result) {
				boolean enAlerta = alertaRepository.countByLlegidaAndContingutId(false, expedient.getId()) > 0;
				expedient.setAlerta(enAlerta);
			}
			return result;
		} else {
			return paginacioHelper.getPaginaDtoBuida(ExpedientDto.class);
		}
	}

	private List<Long> findIdsAmbFiltrePaginat(
			Long entitatId,
			ExpedientFiltreDto filtre,
			boolean accesAdmin,
			boolean comprovarAccesMetaExpedients) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, (!accesAdmin), accesAdmin, false);
		MetaExpedientEntity metaExpedient = null;
		if (filtre.getMetaExpedientId() != null) {
			metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
					entitat,
					filtre.getMetaExpedientId(),
					true,
					false,
					false,
					false);
		}

		List<MetaExpedientEntity> metaExpedientsPermesos = metaExpedientRepository.findByEntitatOrderByNomAsc(entitat);
		if (comprovarAccesMetaExpedients) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			permisosHelper.filterGrantedAll(
					metaExpedientsPermesos,
					new ObjectIdentifierExtractor<MetaExpedientEntity>() {

						@Override
						public Long getObjectIdentifier(MetaExpedientEntity metaExpedient) {
							return metaExpedient.getId();
						}

					},
					MetaNodeEntity.class,
					new Permission[] { ExtendedPermission.READ },
					auth);
		}
		if (!metaExpedientsPermesos.isEmpty()) {
			return expedientRepository.findIdByEntitatAndFiltre(
					entitat,
					metaExpedientsPermesos,
					metaExpedient == null,
					metaExpedient,
					filtre.getNumero() == null || "".equals(filtre.getNumero().trim()),
					filtre.getNumero(),
					filtre.getNom() == null || filtre.getNom().isEmpty(),
					filtre.getNom(),
					filtre.getDataCreacioInici() == null,
					filtre.getDataCreacioInici(),
					filtre.getDataCreacioFi() == null,
					filtre.getDataCreacioFi(),
					filtre.getDataTancatInici() == null,
					filtre.getDataTancatInici(),
					filtre.getDataTancatFi() == null,
					filtre.getDataTancatFi(),
					filtre.getEstat() == null,
					filtre.getEstat(),
					filtre.getInteressat() == null || filtre.getInteressat().isEmpty(),
					filtre.getInteressat());
		} else {
			return new ArrayList<Long>();
		}
	}

	private ExpedientDto toExpedientDto(ExpedientEntity expedient, boolean ambPathIPermisos) {
		ExpedientDto expedientDto = (ExpedientDto)contingutHelper.toContingutDto(
				expedient,
				ambPathIPermisos,
				false,
				false,
				false,
				ambPathIPermisos,
				false,
				false);

		return expedientDto;
	}

	private static final Logger logger = LoggerFactory.getLogger(ExpedientServiceImpl.class);

}
