package es.caib.ripea.core.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.caib.ripea.core.api.dto.ArbreDto;
import es.caib.ripea.core.api.dto.CrearReglaDistribucioEstatEnumDto;
import es.caib.ripea.core.api.dto.CrearReglaResponseDto;
import es.caib.ripea.core.api.dto.DominiDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.ExpedientEstatDto;
import es.caib.ripea.core.api.dto.GrupDto;
import es.caib.ripea.core.api.dto.MetaDadaDto;
import es.caib.ripea.core.api.dto.MetaDadaTipusEnumDto;
import es.caib.ripea.core.api.dto.MetaDocumentDto;
import es.caib.ripea.core.api.dto.MetaExpedientAmbitEnumDto;
import es.caib.ripea.core.api.dto.MetaExpedientCarpetaDto;
import es.caib.ripea.core.api.dto.MetaExpedientComentariDto;
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.dto.MetaExpedientExportDto;
import es.caib.ripea.core.api.dto.MetaExpedientFiltreDto;
import es.caib.ripea.core.api.dto.MetaExpedientRevisioEstatEnumDto;
import es.caib.ripea.core.api.dto.MetaExpedientTascaDto;
import es.caib.ripea.core.api.dto.MetaExpedientTascaValidacioDto;
import es.caib.ripea.core.api.dto.OrganGestorDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.PermisDto;
import es.caib.ripea.core.api.dto.PermissionEnumDto;
import es.caib.ripea.core.api.dto.PrincipalTipusEnumDto;
import es.caib.ripea.core.api.dto.ProcedimentDto;
import es.caib.ripea.core.api.dto.ProgresActualitzacioDto;
import es.caib.ripea.core.api.dto.ReglaDistribucioDto;
import es.caib.ripea.core.api.dto.StatusEnumDto;
import es.caib.ripea.core.api.exception.ExisteixenExpedientsEsborratsException;
import es.caib.ripea.core.api.exception.ExisteixenExpedientsException;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.exception.PermissionDeniedException;
import es.caib.ripea.core.api.service.MetaExpedientService;
import es.caib.ripea.core.api.utils.Utils;
import es.caib.ripea.core.entity.DominiEntity;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.ExpedientEstatEntity;
import es.caib.ripea.core.entity.GrupEntity;
import es.caib.ripea.core.entity.HistoricExpedientEntity;
import es.caib.ripea.core.entity.HistoricInteressatEntity;
import es.caib.ripea.core.entity.HistoricUsuariEntity;
import es.caib.ripea.core.entity.MetaDadaEntity;
import es.caib.ripea.core.entity.MetaDocumentEntity;
import es.caib.ripea.core.entity.MetaExpedientComentariEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.MetaExpedientOrganGestorEntity;
import es.caib.ripea.core.entity.MetaExpedientTascaEntity;
import es.caib.ripea.core.entity.MetaExpedientTascaValidacioEntity;
import es.caib.ripea.core.entity.MetaNodeEntity;
import es.caib.ripea.core.entity.OrganGestorEntity;
import es.caib.ripea.core.entity.UsuariEntity;
import es.caib.ripea.core.helper.CacheHelper;
import es.caib.ripea.core.helper.ConfigHelper;
import es.caib.ripea.core.helper.ConversioTipusHelper;
import es.caib.ripea.core.helper.DistribucioReglaHelper;
import es.caib.ripea.core.helper.DominiHelper;
import es.caib.ripea.core.helper.EntityComprovarHelper;
import es.caib.ripea.core.helper.ExceptionHelper;
import es.caib.ripea.core.helper.ExpedientEstatHelper;
import es.caib.ripea.core.helper.GrupHelper;
import es.caib.ripea.core.helper.MessageHelper;
import es.caib.ripea.core.helper.MetaDadaHelper;
import es.caib.ripea.core.helper.MetaDocumentHelper;
import es.caib.ripea.core.helper.MetaExpedientCarpetaHelper;
import es.caib.ripea.core.helper.MetaExpedientHelper;
import es.caib.ripea.core.helper.MetaNodeHelper;
import es.caib.ripea.core.helper.PaginacioHelper;
import es.caib.ripea.core.helper.PaginacioHelper.Converter;
import es.caib.ripea.core.helper.PaginacioHelper.ConverterParam;
import es.caib.ripea.core.helper.PermisosHelper;
import es.caib.ripea.core.helper.PluginHelper;
import es.caib.ripea.core.helper.UsuariHelper;
import es.caib.ripea.core.repository.DominiRepository;
import es.caib.ripea.core.repository.ExpedientEstatRepository;
import es.caib.ripea.core.repository.ExpedientRepository;
import es.caib.ripea.core.repository.GrupRepository;
import es.caib.ripea.core.repository.MetaExpedientComentariRepository;
import es.caib.ripea.core.repository.MetaExpedientOrganGestorRepository;
import es.caib.ripea.core.repository.MetaExpedientRepository;
import es.caib.ripea.core.repository.MetaExpedientTascaRepository;
import es.caib.ripea.core.repository.MetaExpedientTascaValidacioRepository;
import es.caib.ripea.core.repository.OrganGestorRepository;
import es.caib.ripea.core.repository.UsuariRepository;
import es.caib.ripea.core.repository.historic.HistoricExpedientRepository;
import es.caib.ripea.core.repository.historic.HistoricInteressatRepository;
import es.caib.ripea.core.repository.historic.HistoricUsuariRepository;
import es.caib.ripea.core.security.ExtendedPermission;

/**
 * Implementació del servei de gestió de meta-expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class MetaExpedientServiceImpl implements MetaExpedientService {

	@Autowired private MetaExpedientRepository metaExpedientRepository;
	@Autowired private ExpedientEstatRepository expedientEstatRepository;
	@Autowired private MetaExpedientTascaRepository metaExpedientTascaRepository;
	@Autowired private ConversioTipusHelper conversioTipusHelper;
	@Autowired private MetaNodeHelper metaNodeHelper;
	@Autowired private PaginacioHelper paginacioHelper;
	@Autowired private PermisosHelper permisosHelper;
	@Autowired private EntityComprovarHelper entityComprovarHelper;
	@Autowired private MetaExpedientHelper metaExpedientHelper;
	@Autowired private OrganGestorRepository organGestorRepository;
	@Autowired private ExpedientRepository expedientRepository;
	@Autowired private UsuariRepository usuariRepository;
	@Autowired private MetaExpedientCarpetaHelper metaExpedientCarpetaHelper;
	@Autowired private PluginHelper pluginHelper;
	@Autowired private MetaExpedientOrganGestorRepository metaExpedientOrganGestorRepository;
	@Autowired private UsuariHelper usuariHelper;
	@Autowired private ConfigHelper configHelper;
	@Autowired private MetaExpedientComentariRepository metaExpedientComentariRepository;
	@Autowired private ExpedientEstatHelper expedientEstatHelper;
	@Autowired private GrupHelper grupHelper;
	@Autowired private GrupRepository grupRepository;
	@Autowired private DominiRepository dominiRepository;
	@Autowired private DominiHelper dominiHelper;
	@Autowired private HistoricExpedientRepository historicExpedientRepository;
	@Autowired private HistoricInteressatRepository historicInteressatRepository;
	@Autowired private HistoricUsuariRepository historicUsuariRepository;
	@Autowired private MetaExpedientTascaValidacioRepository metaExpedientTascaValidacioRepository;
	@Autowired private DistribucioReglaHelper distribucioReglaHelper;
	@Autowired private CacheHelper cacheHelper;
	@Resource private MetaDocumentHelper metaDocumentHelper;
	@Resource private MetaDadaHelper metaDadaHelper;

	public static Map<String, ProgresActualitzacioDto> progresActualitzacio = new HashMap<>();
//	public static Map<Long, Integer> metaExpedientsAmbOrganNoSincronitzat = new HashMap<>();

	@Transactional
	@Override
	public MetaExpedientDto create(Long entitatId, MetaExpedientDto metaExpedient, String rolActual, Long organId) {
		logger.debug(
				"Creant un nou meta-expedient (" + "entitatId=" + entitatId + ", " + "metaExpedient=" + metaExpedient +
						")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		if (metaExpedient.getOrganGestor() != null) {
			entityComprovarHelper.comprovarOrganGestorAdmin(
					entitatId,
					metaExpedient.getOrganGestor().getId());
		}
		MetaExpedientEntity metaExpedientPare = null;
		if (metaExpedient.getPareId() != null) {

		metaExpedientPare = entityComprovarHelper.comprovarMetaExpedient(entitat, metaExpedient.getPareId());
		}
		Long organGestorId = metaExpedient.getOrganGestor() != null ? metaExpedient.getOrganGestor().getId() : null;
		MetaExpedientEntity entity = MetaExpedientEntity.getBuilder(
				metaExpedient.getCodi(),
				metaExpedient.getNom(),
				metaExpedient.getDescripcio(),
				metaExpedient.getSerieDocumental(),
				metaExpedient.getClassificacio(),
				metaExpedient.isNotificacioActiva(),
				metaExpedient.isPermetMetadocsGenerals(),
				entitat,
				metaExpedientPare,
				organGestorId == null ? null : organGestorRepository.findOne(organGestorId),
				metaExpedient.isGestioAmbGrupsActiva(),
				metaExpedient.isInteressatObligatori()).
				expressioNumero(metaExpedient.getExpressioNumero()).
				tipusClassificacio(metaExpedient.getTipusClassificacio()).build();
		MetaExpedientEntity metaExpedientEntity = metaExpedientRepository.save(entity);
		if (metaExpedient.getEstructuraCarpetes() != null) {
			//crear estructura carpetes per defecte
			metaExpedientHelper.crearEstructuraCarpetes(
					metaExpedient.getEstructuraCarpetes(), 
					metaExpedientEntity);
		}
		
		MetaExpedientDto metaExpedientDto = conversioTipusHelper.convertir(metaExpedientEntity, MetaExpedientDto.class);
		if ("IPA_ORGAN_ADMIN".equals(rolActual)) {
			metaExpedientHelper.canviarRevisioADisseny(entitatId, metaExpedientEntity.getId(), organId);
		} else {
			metaExpedientEntity.updateRevisioEstat(MetaExpedientRevisioEstatEnumDto.REVISAT);
			if (metaExpedient.isCrearReglaDistribucio()) {
				metaExpedientDto.setCrearReglaResponse(metaExpedientHelper.crearReglaDistribucio(metaExpedientEntity.getId()));
			}
		}
		return metaExpedientDto;
	}

	@Transactional
	@Override
	public MetaExpedientDto update(Long entitatId, MetaExpedientDto metaExpedient, String rolActual, MetaExpedientRevisioEstatEnumDto estatAnterior, Long organId) {
		logger.debug(
				"Actualitzant meta-expedient existent (" + "entitatId=" + entitatId + ", " + "metaExpedient=" +
						metaExpedient + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		MetaExpedientEntity metaExpedientEntity;
		MetaExpedientEntity metaExpedientPare = null;
		metaExpedientEntity = entityComprovarHelper.comprovarMetaExpedientAdmin(entitat, metaExpedient.getId(), organId);

		if (metaExpedient.getPareId() != null) {
			metaExpedientPare = entityComprovarHelper.comprovarMetaExpedientAdmin(entitat, metaExpedient.getPareId(), null);
		}

		Long organGestorId = metaExpedient.getOrganGestor() != null ? metaExpedient.getOrganGestor().getId() : null;
		metaExpedientEntity.update(
				metaExpedient.getCodi(),
				metaExpedient.getNom(),
				metaExpedient.getDescripcio(),
				metaExpedient.getClassificacio(),
				metaExpedient.getSerieDocumental(),
				metaExpedient.getExpressioNumero(),
				metaExpedient.isNotificacioActiva(),
				metaExpedient.isPermetMetadocsGenerals(),
				metaExpedientPare,
				organGestorId == null ? null : organGestorRepository.findOne(organGestorId),
				metaExpedient.isGestioAmbGrupsActiva(), 
				metaExpedient.getTipusClassificacio(),
				metaExpedient.isInteressatObligatori());
		
		if (metaExpedient.getEstructuraCarpetes() != null) {
			//crear estructura carpetes per defecte
			metaExpedientHelper.crearEstructuraCarpetes(
					metaExpedient.getEstructuraCarpetes(), 
					metaExpedientEntity);
		}
		
		List<ExpedientEntity> expedients = expedientRepository.findByMetaExpedientAndEsborrat(metaExpedientEntity, 0);
		
		long t0 = System.currentTimeMillis();
		logger.info("MetaExpedientServiceImpl.update evictErrorsValidacioPerNode start (total expedients:" + (expedients.size()) + "");
		
		for (ExpedientEntity expedient: expedients) {
			cacheHelper.evictErrorsValidacioPerNode(expedient);
		}
		
		if (cacheHelper.mostrarLogsRendiment())
			logger.info("MetaExpedientServiceImpl.update evictErrorsValidacioPerNode end:  " + (System.currentTimeMillis() - t0) + " ms");
		
		if ("IPA_ORGAN_ADMIN".equals(rolActual)) {
			if (estatAnterior == MetaExpedientRevisioEstatEnumDto.DISSENY && metaExpedient.getRevisioEstat() == MetaExpedientRevisioEstatEnumDto.PENDENT)
				marcarPendentRevisio(entitatId,  metaExpedientEntity.getId(), organId);
			else 
				metaExpedientHelper.canviarRevisioADisseny(entitatId, metaExpedientEntity.getId(), organId);
		} else if ("IPA_ADMIN".equals(rolActual)){
			metaExpedientHelper.canviarEstatRevisioASellecionat(entitatId, metaExpedient);
		}
		return conversioTipusHelper.convertir(metaExpedientEntity, MetaExpedientDto.class);
	}
	
	@Transactional
	@Override
	public MetaExpedientDto canviarEstatRevisioASellecionat(
			Long entitatId,
			MetaExpedientDto metaExpedient,
			String rolActual) {
		logger.debug("Canviant estat revicio meta-expedient (" + "entitatId=" + entitatId + ", " + "metaExpedient=" + metaExpedient + ")");

		if (metaExpedient.getRevisioComentari() != null && !metaExpedient.getRevisioComentari().isEmpty()) {
			metaExpedientHelper.publicarComentariPerMetaExpedient(
					entitatId,
					metaExpedient.getId(),
					metaExpedient.getRevisioComentari(),
					rolActual);
		}
		
		return metaExpedientHelper.canviarEstatRevisioASellecionat(entitatId, metaExpedient);
	}

	@Transactional
	@Override
	public void createFromImport(Long entitatId, MetaExpedientExportDto procedimentImportat, String rolActual, Long organId) {
		
		logger.debug("Creant un nou meta-expedient (" + "entitatId=" + entitatId + ", " + "metaExpedient=" + procedimentImportat + ")");
		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		if (procedimentImportat.getOrganGestor() != null) {
			entityComprovarHelper.comprovarOrganGestorAdmin(entitatId, procedimentImportat.getOrganGestor().getId());
		}
		
		MetaExpedientEntity metaExpedientPare = null;
		if (procedimentImportat.getPareId() != null) {
			metaExpedientPare = entityComprovarHelper.comprovarMetaExpedient(entitat, procedimentImportat.getPareId());
		}
		
		Long organGestorId = procedimentImportat.getOrganGestor() != null ? procedimentImportat.getOrganGestor().getId() : null;
		
		MetaExpedientEntity entity = MetaExpedientEntity.getBuilder(
				procedimentImportat.getCodi(),
				procedimentImportat.getNom(),
				procedimentImportat.getDescripcio(),
				procedimentImportat.getSerieDocumental(),
				procedimentImportat.getClassificacio(),
				procedimentImportat.isNotificacioActiva(),
				procedimentImportat.isPermetMetadocsGenerals(),
				entitat,
				metaExpedientPare,
				organGestorId == null ? null : organGestorRepository.findOne(organGestorId),
				procedimentImportat.isGestioAmbGrupsActiva(),
				procedimentImportat.isInteressatObligatori()).
				expressioNumero(procedimentImportat.getExpressioNumero()).
				tipusClassificacio(procedimentImportat.getTipusClassificacio()).build();
		
		MetaExpedientEntity metaExpedientEntity = metaExpedientRepository.save(entity);
		if (procedimentImportat.getEstructuraCarpetes() != null) {
			//crear estructura carpetes per defecte
			metaExpedientHelper.crearEstructuraCarpetes(
					procedimentImportat.getEstructuraCarpetes(), 
					metaExpedientEntity);
		}
		
		if ("IPA_ORGAN_ADMIN".equals(rolActual)) {
			metaExpedientHelper.canviarRevisioADisseny(entitatId, metaExpedientEntity.getId(), organId);
		} else {
			metaExpedientEntity.updateRevisioEstat(MetaExpedientRevisioEstatEnumDto.REVISAT);
		}

		if (procedimentImportat.getMetaDocuments() != null) {
			for (MetaDocumentDto metaDocumentDto : procedimentImportat.getMetaDocuments()) {
				MetaDocumentDto metaDocumentCreated = metaDocumentHelper.create(entitatId, entity.getId(), metaDocumentDto, metaDocumentDto.getPlantillaNom(), metaDocumentDto.getPlantillaContentType(), metaDocumentDto.getPlantillaContingut(), rolActual, organId);
				
				if (metaDocumentDto.getMetaDades() != null) {
					for (MetaDadaDto metaDadaDto : metaDocumentDto.getMetaDades()) {
						if (metaDadaDto.getTipus() == MetaDadaTipusEnumDto.DOMINI) {
							List<DominiEntity> dominis = dominiRepository.findByEntitatAndCodi(entitat, metaDadaDto.getCodi());
							if (dominis == null || dominis.isEmpty() && metaDadaDto.getDomini() != null) {
								dominiHelper.create(entitatId, metaDadaDto.getDomini(), false);
							}
						}
						metaDadaHelper.create(entitatId, metaDocumentCreated.getId(), metaDadaDto, rolActual, organId);
					}
				}
			}
		}
		
		if (procedimentImportat.getMetaDades() != null) {
			for (MetaDadaDto metaDadaDto : procedimentImportat.getMetaDades()) {
				if (metaDadaDto.getTipus() == MetaDadaTipusEnumDto.DOMINI) {
					List<DominiEntity> dominis = dominiRepository.findByEntitatAndCodi(entitat, metaDadaDto.getCodi());
					if (dominis == null || dominis.isEmpty() && metaDadaDto.getDomini() != null) {
						dominiHelper.create(entitatId, metaDadaDto.getDomini(), false);
					}
				}
				metaDadaHelper.create(entitatId, entity.getId(), metaDadaDto, rolActual, organId);
			}
		}
		
		if (procedimentImportat.getEstats() != null) {
			for (ExpedientEstatDto expedientEstatDto : procedimentImportat.getEstats()) {
				expedientEstatDto.setMetaExpedientId(entity.getId());
				ExpedientEstatDto expedientEstatCreated = expedientEstatHelper.createExpedientEstat(
						entitatId,
						expedientEstatDto,
						rolActual,
						organId);
				
				if (procedimentImportat.getTasques() != null) {
					for (MetaExpedientTascaDto metaExpedientTascaDto : procedimentImportat.getTasques()) {
						if (metaExpedientTascaDto.getEstatIdCrearTasca() != null && metaExpedientTascaDto.getEstatIdCrearTasca().equals(expedientEstatDto.getId())) {
							metaExpedientTascaDto.setEstatIdCrearTasca(expedientEstatCreated.getId());
						}
						if (metaExpedientTascaDto.getEstatIdFinalitzarTasca() != null && metaExpedientTascaDto.getEstatIdFinalitzarTasca().equals(expedientEstatDto.getId())) {
							metaExpedientTascaDto.setEstatIdFinalitzarTasca(expedientEstatCreated.getId());
						}
					}
				}
			}
		}
		
		if (procedimentImportat.getTasques() != null) {
			for (MetaExpedientTascaDto metaExpedientTascaDto : procedimentImportat.getTasques()) {
				metaExpedientHelper.tascaCreate(
						entitatId,
						entity.getId(),
						metaExpedientTascaDto,
						rolActual,
						organId);
			}
		}
		
		if (procedimentImportat.getGrups() != null) {
			for (GrupDto grupDto : procedimentImportat.getGrups()) {
				
				GrupEntity grup = grupRepository.findByEntitatIdAndCodi(entitatId, grupDto.getCodi());

				if (grup == null) {
					GrupDto grupCreated = grupHelper.create(entitatId, grupDto);
					grup = grupRepository.findOne(grupCreated.getId());
				}
				entity.addGrup(grup);
			}
		}
	}

	@Override
	@Transactional
	public void updateFromImport(Long entitatId, MetaExpedientExportDto procedimentImportat, String rolActual, Long organId) {
		
		logger.debug("Actualitzant un procediment existent desde importació (" + "entitatId=" + entitatId + ", " + "metaExpedient=" + procedimentImportat + ")");
		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		if (procedimentImportat.getOrganGestor() != null) {
			entityComprovarHelper.comprovarOrganGestorAdmin(
					entitatId,
					procedimentImportat.getOrganGestor().getId());
		}
		
		MetaExpedientEntity metaExpedientEntity = entityComprovarHelper.comprovarMetaExpedient(entitat, procedimentImportat.getId());
		Long organGestorId = procedimentImportat.getOrganGestor() != null ? procedimentImportat.getOrganGestor().getId() : null;
		OrganGestorEntity organGestorEntity = organGestorId == null ? null : organGestorRepository.findOne(organGestorId);
		
		metaExpedientEntity.update(
				metaExpedientEntity.getCodi(), //El codi no s'actualitza, s'agafa del entity no del metaExpedientDto
				procedimentImportat.getNom(),
				procedimentImportat.getDescripcio(),
				procedimentImportat.getClassificacio(),
				procedimentImportat.getSerieDocumental(),
				procedimentImportat.getExpressioNumero(),
				procedimentImportat.isNotificacioActiva(),
				procedimentImportat.isPermetMetadocsGenerals(),
				metaExpedientEntity.getPare(), //El pare no s'actualitza, s'agafa del entity no del metaExpedientDto
				organGestorEntity,
				procedimentImportat.isGestioAmbGrupsActiva(),
				procedimentImportat.getTipusClassificacio(),
				procedimentImportat.isInteressatObligatori());
		
		Long metaExpedientEntityId = metaExpedientEntity.getId();

		//Actualitzar estructura carpetes
		if (procedimentImportat.getEstructuraCarpetes() != null) {
			metaExpedientHelper.crearEstructuraCarpetes(procedimentImportat.getEstructuraCarpetes(), metaExpedientEntity);
		}
		
		/**
		 * ACTUALITZACIO   de   D O C U M E N T S
		 */
		if (procedimentImportat.getMetaDocuments() != null) {
			
			for (MetaDocumentDto metaDocumentDto : procedimentImportat.getMetaDocuments()) {
				
				//Cercar el metaDocument per codi dins el procediment actual, si existeix s'actualitza, sino es crea
				MetaDocumentEntity mdE = metaDocumentHelper.findByCodiAndProcediment(metaExpedientEntity, metaDocumentDto.getCodi());
				Long metaDocumentCreatedId = null;
				
				if (mdE!=null) {
					metaDocumentCreatedId = metaDocumentHelper.update(
						metaExpedientEntityId,
						metaDocumentDto,
						metaDocumentDto.getPlantillaNom(),
						metaDocumentDto.getPlantillaContentType(),
						metaDocumentDto.getPlantillaContingut(),
						rolActual,
						organId).getId();
				} else {
					metaDocumentCreatedId = metaDocumentHelper.create(
							entitatId,
							metaExpedientEntityId,
							metaDocumentDto,
							metaDocumentDto.getPlantillaNom(),
							metaDocumentDto.getPlantillaContentType(),
							metaDocumentDto.getPlantillaContingut(),
							rolActual,
							organId).getId();
				}
				
				//Actualitzar les metadades del document
				if (metaDocumentDto.getMetaDades() != null) {
					for (MetaDadaDto metaDadaDto : metaDocumentDto.getMetaDades()) {
						//Si la metadada es de tipus domini, comprovam que el domini esta creat
						if (MetaDadaTipusEnumDto.DOMINI.equals(metaDadaDto.getTipus())) {
							
							List<DominiEntity> dominis = null;
							//TODO: El tema dels dominis no esta funcionant bé. No es pot recuperar el domini de una metadada perque no hi ha FK.
							
							if (metaDadaDto.getDomini()!=null) {
							
								/*if (metaDadaDto.getDomini().getCodi()!=null) {
									dominis = dominiRepository.findByEntitatAndCodi(entitat, metaDadaDto.getDomini().getCodi());
								}
								
								if (dominis == null || dominis.isEmpty()) {
									dominiHelper.create(entitatId, metaDadaDto.getDomini(), false);
								}*/
							}
						}
						MetaDadaEntity metaDadaEntity = metaDadaHelper.findByMetaNodeAndCodi(mdE, metaDadaDto.getCodi());
						if (metaDadaEntity==null) {
							metaDadaHelper.create(entitatId, metaDocumentCreatedId, metaDadaDto, rolActual, organId);
						} else {
							metaDadaEntity.update(metaDadaDto);
						}
					}
				}
			}
		} // FI METADOCUMENTS
		
		/**
		 * ACTUALITZACIO   de   M E T A D A D E S
		 */
		if (procedimentImportat.getMetaDades() != null) {
			
			for (MetaDadaDto metaDadaDto : procedimentImportat.getMetaDades()) {
				
				if (MetaDadaTipusEnumDto.DOMINI.equals(metaDadaDto.getTipus())) {
					
					//TODO: El tema dels dominis no esta funcionant bé. No es pot recuperar el domini de una metadada perque no hi ha FK.
//					List<DominiEntity> dominis = dominiRepository.findByEntitatAndCodi(entitat, metaDadaDto.getCodi());
//					if (dominis == null || dominis.isEmpty() && metaDadaDto.getDomini() != null) {
//						dominiHelper.create(entitatId, metaDadaDto.getDomini(), false);
//					}
				}
				
				MetaDadaEntity metaDadaEntity = metaDadaHelper.findByMetaNodeAndCodi(metaExpedientEntity, metaDadaDto.getCodi());
				if (metaDadaEntity==null) {
					metaDadaHelper.create(entitatId, metaExpedientEntityId, metaDadaDto, rolActual, organId);
				} else {
					metaDadaEntity.update(metaDadaDto);
				}
			}
		}
		
		/**
		 * ACTUALITZACIO   de   E S T A T S
		 */
		if (procedimentImportat.getEstats() != null) {
			
			for (ExpedientEstatDto expedientEstatDto : procedimentImportat.getEstats()) {
				
				//Actualitzam o cream el estat per l'expedient segons el cas
				ExpedientEstatEntity expEstatEntity = expedientEstatHelper.findByMetaExpedientAndCodi(metaExpedientEntity, expedientEstatDto.getCodi());
				Long expedientEstatCreatedId = null;
				if (expEstatEntity==null) {
					expedientEstatDto.setMetaExpedientId(metaExpedientEntityId);
					ExpedientEstatDto expedientEstatCreated = expedientEstatHelper.createExpedientEstat(
							entitatId,
							expedientEstatDto,
							rolActual,
							organId);
					expedientEstatCreatedId = expedientEstatCreated.getId();
				} else {
					expedientEstatHelper.updateExpedientEstat(metaExpedientEntity, expEstatEntity, entitatId, expedientEstatDto, rolActual, organId);
					expedientEstatCreatedId = expedientEstatDto.getId();
				}
				
				//Actualitzam el estat inicial per defecte al crear una tasca amb el ID que acabam de crear/actualitzar
				//Ja que segurament ha canviat respecte al inicial
				if (procedimentImportat.getTasques() != null) {
					for (MetaExpedientTascaDto metaExpedientTascaDto : procedimentImportat.getTasques()) {
						if (metaExpedientTascaDto.getEstatIdCrearTasca() != null && 
							metaExpedientTascaDto.getEstatIdCrearTasca().equals(expedientEstatDto.getId())) {
								metaExpedientTascaDto.setEstatIdCrearTasca(expedientEstatCreatedId);
						}
						if (metaExpedientTascaDto.getEstatIdFinalitzarTasca() != null && 
							metaExpedientTascaDto.getEstatIdFinalitzarTasca().equals(expedientEstatDto.getId())) {
								metaExpedientTascaDto.setEstatIdFinalitzarTasca(expedientEstatCreatedId);
						}
					}
				}
			}
		}
		
		/**
		 * ACTUALITZACIO   de   T A S Q U E S
		 */
		if (procedimentImportat.getTasques() != null) {
			for (MetaExpedientTascaDto metaExpedientTascaDto : procedimentImportat.getTasques()) {
				
				MetaExpedientTascaEntity metaExpTascaEntity = metaExpedientHelper.findTascaByMetaExpedientAndCodi(
						metaExpedientEntity,
						metaExpedientTascaDto.getCodi());
				
				if (metaExpTascaEntity==null) {
					metaExpedientHelper.tascaCreate(
							entitatId,
							metaExpedientEntityId,
							metaExpedientTascaDto,
							rolActual,
							organId);
				} else {
					metaExpedientHelper.tascaUpdate(metaExpTascaEntity, metaExpedientTascaDto);
				}
			}
		}
		
		/**
		 * CREACIO - ACTUALITZACIO   de   G R U P S
		 */
		if (procedimentImportat.getGrups() != null) {
			for (GrupDto grupDto : procedimentImportat.getGrups()) {
				
				GrupEntity grup = grupRepository.findByEntitatIdAndCodi(entitatId, grupDto.getCodi());

				if (grup == null) {
					GrupDto grupCreated = grupHelper.create(entitatId, grupDto);
					grup = grupRepository.findOne(grupCreated.getId());
				} else {
					//El grup no s'actualitza, perque no es una dada del procediment importat com a tal...
				}
				
				metaExpedientEntity.addGrup(grup);
			}
		}
	}
	
	@Transactional
	@Override
	public String export(Long entitatId, Long id, Long organId) {
		logger.debug("Exportant un meta-expedient (" + "id=" + id + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedientAdmin(entitat, id, organId);
		
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			MetaExpedientExportDto metaExpedientDto = conversioTipusHelper.convertir(metaExpedient, MetaExpedientExportDto.class);
			
			if (metaExpedientDto.getMetaDades() != null) {
				for (MetaDadaDto metaDadaDto : metaExpedientDto.getMetaDades()) {
					if (metaDadaDto.getTipus().equals(MetaDadaTipusEnumDto.DOMINI)) {
						List<DominiEntity> dominis = dominiRepository.findByEntitatAndCodi(entitat, metaDadaDto.getCodi());
						if (dominis != null && !dominis.isEmpty()) {
							DominiEntity domini = dominis.get(0);
							metaDadaDto.setDomini(conversioTipusHelper.convertir(domini, DominiDto.class));
						}
					}
				}
			}

			if (metaExpedientDto.getMetaDocuments() != null) {
				for (MetaDocumentDto metaDocumentDto : metaExpedientDto.getMetaDocuments()) {
					if (metaDocumentDto.getMetaDades() != null) {
						for (MetaDadaDto metaDadaDto : metaDocumentDto.getMetaDades()) {
							if (metaDadaDto.getTipus().equals(MetaDadaTipusEnumDto.DOMINI)) {
								List<DominiEntity> dominis = dominiRepository.findByEntitatAndCodi(entitat, metaDadaDto.getCodi());
								if (dominis != null && !dominis.isEmpty()) {
									DominiEntity domini = dominis.get(0);
									metaDadaDto.setDomini(conversioTipusHelper.convertir(domini, DominiDto.class));
								}
							}
						}
					}
				}
			}
			
			String carAsString = objectMapper.writeValueAsString(metaExpedientDto);
			logger.info(carAsString);
			return carAsString;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}
	

	@Transactional
	@Override
	public MetaExpedientDto updateActiu(Long entitatId, Long id, boolean actiu, String rolActual, Long organId) {
		logger.debug(
				"Actualitzant propietat activa d'un meta-expedient existent (" + "entitatId=" + entitatId + ", " +
						"id=" + id + ", " + "actiu=" + actiu + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedientAdmin(entitat, id, organId);
		metaExpedient.updateActiu(actiu);
		
		if (rolActual.equals("IPA_ORGAN_ADMIN")) {
			metaExpedientHelper.canviarRevisioADisseny(entitatId, metaExpedient.getId(), organId);
		}
		return conversioTipusHelper.convertir(metaExpedient, MetaExpedientDto.class);
	}

	@Transactional
	@Override
	public MetaExpedientDto delete(Long entitatId, Long id, Long organId) {
		logger.debug("Esborrant meta-expedient (id=" + id + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		MetaExpedientEntity metaExpedient;
		metaExpedient = entityComprovarHelper.comprovarMetaExpedientAdmin(entitat, id, organId);
		metaExpedientTascaRepository.deleteMetaExpedientTascaByMetaExpedient(metaExpedient);
		List<ExpedientEntity> expedients = expedientRepository.findByMetaExpedient(metaExpedient);
		boolean allEsborats = true;
		for (ExpedientEntity expedientEntity : expedients) {
			if (expedientEntity.getEsborrat() == 0) {
				allEsborats = false;
			}
		}
		if (expedients.size() > 0) {
			if (allEsborats) {
				throw new ExisteixenExpedientsEsborratsException();
			} else {
				throw new ExisteixenExpedientsException();
			}
			
		}
			
		//esborrar les carpetes per defecte
		metaExpedientCarpetaHelper.removeAllCarpetes(metaExpedient);
		
		List<HistoricExpedientEntity> historicsExpedient = historicExpedientRepository.findByMetaExpedient(metaExpedient);
		for (HistoricExpedientEntity historicEntity : historicsExpedient) {
			historicExpedientRepository.delete(historicEntity);
		}
		List<HistoricInteressatEntity> historicsInteressats = historicInteressatRepository.findByMetaExpedient(metaExpedient);
		for (HistoricInteressatEntity historicEntity : historicsInteressats) {
			historicInteressatRepository.delete(historicEntity);
		}
		List<HistoricUsuariEntity> historicsUsuari = historicUsuariRepository.findByMetaExpedient(metaExpedient);
		for (HistoricUsuariEntity historicEntity : historicsUsuari) {
			historicUsuariRepository.delete(historicEntity);
		}

		// Esborra l'expedient de les preferències d'usuari per a evitar errors de FK
		List<UsuariEntity> usuarisAmbAquestProcedient = usuariRepository.findByProcediment(metaExpedient);
		for (UsuariEntity usuari: usuarisAmbAquestProcedient) {
			usuari.updateProcediment(null);
		}

		metaExpedientRepository.delete(metaExpedient);
		return conversioTipusHelper.convertir(
				metaExpedient,
				MetaExpedientDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public MetaExpedientDto findById(Long entitatId, Long id) {
		logger.debug("Consulta del meta-expedient (" + "entitatId=" + entitatId + ", " + "id=" + id + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);

		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(entitat, id);

		MetaExpedientDto resposta = conversioTipusHelper.convertir(metaExpedient, MetaExpedientDto.class);
		if (resposta != null) {
			metaNodeHelper.omplirMetaDadesPerMetaNode(resposta);
			metaExpedientHelper.omplirMetaDocumentsPerMetaExpedient(metaExpedient, resposta);
			resposta.setNumComentaris(metaExpedient.getComentaris().size());
			
			if (metaExpedient.getOrganGestor() != null) {
				resposta.setOrganEstat(metaExpedient.getOrganGestor().getEstat());
				resposta.setOrganTipusTransicio(metaExpedient.getOrganGestor().getTipusTransicio());
				resposta.setOrgansNous(conversioTipusHelper.convertirList(metaExpedient.getOrganGestor().getNous(), OrganGestorDto.class));
			}
		}
		return resposta;
	}
	
	@Transactional(readOnly = true)
	@Override
	public MetaExpedientDto findByIdAmbElements(Long entitatId, Long id, Long adminOrganId) {
		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);

		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(entitat, id);

		MetaExpedientDto resposta = metaExpedientHelper.toMetaExpedientDto(metaExpedient, adminOrganId);
		
		return resposta;
	}
	
	
	@Transactional(readOnly = true)
	@Override
	public ReglaDistribucioDto consultarReglaDistribucio(Long metaExpedientId) {

		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(metaExpedientId);
		
		return distribucioReglaHelper.consultarRegla(metaExpedient.getClassificacio());
	}
	
	@Transactional(readOnly = true)
	@Override
	public boolean isMetaExpedientPendentRevisio(Long entitatId, Long id) {
		logger.debug("Consulta del meta-expedient estat revisio (" + "entitatId=" + entitatId + ", " + "id=" + id + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);

		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(entitat, id);


		return metaExpedient.getRevisioEstat() == MetaExpedientRevisioEstatEnumDto.PENDENT 
				|| metaExpedient.getRevisioEstat() == MetaExpedientRevisioEstatEnumDto.DISSENY;
	}

	@Transactional(readOnly = true)
	@Override
	public MetaExpedientDto getAndCheckAdminPermission(Long entitatId, Long id, Long organId) {
		logger.debug("Consulta del meta-expedient (" + "entitatId=" + entitatId + ", " + "id=" + id + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedientAdmin(entitat, id, organId);
		MetaExpedientDto resposta = conversioTipusHelper.convertir(metaExpedient, MetaExpedientDto.class);
		if (resposta != null) {
			metaNodeHelper.omplirMetaDadesPerMetaNode(resposta);
			metaExpedientHelper.omplirMetaDocumentsPerMetaExpedient(metaExpedient, resposta);
			
			if (metaExpedient.getOrganGestor() != null) {
				resposta.setOrganEstat(metaExpedient.getOrganGestor().getEstat());
				resposta.setOrganTipusTransicio(metaExpedient.getOrganGestor().getTipusTransicio());
				resposta.setOrgansNous(conversioTipusHelper.convertirList(metaExpedient.getOrganGestor().getNous(), OrganGestorDto.class));  
			}
		}
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public MetaExpedientDto findByEntitatCodi(Long entitatId, String codi) {
		logger.debug(
				"Consulta del meta-expedient per entitat i codi (" + "entitatId=" + entitatId + ", " + "codi=" + codi +
						")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		MetaExpedientEntity metaExpedient = metaExpedientRepository.findByEntitatAndCodi(entitat, codi);
		MetaExpedientDto resposta = conversioTipusHelper.convertir(metaExpedient, MetaExpedientDto.class);

		return resposta;
	}

	
	@Transactional(readOnly = true)
	@Override
	public List<MetaExpedientDto> findByClassificacio(Long entitatId, String classificacio) {
		logger.debug(
				"Consulta del meta-expedient per entitat i codi SIA (" + "entitatId=" + entitatId + ", " + "codi=" + classificacio +
						")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		List<MetaExpedientEntity> metaExpedients = metaExpedientRepository.findByEntitatAndClassificacioOrderByNomAsc(entitat, classificacio);
		
		List<MetaExpedientDto> resposta = null;
		if (metaExpedients != null) {
			resposta = conversioTipusHelper.convertirList(metaExpedients, MetaExpedientDto.class);
		}
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public List<MetaExpedientDto> findByEntitat(Long entitatId) {
		logger.debug("Consulta de meta-expedients de l'entitat (" + "entitatId=" + entitatId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		List<MetaExpedientEntity> metaExpedients = metaExpedientRepository.findByEntitatOrderByNomAsc(entitat);
		return conversioTipusHelper.convertirList(metaExpedients, MetaExpedientDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public List<MetaExpedientDto> findActiusAmbEntitatPerCreacio(Long entitatId, String rolActual) {
		logger.debug("Consulta de meta-expedients actius de l'entitat amb el permis CREATE (" + "entitatId=" + entitatId + ")");

		return conversioTipusHelper.convertirList(
				metaExpedientHelper.findAmbPermis(
						entitatId,
						ExtendedPermission.CREATE,
						true,
						null, 
						rolActual != null && rolActual.equals("IPA_ADMIN"),
						rolActual != null && rolActual.equals("IPA_ORGAN_ADMIN"),
						null, 
						false),
				MetaExpedientDto.class);

	}

	@Transactional(readOnly = true)
	@Override
	public List<MetaExpedientDto> findActiusAmbEntitatPerModificacio(Long entitatId, String rolActual) {
		logger.debug("Consulta de meta-expedients actius de l'entitat amb el permis WRITE (" + "entitatId=" + entitatId + ")");
		return conversioTipusHelper.convertirList(
				metaExpedientHelper.findAmbPermis(
						entitatId,
						ExtendedPermission.WRITE,
						true,
						null, 
						"IPA_ADMIN".equals(rolActual),
						"IPA_ORGAN_ADMIN".equals(rolActual),
						null, 
						false),
				MetaExpedientDto.class);

	}

	@Transactional(readOnly = true)
	@Override
	public List<MetaExpedientDto> findActius(
			Long entitatId,
			String filtreNomOrCodiSia, 
			String rolActual, 
			boolean comu, 
			Long organId) {
		
		List<MetaExpedientEntity> metaExpedientsEnt = metaExpedientHelper.findAmbPermis(
				entitatId,
				ExtendedPermission.READ,
				true,
				filtreNomOrCodiSia, 
				"IPA_ADMIN".equals(rolActual),
				"IPA_ORGAN_ADMIN".equals(rolActual),
				organId, 
				comu); 

		long t0 = System.currentTimeMillis();
		if (cacheHelper.mostrarLogsRendiment())
			logger.info("MetaExpedientServiceImpl.findActius convertirList start ( entitatId=" + entitatId + ", filtreNomOrCodiSia=" + 
					filtreNomOrCodiSia + ", rolActual=" + rolActual + ", organId=" + organId + ", comu=" + comu + ", metaExpedientsEnt=" + (Utils.isNotEmpty(metaExpedientsEnt) ? metaExpedientsEnt.size() : 0) +") ");
		
		List<MetaExpedientDto> metaExpedientsDto = new ArrayList<>();
		
		if (Utils.isNotEmpty(metaExpedientsEnt)) {
			for (MetaExpedientEntity metaExpedientEntity : metaExpedientsEnt) {
				MetaExpedientDto metaExpedientDto = new MetaExpedientDto();
				metaExpedientDto.setId(metaExpedientEntity.getId());
				metaExpedientDto.setNom(metaExpedientEntity.getNom());
				metaExpedientDto.setClassificacio(metaExpedientEntity.getClassificacio());
				metaExpedientDto.setProcedimentComu(metaExpedientEntity.getOrganGestor() == null);
				metaExpedientsDto.add(metaExpedientDto);
				
			}
		}
		
		if (cacheHelper.mostrarLogsRendiment())
			logger.info("MetaExpedientServiceImpl.findActius convertirList end:  " + (System.currentTimeMillis() - t0) + " ms");
		 
		 return metaExpedientsDto;

	}
	

	@Transactional(readOnly = true)
	@Override
	public boolean hasPermissionForAnyProcediment(
			Long entitatId,
			String rolActual, 
			PermissionEnumDto permisDto) {
		
		Permission permis = null;
		
		if (permisDto != null) {
			if (permisDto == PermissionEnumDto.READ) {
				permis = ExtendedPermission.READ;
			} else if (permisDto == PermissionEnumDto.WRITE) {
				permis = ExtendedPermission.WRITE;
			} else if (permisDto == PermissionEnumDto.CREATE) {
				permis = ExtendedPermission.CREATE;
			} else if (permisDto == PermissionEnumDto.DELETE) {
				permis = ExtendedPermission.DELETE;
			}
		}

		List<MetaExpedientEntity> metaExpedientsEnt = metaExpedientHelper.findAmbPermis(
				entitatId,
				permis,
				true,
				null, 
				"IPA_ADMIN".equals(rolActual),
				"IPA_ORGAN_ADMIN".equals(rolActual),
				null, 
				false);

		 return Utils.isNotEmpty(metaExpedientsEnt);

	}
	
	
	@Transactional(readOnly = true)
	@Override
	public List<MetaExpedientDto> findCreateWritePerm(
			Long entitatId,
			String rolActual) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false, 
				false, 
				false);
		List<MetaExpedientEntity> metaExpedients;

		
		List<Long> createWritePermIds = metaExpedientHelper.getIdsCreateWritePermesos(entitatId); 
		
		metaExpedients = metaExpedientRepository.findMetaExpedientsByIds(
				entitat,
				createWritePermIds,
				rolActual.equals("IPA_ADMIN") || rolActual.equals("IPA_ORGAN_ADMIN"));

		return conversioTipusHelper.convertirList(metaExpedients, MetaExpedientDto.class);

	}
	
	

	@Transactional(readOnly = true)
	@Override
	public PaginaDto<MetaExpedientDto> findByEntitatOrOrganGestor(
			Long entitatId,
			Long organGestorId,
			MetaExpedientFiltreDto filtre,
			boolean isRolActualAdministradorOrgan,
			PaginacioParamsDto paginacioParams,
			String rolActual,
			boolean hasPermisAdmComu) {
		PaginaDto<MetaExpedientDto> resposta = null;
		if (isRolActualAdministradorOrgan) {
			resposta = findByOrganGestor(entitatId, organGestorId, hasPermisAdmComu, filtre, paginacioParams);
		} else {
			resposta = findByEntitat(entitatId, filtre, paginacioParams, rolActual);
		}

		return resposta;
	}
	
	@Transactional(readOnly = true)
	@Override
	public int countMetaExpedientsPendentRevisar(
			Long entitatId) {
		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, true, false, false, false);
		return metaExpedientRepository.findByRevisioEstat(entitat, MetaExpedientRevisioEstatEnumDto.PENDENT).size();
	}

	private PaginaDto<MetaExpedientDto> findByEntitat(
			Long entitatId,
			MetaExpedientFiltreDto filtre,
			PaginacioParamsDto paginacioParams, 
			String rolActual) {
		// check permis administracio d'entitat
		EntitatEntity entitat = null;
		if (rolActual.equals("IPA_REVISIO") || rolActual.equals("IPA_DISSENY")) {
			entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false, false, false);
		} else {
			entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, true, false, false, false);
		}
		
		Map<String, String[]> ordenacioMap = new HashMap<String, String[]>();
		ordenacioMap.put("organGestor.codiINom", new String[] {"org.codi"});
		ordenacioMap.put("lastModifiedBy.codiAndNom", new String[] {"lastModifiedBy.nom"});
		
		// Sempre afegirem el nom com a subordre
		addNomSort(paginacioParams);
		
		return paginacioHelper.toPaginaDto(
				metaExpedientRepository.findByEntitat(
						entitat,
						filtre.getCodi() == null || filtre.getCodi().isEmpty(),
						filtre.getCodi() != null ? filtre.getCodi().trim() : "",
						filtre.getNom() == null || filtre.getNom().isEmpty(),
						filtre.getNom() != null ? filtre.getNom().trim() : "",
						filtre.getClassificacio() == null || filtre.getClassificacio().isEmpty(),
						filtre.getClassificacio() != null ? filtre.getClassificacio().trim() : "",
						filtre.getActiu() == null,
						filtre.getActiu() != null ? filtre.getActiu().getValue() : null,
						filtre.getOrganGestorId() == null,
						filtre.getOrganGestorId() != null ? organGestorRepository.findOne(
						filtre.getOrganGestorId()) : null,
						filtre.getAmbit() == null ,
						filtre.getAmbit() == MetaExpedientAmbitEnumDto.COMUNS ? true : false,
						filtre.getRevisioEstats()[0] == null,
						filtre.getRevisioEstats()[0] == null ? null : filtre.getRevisioEstats(),
						paginacioHelper.toSpringDataPageable(paginacioParams, ordenacioMap)),
				MetaExpedientDto.class,
				new Converter<MetaExpedientEntity, MetaExpedientDto>() {
					@Override
					public MetaExpedientDto convert(MetaExpedientEntity source) {
						return metaExpedientHelper.toMetaExpedientDto(source, null);
					}
				});

	}

	private void addNomSort(PaginacioParamsDto paginacioParams) {
		boolean isOrderedByNom = false;
		if (paginacioParams.getOrdres() != null && !paginacioParams.getOrdres().isEmpty()) {
			for(PaginacioParamsDto.OrdreDto ordre : paginacioParams.getOrdres()) {
				if ("nom".equals(ordre.getCamp())) {
					isOrderedByNom = true;
					break;
				}
			}
		}
		if (!isOrderedByNom) {
			paginacioParams.getOrdres().add(new PaginacioParamsDto.OrdreDto("nom", PaginacioParamsDto.OrdreDireccioDto.ASCENDENT));
		}
	}

	private PaginaDto<MetaExpedientDto> findByOrganGestor(
			Long entitatId,
			Long organGestorId,
			boolean hasPermisAdmComu,
			MetaExpedientFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		List<Long> candidateMetaExpIds = metaExpedientHelper.findMetaExpedientIdsFiltratsAmbPermisosOrganGestor(
				entitatId,
				organGestorId,
				hasPermisAdmComu);
		Map<String, String[]> ordenacioMap = new HashMap<String, String[]>();
		ordenacioMap.put("organGestor.codiINom", new String[] {"organGestor.codi", "organGestor.nom"});
		return paginacioHelper.toPaginaDto(
				metaExpedientRepository.findByOrganGestor(
						entitat,
						filtre.getCodi() == null || filtre.getCodi().isEmpty(),
						filtre.getCodi() != null ? filtre.getCodi().trim() : "",
						filtre.getNom() == null || filtre.getNom().isEmpty(),
						filtre.getNom() != null ? filtre.getNom().trim() : "",
						filtre.getClassificacio() == null || filtre.getClassificacio().isEmpty(),
						filtre.getClassificacio() != null ? filtre.getClassificacio().trim() : "",
						filtre.getActiu() == null,
						filtre.getActiu() != null ? filtre.getActiu().getValue() : null,
						filtre.getOrganGestorId() == null,
						filtre.getOrganGestorId() != null ? organGestorRepository.findOne(
								filtre.getOrganGestorId()) : null,
						Utils.getNullIfEmpty(candidateMetaExpIds),
						filtre.getRevisioEstat() == null,
						filtre.getRevisioEstat(),
						paginacioHelper.toSpringDataPageable(paginacioParams, ordenacioMap)),
				MetaExpedientDto.class,
				organGestorId,
				new ConverterParam<MetaExpedientEntity, MetaExpedientDto, Long>() {
					@Override
					public MetaExpedientDto convert(MetaExpedientEntity source, Long param) {
						return metaExpedientHelper.toMetaExpedientDto(source, param);
					}
				});
		
	}

	@Transactional(readOnly = true)
	@Override
	public List<MetaExpedientDto> findActiusAmbOrganGestorPermisLectura(
			Long entitatId,
			Long organGestorId, 
			String filtre) {
		return conversioTipusHelper.convertirList(
				metaExpedientHelper.findActiusAmbOrganGestorPermisLectura(
						entitatId,
						organGestorId,
						filtre),
				MetaExpedientDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public long getProximNumeroSequencia(Long entitatId, Long id, int any) {
		logger.debug(
				"Consulta el pròxim número de seqüència (" + "entitatId=" + entitatId + ", " + "id=" + id + ", " +
						"any=" + any + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false, true, false);

		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(entitat, id);

		return metaExpedientHelper.obtenirProximaSequenciaExpedient(metaExpedient, any, false);
	}

	@Transactional(readOnly = true)
	@Override
	public List<GrupDto> findGrupsAmbMetaExpedient(Long entitatId, Long metaExpedientId, String rolActual) {
		logger.debug("Consulta de grups per metaexpedient (" + "metaExpedientId=" + metaExpedientId + ")");
		entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		List<GrupEntity> grups = metaExpedientRepository.findOne(metaExpedientId).getGrups();
		if (!rolActual.equals("IPA_ADMIN") && !rolActual.equals("IPA_ORGAN_ADMIN")) {
			permisosHelper.filterGrantedAny(grups, GrupEntity.class, new Permission[] {ExtendedPermission.READ});
		}
		return conversioTipusHelper.convertirList(grups, GrupDto.class);
	}

	
	@Transactional
	@Override
	public boolean comprovarPermisosMetaExpedient(
			Long entitatId, 
			Long metaExpedientId,
			PermissionEnumDto permission) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId, 
				false, 
				false, 
				false, 
				false, 
				false);
		
		boolean permitted = true;
		try {
			entityComprovarHelper.comprovarMetaExpedient(
					entitat,
					metaExpedientId,
					permission == PermissionEnumDto.READ,
					permission == PermissionEnumDto.WRITE,
					permission == PermissionEnumDto.CREATE,
					permission == PermissionEnumDto.DELETE,
					false, null, null);
		} catch (PermissionDeniedException ex) {
			permitted = false;
		}
		return permitted;
	}
	
	
	@Transactional
	@Override
	public List<ArbreDto<MetaExpedientCarpetaDto>> findArbreCarpetesMetaExpedient(
			Long entitatId, 
			Long metaExpedientId) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId, 
				false, 
				false, 
				false, 
				true, false);

		if (!isCarpetesDefectaActiva()) {
			throw new RuntimeException("La creació de carpetes per defecte no està activa");
		}
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(entitat, metaExpedientId);

		List<ArbreDto<MetaExpedientCarpetaDto>> carpetes = new ArrayList<ArbreDto<MetaExpedientCarpetaDto>>();
		return metaExpedientHelper.obtenirPareArbreCarpetesPerMetaExpedient(metaExpedient,carpetes);
	}
	
	@Transactional
	@Override
	public MetaExpedientCarpetaDto deleteCarpetaMetaExpedient(
			Long entitatId, 
			Long metaExpedientCarpetaId) {
		entityComprovarHelper.comprovarEntitat(
				entitatId, 
				false, 
				false, 
				false, 
				true, false);
		if (!isCarpetesDefectaActiva()) {
			throw new RuntimeException("La creació de carpetes per defecte no està activa");
		}
		MetaExpedientCarpetaDto carpeteDeleted = metaExpedientHelper.deleteCarpetaMetaExpedient(metaExpedientCarpetaId);
		return carpeteDeleted;
	}
	
	@Transactional
	@Override
	public MetaExpedientTascaDto tascaCreate(
			Long entitatId,
			Long metaExpedientId,
			MetaExpedientTascaDto metaExpedientTasca, String rolActual, Long organId) throws NotFoundException {

		return metaExpedientHelper.tascaCreate(
				entitatId,
				metaExpedientId,
				metaExpedientTasca,
				rolActual,
				organId);
	}

	@Transactional
	@Override
	public MetaExpedientTascaDto tascaUpdate(
			Long entitatId,
			Long metaExpedientId,
			MetaExpedientTascaDto metaExpedientTasca, String rolActual, Long organId) throws NotFoundException {
		logger.debug(
				"Actualitzant la tasca del meta-expedient (" + "entitatId=" + entitatId + ", " + "metaExpedientId=" +
						metaExpedientId + ", " + "metaExpedientTasca=" + metaExpedientTasca + ")");

		ExpedientEstatEntity estatCrearTasca = null;
		if (metaExpedientTasca.getEstatIdCrearTasca() != null) {
			estatCrearTasca = expedientEstatRepository.findOne(metaExpedientTasca.getEstatIdCrearTasca());
		}
		ExpedientEstatEntity estatFinalitzarTasca = null;
		if (metaExpedientTasca.getEstatIdFinalitzarTasca() != null) {
			estatFinalitzarTasca = expedientEstatRepository.findOne(metaExpedientTasca.getEstatIdFinalitzarTasca());
		}

		MetaExpedientTascaEntity entity = getMetaExpedientTasca(entitatId, metaExpedientId, metaExpedientTasca.getId());
		entity.update(
				metaExpedientTasca.getCodi(),
				metaExpedientTasca.getNom(),
				metaExpedientTasca.getDescripcio(),
				metaExpedientTasca.getResponsable(),
				metaExpedientTasca.getDataLimit(),
				metaExpedientTasca.getDuracio(),
				metaExpedientTasca.getPrioritat(),
				estatCrearTasca,
				estatFinalitzarTasca);
		if (rolActual.equals("IPA_ORGAN_ADMIN")) {
			metaExpedientHelper.canviarRevisioADisseny(entitatId, metaExpedientId, organId);
		}
		return conversioTipusHelper.convertir(entity, MetaExpedientTascaDto.class);
	}

	@Transactional
	@Override
	public MetaExpedientTascaDto tascaUpdateActiu(
			Long entitatId,
			Long metaExpedientId,
			Long id,
			boolean activa, String rolActual, Long organId) throws NotFoundException {
		logger.debug(
				"Actualitzant l'atribut activa de la tasca del meta-expedient (" + "entitatId=" + entitatId + ", " +
						"metaExpedientId=" + metaExpedientId + ", " + "id=" + id + ")");
		MetaExpedientTascaEntity entity = getMetaExpedientTasca(entitatId, metaExpedientId, id);
		entity.updateActiva(activa);
		
		if (rolActual.equals("IPA_ORGAN_ADMIN")) {
			metaExpedientHelper.canviarRevisioADisseny(entitatId, metaExpedientId, organId);
		}
		return conversioTipusHelper.convertir(entity, MetaExpedientTascaDto.class);
	}

	@Transactional
	@Override
	public MetaExpedientTascaDto tascaDelete(Long entitatId, Long metaExpedientId, Long id, String rolActual, Long organId) throws NotFoundException {
		logger.debug(
				"Esborrant la tasca del meta-expedient (" + "entitatId=" + entitatId + ", " + "metaExpedientId=" +
						metaExpedientId + ", " + "id=" + id + ")");
		MetaExpedientTascaEntity entity = getMetaExpedientTasca(entitatId, metaExpedientId, id);
		metaExpedientTascaRepository.delete(entity);
		
		if (rolActual.equals("IPA_ORGAN_ADMIN")) {
			metaExpedientHelper.canviarRevisioADisseny(entitatId, metaExpedientId, organId);
		}
		return conversioTipusHelper.convertir(entity, MetaExpedientTascaDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public MetaExpedientTascaDto tascaFindById(Long entitatId, Long metaExpedientId, Long id) throws NotFoundException {
		logger.debug(
				"Consultant la tasca del meta-expedient (" + "entitatId=" + entitatId + ", " + "metaExpedientId=" +
						metaExpedientId + ", " + "id=" + id + ")");
		MetaExpedientTascaEntity entity = getMetaExpedientTasca(entitatId, metaExpedientId, id);
		return conversioTipusHelper.convertir(entity, MetaExpedientTascaDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public PaginaDto<MetaExpedientTascaDto> tascaFindPaginatByMetaExpedient(
			Long entitatId,
			Long metaExpedientId,
			PaginacioParamsDto paginacioParams) throws NotFoundException {
		logger.debug(
				"Consulta paginada de les tasques del meta-expedient(" + "entitatId=" + entitatId + ", " +
						"metaExpedientId=" + metaExpedientId + ", " + "paginacioParams=" + paginacioParams + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);

		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(entitat, metaExpedientId);

		paginacioParams.canviaCampOrdenacio("duracioFormat", "duracio");
		
		return paginacioHelper.toPaginaDto(
				metaExpedientTascaRepository.findByEntitatAndMetaExpedientAndFiltre(
						entitat,
						metaExpedient,
						paginacioParams.getFiltre() == null,
						paginacioParams.getFiltre() != null ? paginacioParams.getFiltre() : "",
						paginacioHelper.toSpringDataPageable(paginacioParams)),
				MetaExpedientTascaDto.class);
	}
	
	
	
	
	@Transactional
	@Override
	public boolean publicarComentariPerMetaExpedient(
			Long entitatId,
			Long metaExpedientId,
			String text, 
			String rolActual) {
		logger.debug("Publicar comentari per metaexpedient ("
				+ "entitatId=" + entitatId + ", "
				+ "text=" + text + ", "
				+ "metaExpedientId=" + metaExpedientId + ")");


		return metaExpedientHelper.publicarComentariPerMetaExpedient(
				entitatId,
				metaExpedientId,
				text,
				rolActual);

	}
	
	
	@Transactional(readOnly = true)
	@Override
	public List<MetaExpedientComentariDto> findComentarisPerMetaExpedient(
			Long entitatId,
			Long metaExpedientId, 
			String rolActual) {
		logger.debug("Obtenint els comentaris pel metaExpedient ("
				+ "entitatId=" + entitatId + ", "
				+ "nodeId=" + metaExpedientId + ")");
		EntitatEntity entitat = null;
		
		if (rolActual.equals("IPA_REVISIO") || rolActual.equals("IPA_DISSENY")) {
			entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false, false, false);
		} else {
			entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false, false, true);
		}
		
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(entitat, metaExpedientId);
		
		List<MetaExpedientComentariEntity> expcoms =
				metaExpedientComentariRepository.findByMetaExpedientOrderByCreatedDateAsc(metaExpedient);

		return conversioTipusHelper.convertirList(
				expcoms, 
				MetaExpedientComentariDto.class);
	}	
	
	
	
	
	
	@Override
	@Transactional(readOnly = true)
	public ProcedimentDto findProcedimentByCodiSia(
			Long entitatId,
			String codiDir3, 
			String codiSia) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false, 
				false, false);
		ProcedimentDto procedimentDto = pluginHelper.procedimentFindByCodiSia(codiDir3, codiSia);
		if (procedimentDto != null && procedimentDto.getUnitatOrganitzativaCodi() != null && !procedimentDto.getUnitatOrganitzativaCodi().isEmpty()) {
			OrganGestorEntity organEntity = organGestorRepository.findByEntitatAndCodi(entitat, procedimentDto.getUnitatOrganitzativaCodi());
			if (organEntity != null) {
				procedimentDto.setOrganId(organEntity.getId());
			}
		}
		return procedimentDto;

	}

	@Transactional
	@Override
	public List<PermisDto> permisFind(Long entitatId, Long id) {
		logger.debug(
				"Consulta dels permisos del meta-expedient (" +
				"entitatId=" + entitatId + ", " +
				"id=" + id + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		entityComprovarHelper.comprovarMetaExpedient(entitat, id);
		List<PermisDto> permisLlistAmbNom = metaExpedientHelper.permisFind(id);
		for (PermisDto permis: permisLlistAmbNom) {
			if (permis.getPrincipalTipus() == PrincipalTipusEnumDto.USUARI) {
				try {
					permis.setPrincipalCodiNom(usuariHelper.getUsuariByCodi(permis.getPrincipalNom()).getNom() + " (" + permis.getPrincipalNom() + ")");
				} catch (NotFoundException ex) {
					logger.debug("No s'ha trobat cap usuari amb el codi " + permis.getPrincipalNom());
					permis.setPrincipalCodiNom(permis.getPrincipalNom());
				}
			} else {
				permis.setPrincipalCodiNom(permis.getPrincipalNom());
			}
		}
		return permisLlistAmbNom;
	}

	@Transactional
	@Override
	public void permisUpdate(Long entitatId, Long id, PermisDto permis, String rolActual, Long organId) {
		logger.debug(
				"Modificació del permis del meta-expedient (" +
				"entitatId=" + entitatId + ", " +
				"id=" + id + ", " +
				"permis=" + permis + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);

		entityComprovarHelper.comprovarMetaExpedient(entitat, id);
		MetaExpedientEntity metaExpedient = metaExpedientRepository.getOne(id);
		if (permis.getOrganGestorId() == null) {
			permisosHelper.updatePermis(id, MetaNodeEntity.class, permis);
		} else {
			OrganGestorEntity organGestor = organGestorRepository.getOne(permis.getOrganGestorId());
			MetaExpedientOrganGestorEntity trobat = metaExpedientOrganGestorRepository.findByMetaExpedientAndOrganGestor(
					metaExpedient,
					organGestor);
			Long metaExpedientOrganGestorId;
			if (trobat == null) {
				metaExpedientOrganGestorId = metaExpedientOrganGestorRepository.save(
						MetaExpedientOrganGestorEntity.getBuilder(metaExpedient, organGestor).build()).getId();
			} else {
				metaExpedientOrganGestorId = trobat.getId();
			}
			permisosHelper.updatePermis(metaExpedientOrganGestorId, MetaExpedientOrganGestorEntity.class, permis);
		}
		
		if (rolActual.equals("IPA_ORGAN_ADMIN")) {
			metaExpedientHelper.canviarRevisioADisseny(entitatId, metaExpedient.getId(), organId);
		}
	}

	@Transactional
	@Override
	public void permisDelete(Long entitatId, Long id, Long permisId, Long organGestorId, String rolActual, Long organId) {
		logger.debug(
				"Eliminació del permis del meta-expedient (" +
				"entitatId=" + entitatId + ", " +
				"id=" + id + ", " +
				"permisId=" + permisId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);

		entityComprovarHelper.comprovarMetaExpedient(entitat, id);
		MetaExpedientEntity metaExpedient = metaExpedientRepository.getOne(id);
		if (organGestorId == null) {
			permisosHelper.deletePermis(id, MetaNodeEntity.class, permisId);
		} else {
			OrganGestorEntity organGestor = organGestorRepository.getOne(organGestorId);
			MetaExpedientOrganGestorEntity trobat = metaExpedientOrganGestorRepository.findByMetaExpedientAndOrganGestor(
					metaExpedient,
					organGestor);
			permisosHelper.deletePermis(trobat.getId(), MetaExpedientOrganGestorEntity.class, permisId);
			//metaExpedientOrganGestorRepository.delete(trobat);
		}
		
		if (rolActual.equals("IPA_ORGAN_ADMIN")) {
			metaExpedientHelper.canviarRevisioADisseny(entitatId, metaExpedient.getId(), organId);
		}
	}

	@Override
	public boolean isRevisioActiva() {
		return metaExpedientHelper.isRevisioActiva();
	}
	
	@Transactional(readOnly = true)
	@Override
	public List<MetaExpedientDto> findActiusAmbEntitatPerConsultaEstadistiques(
			Long entitatId,
			String filtreNomOrCodiSia, 
			String rolActual) {
		logger.debug("Consulta de meta-expedients de l'entitat amb el permis STATISTICS (" + "entitatId=" + entitatId + ")");

		return conversioTipusHelper.convertirList(
				metaExpedientHelper.findAmbPermis(
						entitatId,
						ExtendedPermission.STATISTICS,
						true,
						filtreNomOrCodiSia, 
						"IPA_ADMIN".equals(rolActual),
						"IPA_ORGAN_ADMIN".equals(rolActual),
						null, 
						false), // TODO especificar organId quan és admin organ
				MetaExpedientDto.class);

	}
	
	@Transactional
	@Override
	public CrearReglaResponseDto reintentarCreacioReglaDistribucio(Long entitatId, Long metaExpedientId) {
		entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		return metaExpedientHelper.crearReglaDistribucio(metaExpedientId);
	}
	
	
	@Transactional
	@Override
	public CrearReglaResponseDto canviarEstatReglaDistribucio(
			Long metaExpedientId, 
			boolean activa) {
		MetaExpedientEntity metaExpedient = metaExpedientRepository.findOne(metaExpedientId);
		metaExpedient.updateCrearReglaDistribucio(CrearReglaDistribucioEstatEnumDto.PENDENT);

		try {

			CrearReglaResponseDto rearReglaResponseDto = distribucioReglaHelper.canviEstat(
					metaExpedient.getClassificacio(), 
					activa);

			if (rearReglaResponseDto.getStatus() == StatusEnumDto.OK) {
				metaExpedient.updateCrearReglaDistribucio(CrearReglaDistribucioEstatEnumDto.PROCESSAT);
			} else {
				metaExpedient.updateCrearReglaDistribucioError(StringUtils.abbreviate(rearReglaResponseDto.getMsg(), 1024));
			}

			return rearReglaResponseDto;

		} catch (Exception e) {
			logger.error("Error al crear regla en distribucio ", e);
			metaExpedient.updateCrearReglaDistribucioError(StringUtils.abbreviate(e.getMessage() + ": " + ExceptionUtils.getStackTrace(e), 1024));

			return new CrearReglaResponseDto(StatusEnumDto.ERROR,
					ExceptionHelper.getRootCauseOrItself(e).getMessage());
		}
	}
	
	

    @Override
    public boolean isUpdatingProcediments(EntitatDto entitatDto) {
		ProgresActualitzacioDto progres = progresActualitzacio.get(entitatDto.getCodi());
		return progres != null && (progres.getProgres() > 0 && progres.getProgres() < 100) && !progres.isError();
    }

	@Override
	public ProgresActualitzacioDto getProgresActualitzacio(String codi) {
		ProgresActualitzacioDto progres = progresActualitzacio.get(codi);
		if (progres != null && progres.isFinished()) {
			progresActualitzacio.remove(codi);
		}
		return progres;
	}

	@Override
	@Transactional
	public void actualitzaProcediments(EntitatDto entitatDto, Locale locale) {
		logger.debug("[PROCEDIMENTS] Inici actualitzar procediments");
		MessageHelper.setCurrentLocale(locale);
		metaExpedientHelper.actualitzarProcediments(entitatDto, locale, null);
	}

	private MetaExpedientTascaEntity getMetaExpedientTasca(Long entitatId, Long metaExpedientId, Long id) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);

		entityComprovarHelper.comprovarMetaExpedient(entitat, metaExpedientId);

		MetaExpedientTascaEntity entity = metaExpedientTascaRepository.findOne(id);
		if (entity == null || !entity.getMetaExpedient().getId().equals(metaExpedientId)) {
			throw new NotFoundException(id, MetaExpedientTascaEntity.class);
		}
		return entity;
	}

	private boolean isCarpetesDefectaActiva() {
		return configHelper.getAsBoolean("es.caib.ripea.carpetes.defecte");
	}

	@Transactional
	@Override
	public MetaExpedientDto marcarPendentRevisio(Long entitatId, Long id, Long organId) {
		logger.debug(
				"Marcant com a pendent de revisió un meta-expedient existent (" + "entitatId=" + entitatId + ", " +
						"id=" + id + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedientAdmin(entitat, id, organId);
		metaExpedientHelper.canviarRevisioAPendentEnviarEmail(entitatId, metaExpedient.getId(), organId);
		return conversioTipusHelper.convertir(metaExpedient, MetaExpedientDto.class);
	}
	
	@Transactional
	@Override
	public MetaExpedientDto marcarProcesDisseny(Long entitatId, Long id, Long organId) {
		logger.debug(
				"Marcant com en procés de disseny un meta-expedient existent (" + "entitatId=" + entitatId + ", " +
						"id=" + id + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedientAdmin(entitat, id, organId);
		metaExpedientHelper.canviarRevisioADisseny(entitatId, metaExpedient.getId(), organId);
		return conversioTipusHelper.convertir(metaExpedient, MetaExpedientDto.class);
	}
	
	private static final Logger logger = LoggerFactory.getLogger(MetaExpedientServiceImpl.class);

	@Override
	@Transactional(readOnly=true)
	public List<MetaExpedientTascaValidacioDto> findValidacionsTasca(Long metaExpedientTascaId) {
		List<MetaExpedientTascaValidacioEntity> aux = metaExpedientTascaValidacioRepository.findByMetaExpedientTascaId(metaExpedientTascaId);
		return conversioTipusHelper.convertirList(aux, MetaExpedientTascaValidacioDto.class);
	}

	@Override
	@Transactional
	public boolean createValidacioTasca(MetaExpedientTascaValidacioDto metaExpedientTascaValidacioDto) {
		
		//No hi pot haver duplicats per el mateix:
		//-ItemValidacio: Document o Metadada
		//-TipusValidació: Aportat, Firmat, etc...
		//-El mateix element (ItemId), que pot ser un ID de document o de metadada.
		//-La mateixa tasca: MetaExpedientTasca.id
		List<MetaExpedientTascaValidacioEntity> repetits = metaExpedientTascaValidacioRepository.findByItemValidacioAndTipusValidacioAndItemIdAndMetaExpedientTascaId(
				metaExpedientTascaValidacioDto.getItemValidacio(),
				metaExpedientTascaValidacioDto.getTipusValidacio(),
				metaExpedientTascaValidacioDto.getItemId(),
				metaExpedientTascaValidacioDto.getMetaExpedientTasca().getId());
		
		if (repetits==null || repetits.size()==0) {
			MetaExpedientTascaValidacioEntity novaValidacio = MetaExpedientTascaValidacioEntity.getBuilder(
					metaExpedientTascaValidacioDto.getItemValidacio(),
					metaExpedientTascaValidacioDto.getTipusValidacio(),
					metaExpedientTascaValidacioDto.getItemId(),
					metaExpedientTascaValidacioDto.isActiva()).build();
			novaValidacio.setMetaExpedientTasca(metaExpedientTascaRepository.findOne(metaExpedientTascaValidacioDto.getMetaExpedientTasca().getId()));
			metaExpedientTascaValidacioRepository.save(novaValidacio);
			return true;
		} else {
			return false;
		}
	}

	@Override
	@Transactional
	public MetaExpedientTascaValidacioDto updateValidacioTasca(Long metaExpedientTascaValidacioId, String accio) {
		MetaExpedientTascaValidacioEntity aux = metaExpedientTascaValidacioRepository.findOne(metaExpedientTascaValidacioId);
		if("DESACTIVAR".equals(accio)) {
			aux.setActiva(false);
		} else if("ACTIVAR".equals(accio)) {
			aux.setActiva(true);
		} else if("ELIMINAR".equals(accio)) {
			metaExpedientTascaValidacioRepository.delete(aux);
		}
		return conversioTipusHelper.convertir(aux, MetaExpedientTascaValidacioDto.class);
	}

	@Override
	@Transactional
	public int createValidacionsTasca(
			Long entitatId,
			Long tascaID,
			List<MetaExpedientTascaValidacioDto> validacions) {
		
		int resultat = 0;
		
		for (MetaExpedientTascaValidacioDto validacioDto: validacions) {
			if (validacioDto.getMetaExpedientTasca()==null) {
				MetaExpedientTascaDto metaExpedientTasca = new MetaExpedientTascaDto();
				validacioDto.setMetaExpedientTasca(metaExpedientTasca);
			}
			validacioDto.getMetaExpedientTasca().setId(tascaID);
			if (createValidacioTasca(validacioDto)) {
				resultat++;
			}
		}
		
		return resultat;
	}

}