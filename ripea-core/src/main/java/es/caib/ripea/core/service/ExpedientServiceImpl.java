/**
 * 
 */
package es.caib.ripea.core.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.ws.backofficeintegracio.AnotacioRegistreId;
import es.caib.distribucio.ws.backofficeintegracio.Estat;
import es.caib.ripea.core.api.dto.CarpetaDto;
import es.caib.ripea.core.api.dto.CodiValorDto;
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
import es.caib.ripea.core.api.dto.ResultDto;
import es.caib.ripea.core.api.dto.ResultEnumDto;
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
import es.caib.ripea.core.entity.GrupEntity;
import es.caib.ripea.core.entity.InteressatEntity;
import es.caib.ripea.core.entity.MetaDadaEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.MetaExpedientOrganGestorEntity;
import es.caib.ripea.core.entity.MetaNodeEntity;
import es.caib.ripea.core.entity.OrganGestorEntity;
import es.caib.ripea.core.entity.RegistreAnnexEntity;
import es.caib.ripea.core.entity.UsuariEntity;
import es.caib.ripea.core.firma.DocumentFirmaServidorFirma;
import es.caib.ripea.core.helper.CacheHelper;
import es.caib.ripea.core.helper.CarpetaHelper;
import es.caib.ripea.core.helper.ConfigHelper;
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
import es.caib.ripea.core.helper.PaginacioHelper.ConverterParam;
import es.caib.ripea.core.helper.PermisosHelper;
import es.caib.ripea.core.helper.PluginHelper;
import es.caib.ripea.core.helper.RolHelper;
import es.caib.ripea.core.helper.UsuariHelper;
import es.caib.ripea.core.repository.AlertaRepository;
import es.caib.ripea.core.repository.CarpetaRepository;
import es.caib.ripea.core.repository.DadaRepository;
import es.caib.ripea.core.repository.DocumentRepository;
import es.caib.ripea.core.repository.ExpedientComentariRepository;
import es.caib.ripea.core.repository.ExpedientEstatRepository;
import es.caib.ripea.core.repository.ExpedientPeticioRepository;
import es.caib.ripea.core.repository.ExpedientRepository;
import es.caib.ripea.core.repository.GrupRepository;
import es.caib.ripea.core.repository.MetaExpedientRepository;
import es.caib.ripea.core.repository.OrganGestorRepository;
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
	private DadaRepository dadaRepository;
	@Autowired
	private AlertaRepository alertaRepository;
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
	@Autowired
	private ConfigHelper configHelper;
	@Autowired
	private OrganGestorRepository organGestorRepository;
	@Autowired
	private DocumentRepository documentRepository;
	@Autowired
	private CarpetaHelper carpetaHelper;
	@Autowired
	private CarpetaRepository carpetaRepository;
	@Autowired
	private GrupRepository grupRepository;
	
	public static List<DocumentDto> expedientsWithImportacio = new ArrayList<DocumentDto>();
	public Object lock = new Object();

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
			Long grupId, 
			String rolActual, 
			Map<Long, Long> anexosIdsMetaDocsIdsMap) {

		logger.info(
				"Creant nou expedient Service(" +
						"entitatId=" + entitatId + ", " +
						"metaExpedientId=" + metaExpedientId + ", " +
						"metaExpedientDominiId=" + metaExpedientDominiId + ", " +
						"organGestorId=" + organGestorId + ", " +
						"pareId=" + pareId + ", " +
						"any=" + any + ", " +
						"sequencia=" + sequencia + ", " +
						"nom=" + nom + ", " +
						"expedientPeticioId=" + expedientPeticioId + ")");

		// create expedient in db 
		Long expedientId;
		synchronized (lock) {
			expedientId = expedientHelper.create(
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
					grupId, 
					rolActual);
		}
		
		boolean expCreatArxiuOk = expedientHelper.createArxiu(expedientId);
		
		ExpedientEntity expedient = expedientRepository.findOne(expedientId);
		logger.info(
				"Expedient crear Service Middle(" +
						"sequencia=" + expedient.getSequencia() + ", " +
						"any=" + expedient.getAny() + ", " +
						"metaExpedient=" + expedient.getMetaExpedient().getId() + " - " + expedient.getMetaExpedient().getCodi() + ")");
		ExpedientDto expedientDto = expedientHelper.toExpedientDto(expedient, true, null, false);
		
		
		// if expedient comes from distribucio
		boolean processatOk = true;
		ExpedientPeticioEntity expedientPeticioEntity = null;
		if (expedientPeticioId != null) {
			
			expedientPeticioEntity = expedientPeticioRepository.findOne(expedientPeticioId);
			if (expCreatArxiuOk) {
				expedientDto.setExpCreatArxiuOk(true);

				expedientHelper.inicialitzarExpedientsWithImportacio();
				for (RegistreAnnexEntity registeAnnexEntity : expedientPeticioEntity.getRegistre().getAnnexos()) {
					try {
						expedientHelper.crearDocFromAnnex(
								expedient.getId(),
								registeAnnexEntity.getId(),
								expedientPeticioEntity.getId(), 
								anexosIdsMetaDocsIdsMap.get(registeAnnexEntity.getId()), rolActual);
					} catch (Exception e) {
						processatOk = false;
						logger.error("Error crear doc from annex", e);
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
								expedientPeticioEntity.getId());
					} catch (Exception e) {
						processatOk = false;
						logger.error("Error crear doc from uuid", e);
					}
					
				}
				if (!expedientHelper.consultaExpedientsAmbImportacio().isEmpty() && ! isIncorporacioDuplicadaPermesa()) {
					throw new DocumentAlreadyImportedException();
				}
				expedientPeticioHelper.canviEstatExpedientPeticio(expedientPeticioEntity.getId(), ExpedientPeticioEstatEnumDto.PROCESSAT_PENDENT);
				if (processatOk) {
					notificarICanviEstatToProcessatNotificat(expedientPeticioEntity.getId());
				}
				expedientDto.setProcessatOk(processatOk);
				
			} else {
				
				for (RegistreAnnexEntity registeAnnexEntity : expedientPeticioEntity.getRegistre().getAnnexos()) {
					expedientHelper.updateRegistreAnnexError(
							registeAnnexEntity.getId(),
							"Annex no s'ha processat perque expedient na s'ha creat en arxiu");
				}
				expedientDto.setExpCreatArxiuOk(false);
			}

		}

		
		logger.info(
				"Expedient crear Service End(" +
						"id=" + expedient.getId() + ", " +
						"nom=" + expedient.getNom() + ", " +
						"numero=" + expedient.getMetaExpedient().getCodi() + "/" +  expedient.getSequencia() + "/" + expedient.getAny() +
						"metaExpedientId=" + expedient.getMetaExpedient().getId() + ")");
		
		return expedientDto;
	}

	@Override
	public boolean incorporar(Long entitatId, Long expedientId, Long expedientPeticioId, boolean associarInteressats, String rolActual, Map<Long, Long> anexosIdsMetaDocsIdsMap) {
		logger.info("Incorporant a l'expedient existent (" +
				"entitatId=" + entitatId + ", " +
				"expedientId=" + expedientId + ", " +
				"expedientPeticioId=" + expedientPeticioId + ")");

		expedientHelper.relateExpedientWithPeticioAndSetAnnexosPendentNewTransaction(expedientPeticioId, expedientId);
		if (associarInteressats) {
			expedientHelper.associateInteressats(expedientId, entitatId, expedientPeticioId, PermissionEnumDto.WRITE, rolActual);
		}
		expedientHelper.inicialitzarExpedientsWithImportacio();
		boolean processatOk = true;
		
		Long registreId = expedientPeticioRepository.getRegistreId(expedientPeticioId);
		List<Long> annexosIds = expedientPeticioRepository.getRegistreAnnexosId(registreId);
		for (Long annexId : annexosIds) {
			try {
				boolean throwException1 = false;
				if (throwException1)
					throw new RuntimeException("EXCEPION BEFORE INCORPORAR !!!!!! ");
				expedientHelper.crearDocFromAnnex(
						expedientId,
						annexId,
						expedientPeticioId, 
						anexosIdsMetaDocsIdsMap.get(annexId), rolActual);	
			} catch (Exception e) {
				processatOk = false;
				logger.error(ExceptionUtils.getStackTrace(e));
				expedientHelper.updateRegistreAnnexError(annexId, ExceptionUtils.getStackTrace(e));
			}
		}
		String arxiuUuid = expedientPeticioRepository.getRegistreJustificantArxiuUuid(registreId);
		if (arxiuUuid != null && isIncorporacioJustificantActiva()) {
			try {
				expedientHelper.crearDocFromUuid(
						expedientId,
						arxiuUuid, 
						expedientPeticioId);
			} catch (Exception e) {
				logger.error(ExceptionUtils.getStackTrace(e));
			}
		}
		if (!expedientHelper.consultaExpedientsAmbImportacio().isEmpty() && ! isIncorporacioDuplicadaPermesa()) {
			throw new DocumentAlreadyImportedException();
		}
		expedientPeticioHelper.canviEstatExpedientPeticio(expedientPeticioId, ExpedientPeticioEstatEnumDto.PROCESSAT_PENDENT);
		if (processatOk) {
			notificarICanviEstatToProcessatNotificat(expedientPeticioId);
		}
		return processatOk;
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
	public boolean retryCreateDocFromAnnex(Long registreAnnexId, Long expedientPeticioId, Long metaDocumentId, String rolActual) {
		boolean processatOk = true;
		try {
			ExpedientPeticioEntity expedientPeticioEntity = expedientPeticioRepository.findOne(expedientPeticioId);
			if (expedientPeticioEntity.getExpedient() == null) {
				throw new RuntimeException("Anotació pendent amb id: " + expedientPeticioEntity.getId() + " no té expedient associat en la base de dades.");
			}
			expedientHelper.crearDocFromAnnex(expedientPeticioEntity.getExpedient().getId(), registreAnnexId, expedientPeticioEntity.getId(), metaDocumentId, rolActual);
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
		contingutHelper.comprovarContingutDinsExpedientModificable(entitatId, id, false, true, false, false, false, null);
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				id,
				false,
				false,
				true,
				false,
				false, 
				false, null);

		expedientHelper.updateNomExpedient(expedient, nom);
		ExpedientDto dto = expedientHelper.toExpedientDto(expedient, true, null, false);
		contingutHelper.arxiuPropagarModificacio(expedient, null, false, false, null, false);
		return dto;
	}

	@Transactional
	@Override
	public ExpedientDto update(Long entitatId, Long id, String nom, int any, Long metaExpedientDominiId, Long organGestorId, String rolActual, Long grupId) {
		logger.debug(
				"Actualitzant dades de l'expedient (" + "entitatId=" + entitatId + ", " + "id=" + id + ", " + "nom=" +
						nom + ")");
		contingutHelper.comprovarContingutDinsExpedientModificable(entitatId, id, false, true, false, false, false, rolActual);
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				id,
				false,
				false,
				true,
				false,
				false, 
				false, 
				rolActual);
		entityComprovarHelper.comprovarEstatExpedient(entitatId, id, ExpedientEstatEnumDto.OBERT);
		expedientHelper.updateNomExpedient(expedient, nom);
		expedientHelper.updateAnyExpedient(expedient, any);
		expedientHelper.updateOrganGestor(expedient, organGestorId, rolActual);
		GrupEntity grupEntity = grupRepository.findOne(grupId);
		expedient.setGrup(grupEntity);
		ExpedientDto dto = expedientHelper.toExpedientDto(expedient, true, null, false);
		contingutHelper.arxiuPropagarModificacio(expedient, null, false, false, null, false);
		return dto;
	}

	@Transactional(readOnly = true)
	@Override
	public ExpedientDto findById(Long entitatId, Long id, String rolActual) {
		logger.debug("Obtenint l'expedient (" + "entitatId=" + entitatId + ", " + "id=" + id + ")");
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				id,
				false,
				true,
				false,
				false,
				false, false, null);
		return expedientHelper.toExpedientDto(expedient, true, null, false);
	}
	
	@Transactional(readOnly = true)
	@Override
	public List<ExpedientDto> findByIds(Long entitatId, Set<Long> ids) {
		logger.debug("Obtenint l'expedients (" + "entitatId=" + entitatId + ", " + "ids=" + ids + ")");
		List<ExpedientDto> expedients = new ArrayList<>();
		
		for (Long id : ids) {
			ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
					entitatId,
					id,
					false,
					true,
					false,
					false,
					false, 
					false, 
					null);
			expedients.add(expedientHelper.toExpedientDto(expedient, true, null, false));
		}
		return expedients;
	}
	
	

	@Transactional(readOnly = true)
	public ExpedientDto findByMetaExpedientAndPareAndNomAndEsborrat(
			Long entitatId,
			Long metaExpedientId,
			Long pareId,
			String nom,
			int esborrat, 
			String rolActual, 
			Long organId) {
		return expedientHelper.findByMetaExpedientAndPareAndNomAndEsborrat(
				entitatId,
				metaExpedientId,
				pareId,
				nom,
				esborrat,
				rolActual,
				organId);
	}

	@Transactional
	@Override
	public boolean publicarComentariPerExpedient(Long entitatId, Long expedientId, String text, String rolActual) {
		logger.debug(
				"Obtenint els comentaris pel contingut (" + "entitatId=" + entitatId + ", " + "nodeId=" + expedientId +
						")");
		entityComprovarHelper.comprovarEntitat(entitatId, false, false, false, true, false);
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				expedientId,
				false,
				false,
				true,
				false,
				false, 
				false, 
				rolActual);
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
		entityComprovarHelper.comprovarEntitat(entitatId, false, false, false, true, false);
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				expedientId,
				false,
				true,
				false,
				false,
				false, false, null);

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
		return findAmbFiltrePaginat(entitatId, filtre, paginacioParams, null, rolActual, ResultEnumDto.PAGE).getPagina();
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
		return findAmbFiltrePaginat(entitatId, filtre, paginacioParams, expedientId, "tothom", ResultEnumDto.PAGE).getPagina();
	}

	@Transactional
	@Override
	public List<ExpedientDto> findByEntitatAndMetaExpedient(Long entitatId, Long metaExpedientId, String rolActual, Long organActualId) {
		logger.debug(
				"Consultant els expedients(" + "entitatId=" + entitatId + ", " + "metaExpedientId=" + metaExpedientId +
						")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false, true, false);
		MetaExpedientEntity metaExpedient = null;
		if (metaExpedientId != null) {
			metaExpedient = entityComprovarHelper.comprovarMetaExpedientPerExpedient(
					entitat,
					metaExpedientId,
					true,
					false,
					false,
					false, 
					false, 
					rolActual, 
					null);
		}
		
		
		List<ExpedientEntity> expedientsEnt;
		if (rolActual.equals("IPA_ORGAN_ADMIN")) {
			List<Long> organsIdsPermitted = organGestorRepository.findFillsIds(entitat, Arrays.asList(organActualId));
			organsIdsPermitted.add(organActualId);
			expedientsEnt = expedientRepository.findByEntitatAndMetaExpedientAndOrgans(entitat, organsIdsPermitted, metaExpedient);
		} else {
			expedientsEnt = expedientRepository.findByEntitatAndMetaExpedient(entitat, metaExpedient);
		}

		List<ExpedientDto> expedientsDto = new ArrayList<>();
		// if meta expedient has write permissions add all expedients
		if (entityComprovarHelper.hasMetaExpedientWritePermissons(metaExpedientId) || RolHelper.isAdminEntitat(rolActual) || rolActual.equals("IPA_ORGAN_ADMIN")) {
			for (ContingutEntity cont : expedientsEnt) {
				ExpedientEntity exp = (ExpedientEntity)cont;
				ExpedientDto expedient = new ExpedientDto();
				expedient.setId(exp.getId());
				expedient.setNom(exp.getNom());
				expedient.setNumero(expedientHelper.calcularNumero(exp));
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
					expedient.setNumero(expedientHelper.calcularNumero(exp));
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
	public List<CodiValorDto> findByEntitat(Long entitatId) {
		logger.debug("Consulta de expedients de l'entitat (" + "entitatId=" + entitatId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, true, false, false, false);
		List<ExpedientEntity> expedients = expedientRepository.findByEntitatOrderByNomAsc(entitat);
		return conversioTipusHelper.convertirList(expedients, CodiValorDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public List<ExpedientSelectorDto> findPerUserAndProcediment(Long entitatId, Long metaExpedientId, String rolActual) {
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
					rolActual.equals("IPA_ADMIN") || rolActual.equals("IPA_ORGAN_ADMIN"),
					null, 
					null);
		}
		List<MetaExpedientEntity> metaExpedientsPermesos = metaExpedientHelper.findPermesosAccioMassiva(entitatId, rolActual);
//		List<MetaExpedientEntity> metaExpedientsPermesos = metaExpedientRepository.findByEntitatOrderByNomAsc(entitat);
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		permisosHelper.filterGrantedAll(
//				metaExpedientsPermesos,
//				new ObjectIdentifierExtractor<MetaExpedientEntity>() {
//					@Override
//					public Long getObjectIdentifier(MetaExpedientEntity metaExpedient) {
//						return metaExpedient.getId();
//					}
//				},
//				MetaNodeEntity.class,
//				new Permission[] { ExtendedPermission.READ },
//				auth);
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
	public List<Long> findIdsAmbFiltre(Long entitatId, ExpedientFiltreDto filtre, String rolActual) throws NotFoundException {
		logger.debug(
				"Consultant els ids d'expedient segons el filtre (" + "entitatId=" + entitatId + ", " + "filtre=" +
						filtre + ")");
		return findAmbFiltrePaginat(entitatId, filtre, null, null, rolActual, ResultEnumDto.IDS).getIds();
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
				false, false, null);
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
				false, null);
		
		entityComprovarHelper.comprovarPermisMetaNode(
				expedient.getMetaExpedient(),
				expedient.getId(),
				ExtendedPermission.WRITE,
				"WRITE",
				usuariCodi, 
				null, 
				null);
		
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
				false, false, null);
		

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
				false, false, null);
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
				false, false, null);
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
				checkPerMassiuAdmin, null);
		if (!cacheHelper.findErrorsValidacioPerNode(expedient).isEmpty()) {
			throw new ValidationException("No es pot tancar un expedient amb errors de validació");
		}
		if (cacheHelper.hasNotificacionsPendentsPerExpedient(expedient)) {
			throw new ValidationException("No es pot tancar un expedient amb notificacions pendents");
		}
		boolean hiHaEsborranysPerFirmar = documentsPerFirmar != null && documentsPerFirmar.length > 0;
		if (!documentRepository.hasAnyDocumentDefinitiu(expedient) && !hiHaEsborranysPerFirmar) {
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
				false, null);
		entityComprovarHelper.comprovarEstatExpedient(entitatId, id, ExpedientEstatEnumDto.TANCAT);
		expedient.updateEstat(ExpedientEstatEnumDto.OBERT, null);
		contingutLogHelper.log(expedient, LogTipusEnumDto.REOBERTURA, null, null, false, false);
		if (pluginHelper.isArxiuPluginActiu()) {
			pluginHelper.arxiuExpedientReobrir(expedient);
		}
	}

	
	@Transactional
	@Override
	public Exception guardarExpedientArxiu(
			Long expId) {
		
		return expedientHelper.guardarExpedientArxiu(expId);
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
					checkPerMassiuAdmin, null, null);
		}
		
		List<MetaExpedientEntity> metaExpedientsPermesos = metaExpedientHelper.findPermesosAccioMassiva(entitatId, rolActual);

		if (!metaExpedientsPermesos.isEmpty()) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			UsuariEntity usuariActual = usuariRepository.findOne(auth.getName());
			Date dataInici = DateHelper.toDateInicialDia(filtre.getDataInici());
			Date dataFi = DateHelper.toDateFinalDia(filtre.getDataFi());
			Map<String, String[]> ordenacioMap = new HashMap<String, String[]>();
			ordenacioMap.put("createdBy.codiAndNom", new String[] {"createdBy.nom"});
			Page<ExpedientEntity> paginaDocuments = expedientRepository.findExpedientsPerTancamentMassiu(
					entitat,
					nomesAgafats,
					usuariActual,
					metaExpedientsPermesos, 
					metaExpedient == null,
					metaExpedient,
					filtre.getNom() == null,
					filtre.getNom() != null ? filtre.getNom().trim() : "",
					dataInici == null,
					dataInici,
					dataFi == null,
					dataFi,
					paginacioHelper.toSpringDataPageable(paginacioParams,ordenacioMap));
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
									false, null, false, null);
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
					false, false, null, null);
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
					filtre.getNom() != null ? filtre.getNom().trim() : "",
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
	public void relacioCreate(Long entitatId, final Long id, final Long relacionatId, String rolActual) {
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
				false, 
				false, 
				rolActual);
		ExpedientEntity toRelate = entityComprovarHelper.comprovarExpedient(
				entitatId,
				relacionatId,
				false,
				true,
				false,
				false,
				false, 
				false, 
				rolActual);

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
	public boolean relacioDelete(Long entitatId, final Long id, final Long relacionatId, String rolActual) {
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
				false, 
				false, 
				rolActual);
		ExpedientEntity relacionat = entityComprovarHelper.comprovarExpedient(
				entitatId,
				relacionatId,
				false,
				true,
				false,
				false,
				false, 
				false, 
				rolActual);
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
	public List<ExpedientDto> findByText(Long entitatId, String text) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false, 
				false, 
				false);
		List<ExpedientEntity> expedients = expedientRepository.findByText(entitat, text);
		List<ExpedientDto> expedientsDto = new ArrayList<ExpedientDto>();
		for (ExpedientEntity expedientEntity : expedients) {
			ExpedientDto expedientDto = new ExpedientDto();
			expedientDto.setId(expedientEntity.getId());
			expedientDto.setNom(expedientEntity.getNom());
			expedientDto.setNumero(expedientHelper.calcularNumero(expedientEntity));
			expedientsDto.add(expedientDto);
		}
		return expedientsDto;
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
				false, false, null);
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
			relacionatsDto.add(expedientHelper.toExpedientDto(e, false, null, false));
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
					false, false, null, null);
		}
		List<ExpedientEntity> expedients = expedientRepository.findByEntitatAndIdInOrderByIdAsc(
				entitat,
				expedientIds);
		List<MetaDadaEntity> metaDades = dadaRepository.findDistinctMetaDadaByNodeIdInOrderByMetaDadaCodiAsc(
				expedientIds);
		List<DadaEntity> dades = dadaRepository.findByNodeIdInOrderByNodeIdAscMetaDadaCodiAsc(expedientIds);
		int numColumnes = 7 + metaDades.size();
		String[] columnes = new String[numColumnes];
		columnes[0] = messageHelper.getMessage("expedient.service.exportacio.numero");
		columnes[1] = messageHelper.getMessage("expedient.service.exportacio.titol");
		columnes[2] = messageHelper.getMessage("expedient.service.exportacio.estat");
		columnes[3] = messageHelper.getMessage("expedient.service.exportacio.datcre");
		columnes[4] = messageHelper.getMessage("expedient.service.exportacio.idnti");
		columnes[5] = messageHelper.getMessage("expedient.service.exportacio.procediment");
		columnes[6] = messageHelper.getMessage("expedient.service.exportacio.interessats");
		for (int i = 0; i < metaDades.size(); i++) {
			MetaDadaEntity metaDada = metaDades.get(i);
			columnes[7 + i] = metaDada.getNom() + " (" + metaDada.getCodi() + ")";
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
			fila[5] = expedient.getMetaExpedient().getNom();
			
			String intressatsString = "";
			for (InteressatEntity interessat : expedient.getInteressats()) {
				intressatsString += interessat.getIdentificador() + " (" + interessat.getDocumentNum() + ") | ";
			}
			intressatsString = intressatsString.replaceAll(",","");
			
			int index = intressatsString.lastIndexOf(" | ");
			if (index != -1) {
				intressatsString = intressatsString.substring(0, index);
			}
			fila[6] = intressatsString;
			
			if (!dades.isEmpty() && dadesIndex < dades.size()) {
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
							fila[7 + i] = dadaActual.getValorComString();
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
			Set<Long> expedientIds,
			boolean exportar) throws IOException {
		if (expedientIds.size() == 1)
			logger.debug("Exportant índex de l'expedient (" + "entitatId=" + entitatId + ", " + "expedientId=" + expedientIds.iterator().next() + ")");
		EntitatEntity entitatActual = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false, true, false);
		List<ExpedientEntity> expedients = new ArrayList<ExpedientEntity>();
//		comprovar accés expedients
		for (Long expedientId : expedientIds) {
			ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
					entitatId,
					expedientId,
					false,
					true,
					false,
					false,
					false, 
					false, null);
			expedients.add(expedient);
		}
		
		FitxerDto resultat = new FitxerDto();
		
		try {
			resultat = expedientHelper.exportarExpedient(
					entitatActual, 
					expedients, 
					exportar);	
		} catch (Exception ex) {
			throw new RuntimeException("Hi ha hagut un problema generant l'índex de l'expedient", ex);
		}
		return resultat;
	}

	@Override
	@Transactional
	public FitxerDto exportIndexExpedients(
			Long entitatId, 
			Set<Long> expedientIds,
			String format) throws IOException {
		logger.debug("Exportant índex dels expedients seleccionats (" + "entitatId=" + entitatId + ", " + "expedientIds=" + expedientIds + ")");
		entityComprovarHelper.comprovarEntitat(entitatId, true, false, false, false, false);
		FitxerDto resposta = new FitxerDto();
		
		if ("PDF".equals(format)) {
			FitxerDto resultat = exportIndexExpedient(
					entitatId, 
					expedientIds, 
					false);
			resposta.setNom(resultat.getNom());
			resposta.setContentType("application/pdf");
			resposta.setContingut(resultat.getContingut());
		} else if ("ZIP".equals(format)) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ZipOutputStream zos = new ZipOutputStream(baos);
			for (Long expedientId : expedientIds) {
				Set<Long> expedientIdSet = new HashSet<>(Arrays.asList(expedientId));
				FitxerDto resultat = exportIndexExpedient(entitatId, expedientIdSet, false);
				contingutHelper.crearNovaEntrada(
						resultat.getNom(), 
						resultat, 
						zos);
			}
			zos.close();
			resposta.setNom(messageHelper.getMessage("expedient.service.exportacio.index") + ".zip");
			resposta.setContentType("application/zip");
			resposta.setContingut(baos.toByteArray());
		}
		return resposta;
	}
	
	@Override
	@Transactional	
	public boolean isOrganGestorPermes (Long expedientId, String rolActual) {
		ExpedientEntity expediente = expedientRepository.findOne(expedientId);
		
		return organGestorHelper.isOrganGestorPermes(expediente.getMetaExpedient(), 
				expediente.getOrganGestor(), 
				ExtendedPermission.ADMINISTRATION, 
				rolActual);
	}
	
	@Override
	public boolean hasReadPermissionsAny(String rolActual, Long entitatId) {
		boolean hasAnyPermissions = false;
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false, true, false);
		
		if (rolActual.equals("IPA_ADMIN")) {
			hasAnyPermissions = permisosHelper.isGrantedAll(
					entitatId,
					EntitatEntity.class,
					new Permission[] { ExtendedPermission.ADMINISTRATION },
					SecurityContextHolder.getContext().getAuthentication());
		}
		
		// Cercam els metaExpedients amb permisos assignats directament
		List<Long> metaExpedientIdPermesos = toListLong(permisosHelper.getObjectsIdsWithPermission(
				MetaNodeEntity.class,
				ExtendedPermission.READ));
		if (metaExpedientIdPermesos != null && !metaExpedientIdPermesos.isEmpty()) {
			hasAnyPermissions = true;
		}
		
		// Cercam els òrgans amb permisos assignats directament
		List<Long> organIdPermesos;
		if (rolActual.equals("IPA_ORGAN_ADMIN")) {
			organIdPermesos = toListLong(permisosHelper.getObjectsIdsWithPermission(
					OrganGestorEntity.class,
					ExtendedPermission.ADMINISTRATION));
		} else {
			organIdPermesos = toListLong(permisosHelper.getObjectsIdsWithPermission(
					OrganGestorEntity.class,
					ExtendedPermission.READ));
		}
		if (organIdPermesos != null && !organIdPermesos.isEmpty()) {
			hasAnyPermissions = true;
		}
		
		// Cercam las parelles metaExpedient-organ amb permisos assignats directament
		List<Long> metaExpedientOrganIdPermesos = toListLong(permisosHelper.getObjectsIdsWithPermission(
				MetaExpedientOrganGestorEntity.class,
				ExtendedPermission.READ));
		if (metaExpedientOrganIdPermesos != null && !metaExpedientOrganIdPermesos.isEmpty()) {
			hasAnyPermissions = true;
		}
		
		// Cercam els òrgans amb permisos per procediemnts comuns
		List<Long> organProcedimentsComunsIdsPermesos = toListLong(permisosHelper.getObjectsIdsWithTwoPermissions(
				MetaExpedientOrganGestorEntity.class,
				ExtendedPermission.COMU,
				ExtendedPermission.READ));
		if (organProcedimentsComunsIdsPermesos != null && !organProcedimentsComunsIdsPermesos.isEmpty()) {
			hasAnyPermissions = true;
		}
		
		List<Long> procedimentsComunsIds = metaExpedientRepository.findProcedimentsComunsActiveIds(entitat);
		if (procedimentsComunsIds != null && !procedimentsComunsIds.isEmpty()) {
			hasAnyPermissions = true;
		}
		
		return hasAnyPermissions;
	}

	

	private ResultDto<ExpedientDto> findAmbFiltrePaginat(
			Long entitatId,
			ExpedientFiltreDto filtre,
			PaginacioParamsDto paginacioParams,
			Long expedientId, 
			String rolActual, 
			ResultEnumDto resultEnum) {
		
		ResultDto<ExpedientDto> result = new ResultDto<ExpedientDto>();
		
		long t0 = System.currentTimeMillis();
		
		long t1 = System.currentTimeMillis();
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false, true, false);
		logger.trace("comprovarEntitat time:  " + (System.currentTimeMillis() - t1) + " ms");
		MetaExpedientEntity metaExpedientFiltre = null;
		List<Long> metaExpedientIdDomini = null;
		
		long t2 = System.currentTimeMillis();
		if (filtre.getMetaExpedientId() != null) {
			metaExpedientFiltre = entityComprovarHelper.comprovarMetaExpedientPerExpedient(
					entitat,
					filtre.getMetaExpedientId(),
					true,
					false,
					false,
					false,
					false, 
					rolActual, null);
		}
		logger.trace("comprovarMetaExpedientPerExpedient time:  " + (System.currentTimeMillis() - t2) + " ms");
		
		long t3 = System.currentTimeMillis();
		OrganGestorEntity organGestorFiltre = null;
		if (filtre.getOrganGestorId() != null) {
			if (rolActual.equals("IPA_ADMIN")) {
				organGestorFiltre = entityComprovarHelper.comprovarOrganGestorAdmin(
						entitat.getId(),
						filtre.getOrganGestorId());
			} else {
				organGestorFiltre = entityComprovarHelper.comprovarOrganGestorPerRolUsuari(
						entitat,
						filtre.getOrganGestorId());
			}
		}
		logger.trace("comprovarOrgan time:  " + (System.currentTimeMillis() - t3) + " ms");
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
			long t4 = System.currentTimeMillis();
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
			logger.trace("getUsuariAgafat time:  " + (System.currentTimeMillis() - t4) + " ms");
			
			long t5 = System.currentTimeMillis();
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
			logger.trace("getEstat time:  " + (System.currentTimeMillis() - t5) + " ms");
			
			long t6 = System.currentTimeMillis();
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
						false, 
						false, 
						rolActual);
				expedientsToBeExluded.addAll(expedient.getRelacionatsAmb());
				expedientsToBeExluded.addAll(expedient.getRelacionatsPer());
				expedientsToBeExluded.add(expedient);
			} else {
				esNullExpedientsToBeExcluded = true;
				expedientsToBeExluded = null; // repository does not accept empty list but it accepts null value
			}			
			logger.trace("expedientsToBeExluded time:  " + (System.currentTimeMillis() - t6) + " ms");
			long t7 = System.currentTimeMillis();
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
			
			List<Long> metaExpedientIdPermesos = null;
			if (rolActual.equals("IPA_ADMIN")) {
				metaExpedientIdPermesos = metaExpedientRepository.findAllIdsByEntitat(entitat);
			} else {
				// Cercam els metaExpedients amb permisos assignats directament
				metaExpedientIdPermesos = toListLong(permisosHelper.getObjectsIdsWithPermission(
						MetaNodeEntity.class,
						ExtendedPermission.READ));
			}
			logger.trace("metaExpedientIdPermesos (" + (metaExpedientIdPermesos != null ? metaExpedientIdPermesos.size() : "0") + ") time:  " + (System.currentTimeMillis() - t7) + " ms");
			
			// Cercam els òrgans amb permisos assignats directament
			long t8 = System.currentTimeMillis();
			List<Long> organIdPermesos;
			if (rolActual.equals("IPA_ORGAN_ADMIN")) {
				organIdPermesos = toListLong(permisosHelper.getObjectsIdsWithPermission(
						OrganGestorEntity.class,
						ExtendedPermission.ADMINISTRATION));
			} else {
				organIdPermesos = toListLong(permisosHelper.getObjectsIdsWithPermission(
						OrganGestorEntity.class,
						ExtendedPermission.READ));
			}
			logger.trace("organIdPermesos (" + (organIdPermesos != null ? organIdPermesos.size() : "0") + ") time:  " + (System.currentTimeMillis() - t8) + " ms");
			
			// Cercam las parelles metaExpedient-organ amb permisos assignats directament
			long t9 = System.currentTimeMillis();
			List<Long> metaExpedientOrganIdPermesos = toListLong(permisosHelper.getObjectsIdsWithPermission(
					MetaExpedientOrganGestorEntity.class,
					ExtendedPermission.READ));
			logger.trace("metaExpedientOrganIdPermesos (" + (metaExpedientOrganIdPermesos != null ? metaExpedientOrganIdPermesos.size() : "0") + ") time:  " + (System.currentTimeMillis() - t9) + " ms");
			
			// Cercam els òrgans amb permisos per procediemnts comuns
			long t91 = System.currentTimeMillis();
			List<Long> organProcedimentsComunsIdsPermesos = toListLong(permisosHelper.getObjectsIdsWithTwoPermissions(
					MetaExpedientOrganGestorEntity.class,
					ExtendedPermission.COMU,
					ExtendedPermission.READ));
			List<Long> procedimentsComunsIds = metaExpedientRepository.findProcedimentsComunsActiveIds(entitat);
			logger.trace("organProcedimentsComunsIdsPermesos (" + (organProcedimentsComunsIdsPermesos != null ? organProcedimentsComunsIdsPermesos.size() : "0") + " " + (procedimentsComunsIds != null ? procedimentsComunsIds.size() : "0") + ") time:  " + (System.currentTimeMillis() - t91) + " ms");
			
			// Cercam metaExpedients amb una meta-dada del domini del filtre
			long t92 = System.currentTimeMillis();
			metaExpedientIdDomini = expedientHelper.getMetaExpedientIdDomini(filtre.getMetaExpedientDominiCodi());
			logger.trace("metaExpedientIdDomini (" + (metaExpedientOrganIdPermesos != null ? metaExpedientOrganIdPermesos.size() : "0") + ") time:  " + (System.currentTimeMillis() - t92) + " ms");
			
			if (resultEnum == ResultEnumDto.PAGE) {
				
				// ================================  RETURNS PAGE (DATATABLE) ==========================================
				long t10 = System.currentTimeMillis();
				Pageable pageable = paginacioHelper.toSpringDataPageable(paginacioParams, ordenacioMap);
				Page<ExpedientEntity> paginaExpedients = expedientRepository.findByEntitatAndPermesosAndFiltre(
						entitat,
						metaExpedientIdPermesos == null || metaExpedientIdPermesos.isEmpty(),
						metaExpedientIdPermesos == null || metaExpedientIdPermesos.isEmpty() ? null : metaExpedientIdPermesos,
						organIdPermesos == null || organIdPermesos.isEmpty(),
						organIdPermesos == null || organIdPermesos.isEmpty() ? null : organIdPermesos,
						metaExpedientOrganIdPermesos == null || metaExpedientOrganIdPermesos.isEmpty(),
						metaExpedientOrganIdPermesos == null || metaExpedientOrganIdPermesos.isEmpty() ? null : metaExpedientOrganIdPermesos,
						organProcedimentsComunsIdsPermesos == null || organProcedimentsComunsIdsPermesos.isEmpty(),
						organProcedimentsComunsIdsPermesos == null || organProcedimentsComunsIdsPermesos.isEmpty() ? null : organProcedimentsComunsIdsPermesos,	
						!procedimentsComunsIds.isEmpty() ? procedimentsComunsIds : null,
						metaExpedientFiltre == null,
						metaExpedientFiltre,
						metaExpedientIdDomini == null || metaExpedientIdDomini.isEmpty(),
						!metaExpedientIdDomini.isEmpty() ? metaExpedientIdDomini : null,
						organGestorFiltre == null,
						organGestorFiltre,
						filtre.getNumero() == null || "".equals(filtre.getNumero().trim()),
						filtre.getNumero() != null ? filtre.getNumero().trim() : "",
						filtre.getNom() == null || filtre.getNom().isEmpty(),
						filtre.getNom() != null ? filtre.getNom().trim() : "",
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
						filtre.getSearch() != null ? filtre.getSearch().trim() : "",
						filtre.getTipusId() == null,
						filtre.getTipusId(),
						esNullExpedientsToBeExcluded,
						expedientsToBeExluded,
						filtre.getInteressat() == null || filtre.getInteressat().isEmpty(),
						filtre.getInteressat() != null ? filtre.getInteressat().trim() : "",
						filtre.getMetaExpedientDominiValor() == null || filtre.getMetaExpedientDominiValor().isEmpty(),
						filtre.getMetaExpedientDominiValor() != null ? filtre.getMetaExpedientDominiValor().trim() : "",
						esNullRolsCurrentUser,
						rolsCurrentUser,
						pageable);
				logger.trace("findByEntitatAndPermesosAndFiltre time:  " + (System.currentTimeMillis() - t10) + " ms");
				long t11 = System.currentTimeMillis();
				PaginaDto<ExpedientDto> paginaDto = paginacioHelper.toPaginaDto(
						paginaExpedients,
						ExpedientDto.class,
						rolActual,
						new ConverterParam<ExpedientEntity, ExpedientDto>() {
							@Override
							public ExpedientDto convert(ExpedientEntity source, String param) {
								return expedientHelper.toExpedientDto(source, false, param, true);
							}
						});
				for (ExpedientDto expedient: paginaDto) {
					boolean enAlerta = alertaRepository.countByLlegidaAndContingutId(false, expedient.getId()) > 0;
					expedient.setAlerta(enAlerta);
				}
				result.setPagina(paginaDto);
				logger.trace("toPaginaDto time:  " + (System.currentTimeMillis() - t11) + " ms");			
				logger.trace("findAmbFiltrePaginat (" + (paginaDto != null ? paginaDto.getTamany() + "/" + paginaDto.getElementsTotal() : "0")  +") time:  " + (System.currentTimeMillis() - t0) + " ms");

			} else {
				
				// ==================================  RETURNS IDS (SELECCIONAR TOTS) ============================================
				List<Long> expedientsIds = expedientRepository.findIdsByEntitatAndFiltre(
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
						filtre.getNumero() != null ? filtre.getNumero().trim() : "",
						filtre.getNom() == null || filtre.getNom().isEmpty(),
						filtre.getNom() != null ? filtre.getNom().trim() : "",
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
						filtre.getSearch() != null ? filtre.getSearch().trim() : "",
						filtre.getTipusId() == null,
						filtre.getTipusId(),
						true,
						null,
						filtre.getInteressat() == null || filtre.getInteressat().isEmpty(),
						filtre.getInteressat() != null ? filtre.getInteressat().trim() : "",
						filtre.getMetaExpedientDominiValor() == null || filtre.getMetaExpedientDominiValor().isEmpty(),
						filtre.getMetaExpedientDominiValor() != null ? filtre.getMetaExpedientDominiValor().trim() : "",
						esNullRolsCurrentUser,
						rolsCurrentUser);
				result.setIds(expedientsIds);
				
				logger.trace("findAmbFiltrePaginat ids (size: " + expedientsIds.size()  +") time:  " + (System.currentTimeMillis() - t0) + " ms");
			}
			
			
			return result;
		/*} else {
			return paginacioHelper.getPaginaDtoBuida(ExpedientDto.class);
		}*/
	}


	@Override
	@Transactional(readOnly = true)
	public PaginaDto<ExpedientDto> findExpedientMetaExpedientPaginat(
			Long entitatId,
			Long metaExpedientId,
			PaginacioParamsDto paginacioParams) {
		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false, true, false);
		MetaExpedientEntity metaExpedientFiltre = entityComprovarHelper.comprovarMetaExpedient(entitat, metaExpedientId);
		
		Map<String, String[]> ordenacioMap = new HashMap<String, String[]>();
		ordenacioMap.put("numero", new String[] { "codi", "any", "sequencia" });
		
		Page<ExpedientEntity> paginaExpedients = expedientRepository.findByMetaExpedientAndEsborrat(metaExpedientFiltre, 0, paginacioHelper.toSpringDataPageable(paginacioParams, ordenacioMap));
		return paginacioHelper.toPaginaDto(
				paginaExpedients,
				ExpedientDto.class,
				new Converter<ExpedientEntity, ExpedientDto>() {
					@Override
					public ExpedientDto convert(ExpedientEntity source) {
						return expedientHelper.toExpedientDto(source);
					}
				});
	}
	
	@Transactional
	@Override
	public PaginaDto<ExpedientDto> relacioFindAmbExpedientPaginat(
			Long entitatId, 
			ExpedientFiltreDto filtre,
			Long expedientId,
			PaginacioParamsDto paginacioDtoFromRequest) {
		logger.debug("Obtenint la pàgina d'expedients relacionats (" +
				"entitatId=" + entitatId + ", " +
				"expedientId=" + expedientId + ")");
		long t0 = System.currentTimeMillis();
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId,
				expedientId,
				false,
				true,
				false,
				false,
				false, false, null);
		Page<ExpedientEntity> paginaExpedientsRelacionats = null;
		Map<String, String[]> ordenacioMap = new HashMap<String, String[]>();
		ordenacioMap.put("numero", new String[] { "codi", "any", "sequencia" });
		Pageable pageable = paginacioHelper.toSpringDataPageable(paginacioDtoFromRequest, ordenacioMap);
		List<Long> expedientsRelacionatsIdx = new ArrayList<Long>();
		for (ExpedientEntity expedientRelacionatAmb: expedient.getRelacionatsAmb()) {
			expedientsRelacionatsIdx.add(expedientRelacionatAmb.getId());
		}
		for (ExpedientEntity expedientRelacionatPer: expedient.getRelacionatsPer()) {
			expedientsRelacionatsIdx.add(expedientRelacionatPer.getId());
		}
		if (!expedientsRelacionatsIdx.isEmpty()) {
			long t1 = System.currentTimeMillis();
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false, true, false);
			logger.trace("comprovarEntitat time:  " + (System.currentTimeMillis() - t1) + " ms");
			// metaexpedient
			MetaExpedientEntity metaExpedientFiltre = null;
			if (filtre.getMetaExpedientId() != null) {
				long t2 = System.currentTimeMillis();
				metaExpedientFiltre = entityComprovarHelper.comprovarMetaExpedientPerExpedient(
						entitat,
						filtre.getMetaExpedientId(),
						true,
						false,
						false,
						false,
						false, 
						"tothom",
						null);
				logger.trace("comprovarMetaExpedientPerExpedient time:  " + (System.currentTimeMillis() - t2) + " ms");
			}
			// estats
			ExpedientEstatEnumDto chosenEstatEnum = null;
			ExpedientEstatEntity chosenEstat = null;
			Long estatId = filtre.getExpedientEstatId();
			if (estatId != null) {
				long t3 = System.currentTimeMillis();
				if (estatId.intValue() <= 0) { // if estat is 0 or less the given estat is enum
					int estatIdInt = -estatId.intValue();
					chosenEstatEnum = ExpedientEstatEnumDto.values()[estatIdInt];
				} else { // given estat is estat from database
					chosenEstat = expedientEstatRepository.findOne(estatId);
				}
				logger.trace("getEstat time:  " + (System.currentTimeMillis() - t3) + " ms");
			}
			
			long t4 = System.currentTimeMillis();
			paginaExpedientsRelacionats = expedientRepository.findExpedientsRelacionatsByIdIn(
				entitat,
				metaExpedientFiltre == null,
				metaExpedientFiltre,
				filtre.getNumero() == null || "".equals(filtre.getNumero().trim()),
				filtre.getNumero() != null ? filtre.getNumero().trim() : "",
				filtre.getNom() == null || filtre.getNom().isEmpty(),
				filtre.getNom() != null ? filtre.getNom().trim() : "",
				chosenEstatEnum == null,
				chosenEstatEnum,
				chosenEstat == null,
				chosenEstat,
				expedientsRelacionatsIdx,
				pageable);
			logger.trace("findExpedientsRelacionatsByIdIn time:  " + (System.currentTimeMillis() - t4) + " ms");
			
			long t5 = System.currentTimeMillis();
			PaginaDto<ExpedientDto> paginaDto = paginacioHelper.toPaginaDto(
					paginaExpedientsRelacionats,
					ExpedientDto.class,
					"tothom",
					new ConverterParam<ExpedientEntity, ExpedientDto>() {
						@Override
						public ExpedientDto convert(ExpedientEntity source, String param) {
							return expedientHelper.toExpedientDto(source, false, param, true);
						}
					});
			logger.trace("toPaginaDto time:  " + (System.currentTimeMillis() - t5) + " ms");
			logger.trace("relacioFindAmbExpedientPaginat ids (size: " + expedientsRelacionatsIdx.size()  +") time:  " + (System.currentTimeMillis() - t0) + " ms");
			return paginaDto;
		} else {
			return paginacioHelper.getPaginaDtoBuida(ExpedientDto.class);
		}
	}

	@Transactional
	@Override
	public void importarExpedient(
			Long entitatId, 
			Long pareId, 
			Long expedientId, 
			String rolActual)
			throws NotFoundException {
		logger.debug("Important un expedient relacionat a la llista de documents (" +
				"entitatId=" + entitatId + ", " +
				"pareId=" + pareId + "," + 
				"expedientId=" + expedientId + ")");
		if (!isImportacioRelacionatsActiva()) {
			throw new ValidationException("La importació d'expedients relacionats no està activa");
		}
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false, true, false);
		ContingutEntity contingutPare = entityComprovarHelper.comprovarContingut(
				entitat,
				pareId);
		ExpedientEntity expedientFill = entityComprovarHelper.comprovarExpedient(
				entitatId,
				expedientId,
				false,
				true,
				false,
				false,
				false,
				false,
				null);
		CarpetaEntity expedientFillExists = carpetaRepository.findByPareAndExpedientRelacionatAndEsborrat(contingutPare, expedientFill, 0);
		if (expedientFillExists != null) {
			throw new ValidationException("L'expedient " + expedientFillExists.getNom() + " s'ha importat prèviament");
		}
		// Crear l'expedient a importar com una carpeta de l'expedient pare
		CarpetaDto expedientFillImported = carpetaHelper.create(
				entitatId, 
				pareId, 
				expedientFill.getNom(),
				false,
				null,
				false,
				null,
				false,
				rolActual);
		CarpetaEntity expedientFillImportedEntity = carpetaRepository.findOne(expedientFillImported.getId());
		expedientFillImportedEntity.updateExpedientRelacionat(expedientFill);
	}

	@Override
	public boolean esborrarExpedientFill(Long entitatId, Long expedientPareId, Long expedientId, String rolActual)
			throws NotFoundException {
		// TODO Auto-generated method stub
		return false;
	}

	
	private boolean isIncorporacioDuplicadaPermesa() {
		return configHelper.getAsBoolean("es.caib.ripea.incorporacio.anotacions.duplicada");
	}
	
	private boolean isProgaparRelacioActiva() {
		return configHelper.getAsBoolean("es.caib.ripea.propagar.relacio.expedients");
	}
	
	private boolean isIncorporacioJustificantActiva() {
		return configHelper.getAsBoolean("es.caib.ripea.incorporar.justificant");
	}
	
	private boolean isImportacioRelacionatsActiva() {
		return configHelper.getAsBoolean("es.caib.ripea.importacio.expedient.relacionat.activa");
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
