/**
 * 
 */
package es.caib.ripea.core.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
import org.springframework.data.domain.Pageable;
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
import es.caib.ripea.core.api.dto.ContingutMassiuFiltreDto;
import es.caib.ripea.core.api.dto.DocumentDto;
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
import es.caib.ripea.core.api.dto.PermissionEnumDto;
import es.caib.ripea.core.api.dto.UsuariDto;
import es.caib.ripea.core.api.exception.DocumentAlreadyImportedException;
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
import es.caib.ripea.core.entity.MetaExpedientOrganGestorEntity;
import es.caib.ripea.core.entity.MetaNodeEntity;
import es.caib.ripea.core.entity.OrganGestorEntity;
import es.caib.ripea.core.entity.RegistreAnnexEntity;
import es.caib.ripea.core.entity.UsuariEntity;
import es.caib.ripea.core.firma.DocumentFirmaServidorFirma;
import es.caib.ripea.core.helper.CacheHelper;
import es.caib.ripea.core.helper.ContingutHelper;
import es.caib.ripea.core.helper.ContingutLogHelper;
import es.caib.ripea.core.helper.ConversioTipusHelper;
import es.caib.ripea.core.helper.CsvHelper;
import es.caib.ripea.core.helper.DateHelper;
import es.caib.ripea.core.helper.DistribucioHelper;
import es.caib.ripea.core.helper.DocumentHelper;
import es.caib.ripea.core.helper.EntityComprovarHelper;
import es.caib.ripea.core.helper.ExpedientHelper;
import es.caib.ripea.core.helper.ExpedientPeticioHelper;
import es.caib.ripea.core.helper.MessageHelper;
import es.caib.ripea.core.helper.MetaExpedientHelper;
import es.caib.ripea.core.helper.OrganGestorHelper;
import es.caib.ripea.core.helper.PaginacioHelper;
import es.caib.ripea.core.helper.PaginacioHelper.Converter;
import es.caib.ripea.core.helper.PermisosHelper;
import es.caib.ripea.core.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.ripea.core.helper.PluginHelper;
import es.caib.ripea.core.helper.PropertiesHelper;
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
import es.caib.ripea.core.repository.UsuariRepository;
import es.caib.ripea.core.security.ExtendedPermission;

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
	private DocumentHelper documentHelper;
	@Autowired
	private MetaExpedientHelper metaExpedientHelper;
	@Autowired
	private DocumentFirmaServidorFirma documentFirmaServidorFirma;
	@Autowired
	private ContingutLogHelper contingutLogHelper;
	@Autowired
	private UsuariRepository usuariRepository;
	@Autowired
	private CacheHelper cacheHelper;
	@Autowired
	private OrganGestorHelper organGestorHelper;
	
	public static List<DocumentDto> expedientsWithImportacio = new ArrayList<DocumentDto>();

	@Transactional
	@Override
	public ExpedientDto create (
			Long entitatId,
			Long metaExpedientId,
			Long metaExpedientDominiId,
			Long organGestorId,
			Long pareId,
			Integer any,
			Long sequencia,
			String nom,
			Long expedientPeticioId,
			boolean associarInteressats,
			Long grupId) {
		logger.debug(
				"Creant nou expedient (" +
						"entitatId=" + entitatId + ", " +
						"metaExpedientId=" + metaExpedientId + ", " +
						"metaExpedientDominiId=" + metaExpedientDominiId + ", " +
						"organGestorId=" + organGestorId + ", " +
						"pareId=" + pareId + ", " +
						"any=" + any + ", " +
						"sequencia=" + sequencia + ", " +
						"nom=" + nom + ", " +
						"expedientPeticioId=" + expedientPeticioId + ")");
		// if expedient comes from distribucio
		ExpedientPeticioEntity expedientPeticioEntity = null;
		if (expedientPeticioId != null) {
			expedientPeticioEntity = expedientPeticioRepository.findOne(expedientPeticioId);
		}
		// create expedient in db 
		ExpedientEntity expedient = expedientHelper.create(
				entitatId,
				metaExpedientId,
				metaExpedientDominiId,
				organGestorId,
				pareId,
				any,
				sequencia,
				nom,
				expedientPeticioId,
				associarInteressats,
				grupId);
		ExpedientDto expedientDto = toExpedientDto(expedient, true);
		//create expedient in arxiu
		contingutHelper.arxiuPropagarModificacio(
				expedient,
				null,
				false,
				false,
				null);
		boolean processatOk = true;
		// if expedient comes from distribucio
		if (expedientPeticioId != null) {
			expedientHelper.inicialitzarExpedientsWithImportacio();
			for (RegistreAnnexEntity registeAnnexEntity : expedientPeticioEntity.getRegistre().getAnnexos()) {
				try {
					expedientHelper.crearDocFromAnnex(
							expedient.getId(),
							registeAnnexEntity.getId(),
							expedientPeticioEntity);
				} catch (Exception e) {
					processatOk = false;
					logger.info(ExceptionUtils.getStackTrace(e));
					expedientHelper.updateRegistreAnnexError(
							registeAnnexEntity.getId(),
							ExceptionUtils.getStackTrace(e));

				}
			}
			String arxiuUuid = expedientPeticioEntity.getRegistre().getJustificantArxiuUuid();
			if (arxiuUuid != null && isIncorporacioJustificantActiva()) {
				try {
					expedientPeticioEntity = expedientPeticioRepository.findOne(expedientPeticioId);
					expedientHelper.crearDocFromUuid(
							expedient.getId(),
							arxiuUuid, 
							expedientPeticioEntity);
				} catch (Exception e) {
					processatOk = false;
					logger.info(ExceptionUtils.getStackTrace(e));
				}
				
			}
			if (!expedientHelper.consultaExpedientsAmbImportacio().isEmpty() && ! isIncorporacioDuplicadaPermesa()) {
				throw new DocumentAlreadyImportedException();
			}
			canviEstatToProcessatPendent(expedientPeticioEntity);
			if (processatOk) {
				notificarICanviEstatToProcessatNotificat(expedientPeticioEntity.getId());
			}
		}
		expedientDto.setProcessatOk(processatOk);
		return expedientDto;
	}

	@Transactional
	@Override
	public boolean incorporar(Long entitatId, Long expedientId, Long expedientPeticioId, boolean associarInteressats) {
		logger.debug("Incorporant a l'expedient existent (" +
				"entitatId=" + entitatId + ", " +
				"expedientId=" + expedientId + ", " +
				"expedientPeticioId=" + expedientPeticioId + ")");
		ExpedientPeticioEntity expedientPeticioEntity = expedientPeticioRepository.findOne(expedientPeticioId);
		expedientHelper.relateExpedientWithPeticioAndSetAnnexosPendentNewTransaction(expedientPeticioId, expedientId);
		expedientHelper.associateInteressats(expedientId, entitatId, expedientPeticioId, PermissionEnumDto.WRITE);
		expedientHelper.inicialitzarExpedientsWithImportacio();
		boolean processatOk = true;
		for (RegistreAnnexEntity registeAnnexEntity : expedientPeticioEntity.getRegistre().getAnnexos()) {
			try {
				boolean throwException1 = false;
				if (throwException1)
					throw new RuntimeException("EXCEPION BEFORE INCORPORAR !!!!!! ");
				expedientHelper.crearDocFromAnnex(
						expedientId,
						registeAnnexEntity.getId(),
						expedientPeticioEntity);	
			} catch (Exception e) {
				processatOk = false;
				logger.error(ExceptionUtils.getStackTrace(e));
				expedientHelper.updateRegistreAnnexError(registeAnnexEntity.getId(), ExceptionUtils.getStackTrace(e));
			}
		}
		String arxiuUuid = expedientPeticioEntity.getRegistre().getJustificantArxiuUuid();
		if (arxiuUuid != null && isIncorporacioJustificantActiva()) {
			try {
				expedientHelper.crearDocFromUuid(
						expedientId,
						arxiuUuid, 
						expedientPeticioEntity);
			} catch (Exception e) {
				logger.error(ExceptionUtils.getStackTrace(e));
			}
		}
		if (!expedientHelper.consultaExpedientsAmbImportacio().isEmpty() && ! isIncorporacioDuplicadaPermesa()) {
			throw new DocumentAlreadyImportedException();
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
	
	@Override
	public List<DocumentDto> consultaExpedientsAmbImportacio() {
		return expedientHelper.consultaExpedientsAmbImportacio();
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

	@Transactional
	@Override
	public boolean retryCreateDocFromAnnex(Long registreAnnexId, Long expedientPeticioId) {
		boolean processatOk = true;
		try {
			ExpedientPeticioEntity expedientPeticioEntity = expedientPeticioRepository.findOne(expedientPeticioId);
			expedientHelper.crearDocFromAnnex(expedientPeticioEntity.getExpedient().getId(), registreAnnexId, expedientPeticioEntity);

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
		contingutHelper.comprovarContingutDinsExpedientModificable(entitatId, id, false, true, false, false, false);
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				id,
				false,
				false,
				true,
				false,
				false, 
				false);

		expedientHelper.updateNomExpedient(expedient, nom);
		ExpedientDto dto = toExpedientDto(expedient, true);
		contingutHelper.arxiuPropagarModificacio(expedient, null, false, false, null);
		return dto;
	}

	@Transactional
	@Override
	public ExpedientDto update(Long entitatId, Long id, String nom, int any, Long metaExpedientDominiId, Long organGestorId) {
		logger.debug(
				"Actualitzant dades de l'expedient (" + "entitatId=" + entitatId + ", " + "id=" + id + ", " + "nom=" +
						nom + ")");
		contingutHelper.comprovarContingutDinsExpedientModificable(entitatId, id, false, true, false, false, false);
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				id,
				false,
				false,
				true,
				false,
				false, 
				false);
		entityComprovarHelper.comprovarEstatExpedient(entitatId, id, ExpedientEstatEnumDto.OBERT);
		expedientHelper.updateNomExpedient(expedient, nom);
		expedientHelper.updateAnyExpedient(expedient, any);
		expedientHelper.updateOrganGestor(expedient, organGestorId);
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
				false, false);
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
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false, true, false);
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedientPerExpedient(
				entitat,
				metaExpedientId,
				false,
				false,
				true,
				false, 
				false);

		ContingutEntity contingutPare = null;
		if (pareId != null) {
			contingutPare = contingutHelper.comprovarContingutDinsExpedientModificable(
					entitatId,
					pareId,
					false,
					false,
					true,
					false, false);
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
		entityComprovarHelper.comprovarEntitat(entitatId, false, false, true, false, false);
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				expedientId,
				false,
				false,
				true,
				false,
				false, false);
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
		entityComprovarHelper.comprovarEntitat(entitatId, false, false, true, false, false);
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				expedientId,
				false,
				true,
				false,
				false,
				false, false);

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

		if (!granted && expedient.getExpedientEstat() != null && entityComprovarHelper.hasEstatWritePermissons(expedient.getExpedientEstat().getId())) {
			granted = true;
		}
		return granted;
	}

	@Transactional(readOnly = true)
	@Override
	public PaginaDto<ExpedientDto> findAmbFiltreUser(
			Long entitatId,
			ExpedientFiltreDto filtre,
			PaginacioParamsDto paginacioParams, 
			String rolActual) {
		logger.debug(
				"Consultant els expedients segons el filtre per usuaris (" + "entitatId=" + entitatId + ", " +
						"filtre=" + filtre + ", " + "paginacioParams=" + paginacioParams + ")");
		entityComprovarHelper.comprovarEntitat(entitatId, false, false, false, true, false);
		return findAmbFiltrePaginat(entitatId, filtre, paginacioParams, null, rolActual);
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
		entityComprovarHelper.comprovarEntitat(entitatId, false, false, false, true, false);
		return findAmbFiltrePaginat(entitatId, filtre, paginacioParams, expedientId, "tothom");
	}

	@Transactional
	@Override
	public List<ExpedientDto> findByEntitatAndMetaExpedient(Long entitatId, Long metaExpedientId) {
		logger.debug(
				"Consultant els expedients(" + "entitatId=" + entitatId + ", " + "metaExpedientId=" + metaExpedientId +
						")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, true, false, false, false, false);
		MetaExpedientEntity metaExpedient = null;
		if (metaExpedientId != null) {
			metaExpedient = entityComprovarHelper.comprovarMetaExpedientPerExpedient(
					entitat,
					metaExpedientId,
					true,
					false,
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
			for (ContingutEntity cont : expedientsEnt) {
				ExpedientEntity exp = (ExpedientEntity)cont;
				ExpedientDto expedient = new ExpedientDto();
				expedient.setId(exp.getId());
				expedient.setNom(exp.getNom());
				expedient.setAgafatPer(
						conversioTipusHelper.convertir(
								exp.getAgafatPer(),
								UsuariDto.class));
				expedientsDto.add(expedient);
			}
		} else { // if not add only expedients having estat with permisions
			for (ContingutEntity cont : expedientsEnt) {
				ExpedientEntity exp = (ExpedientEntity)cont;
				if (exp.getExpedientEstat() != null &&
						entityComprovarHelper.hasEstatWritePermissons(exp.getExpedientEstat().getId())) {
					ExpedientDto expedient = new ExpedientDto();
					expedient.setId(exp.getId());
					expedient.setNom(exp.getNom());
					expedient.setAgafatPer(
							conversioTipusHelper.convertir(
									exp.getAgafatPer(),
									UsuariDto.class));
					expedientsDto.add(expedient);

				}
			}
		}
		return expedientsDto;
	}

	@Transactional(readOnly = true)
	@Override
	public List<ExpedientSelectorDto> findPerUserAndTipus(Long entitatId, Long metaExpedientId, boolean checkPerMassiuAdmin) {
		logger.debug(
				"Consultant els expedients segons el tipus per usuaris (" + "entitatId=" + entitatId + ", " +
						"metaExpedientId=" + metaExpedientId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false, true, false);
		MetaExpedientEntity metaExpedient = null;
		if (metaExpedientId != null) {
			metaExpedient = entityComprovarHelper.comprovarMetaExpedientPerExpedient(
					entitat,
					metaExpedientId,
					false,
					true,
					false,
					false, 
					checkPerMassiuAdmin);
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
		entityComprovarHelper.comprovarEntitat(entitatId, true, false, false, false, false);
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
				false, false);
		expedientHelper.agafar(expedient, usuariHelper.getUsuariAutenticat().getCodi());
	}

	@Transactional
	@Override
	public void assignar(
			Long entitatId,
			Long expedientId,
			String usuariCodi) {
		logger.debug("Assignant l'expedient (" + "entitatId=" + entitatId + ", " + "expedientId=" + expedientId + ", " + "usuari=" + usuariCodi + ")");
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				expedientId,
				false,
				false,
				false,
				false,
				false, 
				false);
		
		entityComprovarHelper.comprovarPermisMetaNode(
				expedient.getMetaExpedient(),
				expedient.getId(),
				true,
				ExtendedPermission.WRITE,
				"WRITE",
				usuariCodi, 
				true);
		
		expedientHelper.agafar(expedient, usuariCodi);
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
				false, false);
		

		expedientHelper.agafar(expedient, usuariCodi);
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
				false, false);
		expedientHelper.alliberar(expedient);
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
				false, false);
		expedientHelper.alliberar(expedient);
	}

	@Transactional
	@Override
	public void tancar(Long entitatId, Long id, String motiu, Long[] documentsPerFirmar, boolean checkPerMassiuAdmin) {
		logger.debug(
				"Tancant l'expedient (" + "entitatId=" + entitatId + ", " + "id=" + id + "," + "motiu=" + motiu + ")");
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				id,
				false,
				true,
				false,
				false,
				false, 
				checkPerMassiuAdmin);
		if (!cacheHelper.findErrorsValidacioPerNode(expedient).isEmpty()) {
			throw new ValidationException("No es pot tancar un expedient amb errors de validació");
		}
		if (cacheHelper.hasNotificacionsPendentsPerExpedient(expedient)) {
			throw new ValidationException("No es pot tancar un expedient amb notificacions pendents");
		}
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
						documentFirmaServidorFirma.firmar(document, fitxer, motiu);
						//pluginHelper.arxiuDocumentGuardarFirmaCades(document, fitxer, Arrays.asList(arxiuFirma));
					} else {
						throw new NotFoundException(documentPerFirmar, DocumentEntity.class);
					}
				}
			}
			// Eliminam de l'expedient els esborranys que no s'han firmat
			for (DocumentEntity esborrany : esborranys) {
				boolean trobat = false;
				if (documentsPerFirmar != null) {
					for (Long documentPerFirmarId : documentsPerFirmar) {
						if (documentPerFirmarId.longValue() == esborrany.getId().longValue()) {
							trobat = true;
							break;
						}
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
				false, 
				false);
		entityComprovarHelper.comprovarEstatExpedient(entitatId, id, ExpedientEstatEnumDto.TANCAT);
		expedient.updateEstat(ExpedientEstatEnumDto.OBERT, null);
		contingutLogHelper.log(expedient, LogTipusEnumDto.REOBERTURA, null, null, false, false);
		if (pluginHelper.isArxiuPluginActiu()) {
			pluginHelper.arxiuExpedientReobrir(expedient);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public PaginaDto<ExpedientDto> findExpedientsPerTancamentMassiu(
			Long entitatId,
			ContingutMassiuFiltreDto filtre,
			PaginacioParamsDto paginacioParams, String rolActual) throws NotFoundException {
		

		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false, 
				true, false);
		
		boolean checkPerMassiuAdmin = false;
		boolean nomesAgafats = true;
		if (rolActual.equals("IPA_ADMIN") || rolActual.equals("IPA_ORGAN_ADMIN")) {
			nomesAgafats = false;
			checkPerMassiuAdmin = true;
		} 

		MetaExpedientEntity metaExpedient = null;
		if (filtre.getMetaExpedientId() != null) {
			metaExpedient = entityComprovarHelper.comprovarMetaExpedientPerExpedient(
					entitat,
					filtre.getMetaExpedientId(),
					true,
					false,
					false,
					false, 
					checkPerMassiuAdmin);
		}
		
		List<MetaExpedientEntity> metaExpedientsPermesos = metaExpedientHelper.findPermesosAccioMassiva(entitatId, rolActual);

		if (!metaExpedientsPermesos.isEmpty()) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			UsuariEntity usuariActual = usuariRepository.findOne(auth.getName());
			Date dataInici = DateHelper.toDateInicialDia(filtre.getDataInici());
			Date dataFi = DateHelper.toDateFinalDia(filtre.getDataFi());
			Page<ExpedientEntity> paginaDocuments = expedientRepository.findExpedientsPerTancamentMassiu(
					entitat,
					nomesAgafats,
					usuariActual,
					metaExpedientsPermesos, 
					metaExpedient == null,
					metaExpedient,
					filtre.getNom() == null,
					filtre.getNom(),
					dataInici == null,
					dataInici,
					dataFi == null,
					dataFi,
					paginacioHelper.toSpringDataPageable(paginacioParams));
			return paginacioHelper.toPaginaDto(
					paginaDocuments,
					ExpedientDto.class,
					new Converter<ExpedientEntity, ExpedientDto>() {
						@Override
						public ExpedientDto convert(ExpedientEntity source) {
							ExpedientDto dto = (ExpedientDto)contingutHelper.toContingutDto(
									source,
									false,
									false,
									false,
									false,
									true,
									true,
									false);
							return dto;
						}
					});
		} else {
			return paginacioHelper.getPaginaDtoBuida(
					ExpedientDto.class);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public List<Long> findIdsExpedientsPerTancamentMassiu(
			Long entitatId,
			ContingutMassiuFiltreDto filtre, String rolActual) throws NotFoundException {
		

		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false, false, false);
		MetaExpedientEntity metaExpedient = null;
		if (filtre.getMetaExpedientId() != null) {
			metaExpedient = entityComprovarHelper.comprovarMetaExpedientPerExpedient(
					entitat,
					filtre.getMetaExpedientId(),
					true,
					false,
					false,
					false, false);
		}
		List<MetaExpedientEntity> metaExpedientsPermesos = metaExpedientHelper.findPermesosAccioMassiva(entitatId, rolActual);

		boolean nomesAgafats = true;
		if (rolActual.equals("IPA_ADMIN") || rolActual.equals("IPA_ORGAN_ADMIN")) {
			nomesAgafats = false;
		} 

		if (!metaExpedientsPermesos.isEmpty()) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			UsuariEntity usuariActual = usuariRepository.findOne(auth.getName());
			Date dataInici = DateHelper.toDateInicialDia(filtre.getDataInici());
			Date dataFi = DateHelper.toDateFinalDia(filtre.getDataFi());
			List<Long> idsDocuments = expedientRepository.findIdsExpedientsPerTancamentMassiu(
					entitat,
					nomesAgafats,
					usuariActual,
					metaExpedientsPermesos,
					metaExpedient == null,
					metaExpedient,
					filtre.getNom() == null,
					filtre.getNom(),
					dataInici == null,
					dataInici,
					dataFi == null,
					dataFi);
			return idsDocuments;
		} else {
			return new ArrayList<>();
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
				false, false);
		ExpedientEntity toRelate = entityComprovarHelper.comprovarExpedient(
				entitatId,
				relacionatId,
				false,
				true,
				false,
				false,
				false, false);

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
		contingutLogHelper.log(
				expedient, 
				LogTipusEnumDto.MODIFICACIO, new Persistable<String>() {
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
		boolean isPropagarRelacioActiva = isProgaparRelacioActiva();
		if (pluginHelper.isArxiuPluginActiu() && isPropagarRelacioActiva) {
			pluginHelper.arxiuExpedientEnllacar(
					expedient, 
					toRelate);
		}
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
				false, false);
		ExpedientEntity relacionat = entityComprovarHelper.comprovarExpedient(
				entitatId,
				relacionatId,
				false,
				true,
				false,
				false,
				false, false);
		boolean trobat = true;
		if (expedient.getRelacionatsAmb().contains(relacionat)) {
			expedient.removeRelacionat(relacionat);
		} else if (relacionat.getRelacionatsAmb().contains(expedient)) {
			relacionat.removeRelacionat(expedient);
		} else {
			trobat = false;
		}
		if (trobat) {
			contingutLogHelper.log(
					expedient, 
					LogTipusEnumDto.MODIFICACIO, new Persistable<String>() {
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
		boolean isPropagarRelacioActiva = isProgaparRelacioActiva();
		if (pluginHelper.isArxiuPluginActiu() && isPropagarRelacioActiva) {
			try {
				//provar desenllaçar fill del pare des del pare
				pluginHelper.arxiuExpedientDesenllacar(
						expedient, 
						relacionat);
			} catch (Exception e) {
				logger.debug(e.getMessage());
				//provar desenllaçar fill del pare des del fill
				pluginHelper.arxiuExpedientDesenllacar(
						relacionat, 
						expedient);
			}
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
				false, false);
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
			Collection<Long> expedientIds,
			String format) throws IOException {
		logger.debug(
				"Exportant informació dels expedients (" + "entitatId=" + entitatId + ", " + "expedientIds=" + expedientIds + ", " + "format=" + format + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, true, false, false, false, false);
		List<Long> metaExpedientIds = metaExpedientRepository.findDistinctMetaExpedientIdsByExpedients(expedientIds);
		for (Long metaExpedientId : metaExpedientIds) {
			entityComprovarHelper.comprovarMetaExpedientPerExpedient(
					entitat,
					metaExpedientId,
					true,
					false,
					false,
					false, false);
		}
		List<ExpedientEntity> expedients = expedientRepository.findByEntitatAndIdInOrderByIdAsc(
				entitat,
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
			if (expedient.getExpedientEstat() != null && expedient.getEstat() != ExpedientEstatEnumDto.TANCAT) {
				fila[2] = expedient.getExpedientEstat().getNom();
			} else {
				fila[2] = expedient.getEstat().name();
			}
			fila[3] = sdf.format(expedient.getCreatedDate().toDate());
			fila[4] = expedient.getNtiIdentificador();
			if (!dades.isEmpty()) {
				DadaEntity dadaActual = dades.get(dadesIndex);
				if (dadaActual.getNode().getId().equals(expedient.getId())) {
					for (int i = 0; i < metaDades.size(); i++) {
						MetaDadaEntity metaDada = metaDades.get(i); 
						int dadesIndexIncrement = 0;
						while (dadaActual.getNode().getId().equals(expedient.getId())) {
							
							if (dadaActual.getMetaDada().getId().equals(metaDada.getId())) {
 								break;
							}
							
							dadesIndexIncrement++;
							if (dadesIndex + dadesIndexIncrement == dades.size()) {
								break;
							}
							dadaActual = dades.get(dadesIndex + dadesIndexIncrement);
						}
						if (dadaActual.getMetaDada().getId().equals(metaDada.getId()) && dadaActual.getNode().getId().equals(expedient.getId())) {
							fila[5 + i] = dadaActual.getValorComString();
						} else {
							dadaActual = dades.get(dadesIndex);
						}
					}
				}
				// search for first dada index of next expedient
				DadaEntity dada = dades.get(dadesIndex); 
				while (dada.getNode().getId().equals(expedient.getId())) {
					dadesIndex++;
					if (dadesIndex == dades.size()) {
						break;
					}
					dada = dades.get(dadesIndex);
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
	public FitxerDto exportIndexExpedient(
			Long entitatId, 
			Long expedientId,
			boolean exportar) throws IOException {
		logger.debug(
				"Exportant índex de l'expedient (" + "entitatId=" + entitatId + ", " + "expedientId=" + expedientId +
						")");
		EntitatEntity entitatActual = entityComprovarHelper.comprovarEntitat(entitatId, true, false, false, false, false);
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				expedientId,
				false,
				true,
				false,
				false,
				false, false);
		FitxerDto resultat = new FitxerDto();
		
		if (exportar) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ZipOutputStream zos = new ZipOutputStream(baos);
	
			List<ContingutEntity> continguts = contingutRepository.findByPareAndEsborrat(
					expedient,
					0,
					new Sort("createdDate"));
			BigDecimal num = new BigDecimal(0);
			
			for (ContingutEntity contingut : continguts) {
				if (contingut instanceof DocumentEntity) {
					if (num.scale() > 0)
						num = num.setScale(0, BigDecimal.ROUND_HALF_UP);
					
					DocumentEntity document = (DocumentEntity)contingut;
					FitxerDto fitxer = documentHelper.getFitxerAssociat(document, null);
					BigDecimal sum = new BigDecimal(1);
					num = num.add(sum);
					String nomDocument = (num.scale() > 0 ? num.doubleValue() : num.intValue()) + " " + fitxer.getNom();
	
					if (document.getEstat().equals(DocumentEstatEnumDto.CUSTODIAT) || document.getEstat().equals(DocumentEstatEnumDto.DEFINITIU)) {
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
				}
				if (contingut instanceof CarpetaEntity) {
					if (num.scale() > 0)
						num = num.setScale(0, BigDecimal.ROUND_HALF_UP);
					BigDecimal sum = new BigDecimal(1);
					num = num.add(sum);
					
					List<String> estructuraCarpetes = new ArrayList<String>();
					List<DocumentEntity> documentsCarpetaActual = new ArrayList<DocumentEntity>();
					ContingutEntity carpetaActual = contingut;
					while (carpetaActual instanceof CarpetaEntity) {
						boolean darreraCarpeta = true;
						estructuraCarpetes.add((num.scale() > 0 ? num.doubleValue() : num.intValue()) + " " + carpetaActual.getNom());
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
							BigDecimal sum2 = new BigDecimal(0.1);
							num = num.add(sum2);
							FitxerDto fitxer = documentHelper.getFitxerAssociat(document, null);
							//num += (document.getNom() == documentsCarpetaActual.get(0).getNom()) ? 10 : 1; // primer document
							String nomDocument =  (num.scale() > 0 ? num.doubleValue() : num.intValue()) + " " + fitxer.getNom();
							String nomCarpeta = nomEstructuraCarpetes + nomDocument;
							
							if (document.getEstat().equals(DocumentEstatEnumDto.CUSTODIAT) || document.getEstat().equals(DocumentEstatEnumDto.DEFINITIU)) {
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
			zos.close();
	
			resultat.setNom(messageHelper.getMessage("expedient.service.exportacio.index") + " " + expedient.getNom() + ".zip");
			resultat.setContentType("application/zip");
			resultat.setContingut(baos.toByteArray());
		} else {
			resultat = contingutHelper.generarIndex(entitatActual, expedient);
		}
		return resultat;
	}

	@Override
	@Transactional
	public FitxerDto exportIndexExpedients(Long entitatId, Collection<Long> expedientIds) throws IOException {
		logger.debug(
				"Exportant índex dels expedients seleccionats (" + "entitatId=" + entitatId + ", " + "expedientIds=" +
						expedientIds + ")");
		entityComprovarHelper.comprovarEntitat(entitatId, true, false, false, false, false);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(baos);

		for (Long expedientId : expedientIds) {
			FitxerDto resultat = exportIndexExpedient(entitatId, expedientId, false);
			contingutHelper.crearNovaEntrada(resultat.getNom(), resultat, zos);
		}
		zos.close();
		FitxerDto resultat = new FitxerDto();
		resultat.setNom(messageHelper.getMessage("expedient.service.exportacio.index") + ".zip");
		resultat.setContentType("application/zip");
		resultat.setContingut(baos.toByteArray());
		return resultat;
	}
	
	@Override
	@Transactional	
	public boolean isOrganGestorPermes (Long expedientId) {
		ExpedientEntity expediente = expedientRepository.findOne(expedientId);
		
		return organGestorHelper.isOrganGestorPermes(expediente.getMetaExpedient(), 
				expediente.getOrganGestor(), 
				ExtendedPermission.ADMINISTRATION);
	}

	private PaginaDto<ExpedientDto> findAmbFiltrePaginat(
			Long entitatId,
			ExpedientFiltreDto filtre,
			PaginacioParamsDto paginacioParams,
			Long expedientId, 
			String rolActual) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false, true, false);
		MetaExpedientEntity metaExpedientFiltre = null;
		List<Long> metaExpedientIdDomini = null;
		if (filtre.getMetaExpedientId() != null) {
			metaExpedientFiltre = entityComprovarHelper.comprovarMetaExpedientPerExpedient(
					entitat,
					filtre.getMetaExpedientId(),
					true,
					false,
					false,
					false,
					false);
		}
		OrganGestorEntity organGestorFiltre = null;
		if (filtre.getOrganGestorId() != null) {
			organGestorFiltre = entityComprovarHelper.comprovarOrganGestorPerRolUsuari(
					entitat,
					filtre.getOrganGestorId());
		}
		/*/ Els meta-expedients permesos son els que tenen assignat permís de lectura directament
		// i també els que pertanyen a un òrgan sobre el que es te assignat permís de lectura.
		List<MetaExpedientEntity> metaExpedientsPermesos;
		if (filtre.getOrganGestorId() != null) {
			metaExpedientsPermesos = metaExpedientHelper.findAmbOrganFiltrePermis(
					entitatId,
					filtre.getOrganGestorId(),
					ExtendedPermission.READ,
					false,
					null);
		} else {
			metaExpedientsPermesos = metaExpedientHelper.findAmbEntitatPermis(
					entitatId,
					ExtendedPermission.READ,
					false,
					null, 
					rolActual.equals("IPA_ADMIN"),
					rolActual.equals("IPA_ORGAN_ADMIN"));
		}
		if (!metaExpedientsPermesos.isEmpty()) {*/
			UsuariEntity agafatPer = null;
			
			if (rolActual.equals("tothom")) {
				if (filtre.isMeusExpedients()) {
					agafatPer = usuariHelper.getUsuariAutenticat();
				}
			} else {
				if (filtre.getAgafatPer() != null && !filtre.getAgafatPer().isEmpty()) {
					agafatPer = usuariHelper.getUsuariByCodi(filtre.getAgafatPer());
				} 
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
						false, false);
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
			Map<String, String[]> ordenacioMap = new HashMap<String, String[]>();
			ordenacioMap.put("numero", new String[] { "codi", "any", "sequencia" });
			// Cercam els metaExpedients amb permisos assignats directament
			List<Long> metaExpedientIdPermesos = toListLong(permisosHelper.getObjectsIdsWithPermission(
					MetaNodeEntity.class,
					ExtendedPermission.READ));
			// Cercam els òrgans amb permisos assignats directament
			List<Long> organIdPermesos = toListLong(permisosHelper.getObjectsIdsWithPermission(
					OrganGestorEntity.class,
					ExtendedPermission.READ));
			// Cercam las parelles metaExpedient-organ amb permisos assignats directament
			List<Long> metaExpedientOrganIdPermesos = toListLong(permisosHelper.getObjectsIdsWithPermission(
					MetaExpedientOrganGestorEntity.class,
					ExtendedPermission.READ));
			// Cercam metaExpedients amb una meta-dada del domini del filtre
			metaExpedientIdDomini = expedientHelper.getMetaExpedientIdDomini(filtre.getMetaExpedientDominiCodi());
			Pageable pageable = paginacioHelper.toSpringDataPageable(paginacioParams, ordenacioMap);
			Page<ExpedientEntity> paginaExpedients = expedientRepository.findByEntitatAndPermesosAndFiltre(
					entitat,
					metaExpedientIdPermesos == null || metaExpedientIdPermesos.isEmpty(),
					metaExpedientIdPermesos == null || metaExpedientIdPermesos.isEmpty() ? null : metaExpedientIdPermesos,
					organIdPermesos == null || organIdPermesos.isEmpty(),
					organIdPermesos == null || organIdPermesos.isEmpty() ? null : organIdPermesos,
					metaExpedientOrganIdPermesos == null || metaExpedientOrganIdPermesos.isEmpty(),
					metaExpedientOrganIdPermesos == null || metaExpedientOrganIdPermesos.isEmpty() ? null : metaExpedientOrganIdPermesos,
					metaExpedientFiltre == null,
					metaExpedientFiltre,
					metaExpedientIdDomini == null || metaExpedientIdDomini.isEmpty(),
					metaExpedientIdDomini == null || metaExpedientIdDomini.isEmpty() ? null : metaExpedientIdDomini,
					organGestorFiltre == null,
					organGestorFiltre,
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
					pageable);
			PaginaDto<ExpedientDto> result = paginacioHelper.toPaginaDto(
					paginaExpedients,
					ExpedientDto.class,
					new Converter<ExpedientEntity, ExpedientDto>() {
						@Override
						public ExpedientDto convert(ExpedientEntity source) {
							return toExpedientDto(source, true);
						}
					});
			for (ExpedientDto expedient: result) {
				boolean enAlerta = alertaRepository.countByLlegidaAndContingutId(false, expedient.getId()) > 0;
				expedient.setAlerta(enAlerta);
			}
			return result;
		/*} else {
			return paginacioHelper.getPaginaDtoBuida(ExpedientDto.class);
		}*/
	}

	private List<Long> findIdsAmbFiltrePaginat(
			Long entitatId,
			ExpedientFiltreDto filtre,
			boolean accesAdmin,
			boolean comprovarAccesMetaExpedients) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, (!accesAdmin), accesAdmin, false, false, false);
		MetaExpedientEntity metaExpedient = null;
		if (filtre.getMetaExpedientId() != null) {
			metaExpedient = entityComprovarHelper.comprovarMetaExpedientPerExpedient(
					entitat,
					filtre.getMetaExpedientId(),
					true,
					false,
					false,
					false, false);
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
	
	private boolean isIncorporacioDuplicadaPermesa() {
		boolean isPropagarRelacio = Boolean.parseBoolean(
				PropertiesHelper.getProperties().getProperty("es.caib.ripea.incorporacio.anotacions.duplicada"));
		return isPropagarRelacio;
	}
	
	private boolean isProgaparRelacioActiva() {
		boolean isPropagarRelacio = Boolean.parseBoolean(
				PropertiesHelper.getProperties().getProperty("es.caib.ripea.propagar.relacio.expedients"));
		return isPropagarRelacio;
	}
	
	private boolean isIncorporacioJustificantActiva() {
		boolean isPropagarRelacio = Boolean.parseBoolean(PropertiesHelper.getProperties().getProperty("es.caib.ripea.incorporar.justificant"));
		return isPropagarRelacio;
	}

	private List<Long> toListLong(List<Serializable> original) {
		List<Long> listLong = new ArrayList<Long>(original.size());
		for (Serializable s: original) { 
			listLong.add((Long)s); 
		}
		return listLong;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(ExpedientServiceImpl.class);

}
