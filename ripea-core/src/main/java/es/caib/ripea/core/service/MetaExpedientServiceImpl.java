/**
 * 
 */
package es.caib.ripea.core.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//import com.codahale.metrics.Timer;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.caib.ripea.core.api.dto.ArbreDto;
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
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.PermisDto;
import es.caib.ripea.core.api.dto.PermissionEnumDto;
import es.caib.ripea.core.api.dto.PrincipalTipusEnumDto;
import es.caib.ripea.core.api.dto.ProcedimentDto;
import es.caib.ripea.core.api.dto.ProgresActualitzacioDto;
import es.caib.ripea.core.api.exception.ExisteixenExpedientsEsborratsException;
import es.caib.ripea.core.api.exception.ExisteixenExpedientsException;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.exception.PermissionDeniedException;
import es.caib.ripea.core.api.service.MetaExpedientService;
import es.caib.ripea.core.entity.DominiEntity;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.ExpedientEstatEntity;
import es.caib.ripea.core.entity.GrupEntity;
import es.caib.ripea.core.entity.HistoricExpedientEntity;
import es.caib.ripea.core.entity.HistoricInteressatEntity;
import es.caib.ripea.core.entity.HistoricUsuariEntity;
import es.caib.ripea.core.entity.MetaDocumentEntity;
import es.caib.ripea.core.entity.MetaExpedientComentariEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.MetaExpedientOrganGestorEntity;
import es.caib.ripea.core.entity.MetaExpedientTascaEntity;
import es.caib.ripea.core.entity.MetaNodeEntity;
import es.caib.ripea.core.entity.OrganGestorEntity;
import es.caib.ripea.core.helper.ConfigHelper;
import es.caib.ripea.core.helper.ConversioTipusHelper;
import es.caib.ripea.core.helper.DominiHelper;
import es.caib.ripea.core.helper.EmailHelper;
import es.caib.ripea.core.helper.EntityComprovarHelper;
import es.caib.ripea.core.helper.ExpedientEstatHelper;
import es.caib.ripea.core.helper.GrupHelper;
import es.caib.ripea.core.helper.MessageHelper;
import es.caib.ripea.core.helper.MetaDadaHelper;
import es.caib.ripea.core.helper.MetaDocumentHelper;
import es.caib.ripea.core.helper.MetaExpedientCarpetaHelper;
import es.caib.ripea.core.helper.MetaExpedientHelper;
import es.caib.ripea.core.helper.MetaNodeHelper;
import es.caib.ripea.core.helper.PaginacioHelper;
import es.caib.ripea.core.helper.PermisosHelper;
import es.caib.ripea.core.helper.PluginHelper;
import es.caib.ripea.core.helper.UsuariHelper;
import es.caib.ripea.core.repository.DominiRepository;
import es.caib.ripea.core.repository.ExpedientEstatRepository;
import es.caib.ripea.core.repository.ExpedientRepository;
import es.caib.ripea.core.repository.GrupRepository;
import es.caib.ripea.core.repository.MetaDocumentRepository;
import es.caib.ripea.core.repository.MetaExpedientComentariRepository;
import es.caib.ripea.core.repository.MetaExpedientOrganGestorRepository;
import es.caib.ripea.core.repository.MetaExpedientRepository;
import es.caib.ripea.core.repository.MetaExpedientTascaRepository;
import es.caib.ripea.core.repository.OrganGestorRepository;
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

	@Autowired
	private MetaExpedientRepository metaExpedientRepository;
	@Autowired
	private MetaDocumentRepository metaDocumentRepository;
	@Autowired
	private ExpedientEstatRepository expedientEstatRepository;
	@Autowired
	private MetaExpedientTascaRepository metaExpedientTascaRepository;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private MetaNodeHelper metaNodeHelper;
	@Autowired
	private PaginacioHelper paginacioHelper;
	@Autowired
	private PermisosHelper permisosHelper;
	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private MetaExpedientHelper metaExpedientHelper;
	@Autowired
	private OrganGestorRepository organGestorRepository;
	@Autowired
	private ExpedientRepository expedientRepository;
	@Autowired
	private MetaExpedientCarpetaHelper metaExpedientCarpetaHelper;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private MetaExpedientOrganGestorRepository metaExpedientOrganGestorRepository;
	@Autowired
	private UsuariHelper usuariHelper;
	@Autowired
	private ConfigHelper configHelper;
	@Autowired
	private MetaExpedientComentariRepository metaExpedientComentariRepository;
	@Resource
	private MetaDocumentHelper metaDocumentHelper;
	@Resource
	private MetaDadaHelper metaDadaHelper;
	@Autowired
	private ExpedientEstatHelper expedientEstatHelper;
	@Autowired
	private GrupHelper grupHelper;
	@Autowired
	private GrupRepository grupRepository;
	@Autowired
	private DominiRepository dominiRepository;
	@Autowired
	private DominiHelper dominiHelper;
	@Autowired
	private HistoricExpedientRepository historicExpedientRepository;
	@Autowired
	private HistoricInteressatRepository historicInteressatRepository;
	@Autowired
	private HistoricUsuariRepository historicUsuariRepository;
	@Autowired
	private EmailHelper emailHelper;

	public static Map<String, ProgresActualitzacioDto> progresActualitzacio = new HashMap<>();
	public static Map<Long, Integer> metaExpedientsAmbOrganNoSincronitzat = new HashMap<>();

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
				metaExpedient.getClassificacioSia(),
				metaExpedient.isNotificacioActiva(),
				metaExpedient.isPermetMetadocsGenerals(),
				entitat,
				metaExpedientPare,
				organGestorId == null ? null : organGestorRepository.findOne(organGestorId),
				metaExpedient.isGestioAmbGrupsActiva()).
				expressioNumero(metaExpedient.getExpressioNumero()).build();
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
				metaExpedient.getClassificacioSia(),
				metaExpedient.getSerieDocumental(),
				metaExpedient.getExpressioNumero(),
				metaExpedient.isNotificacioActiva(),
				metaExpedient.isPermetMetadocsGenerals(),
				metaExpedientPare,
				organGestorId == null ? null : organGestorRepository.findOne(organGestorId),
				metaExpedient.isGestioAmbGrupsActiva());
		
		if (metaExpedient.getEstructuraCarpetes() != null) {
			//crear estructura carpetes per defecte
			metaExpedientHelper.crearEstructuraCarpetes(
					metaExpedient.getEstructuraCarpetes(), 
					metaExpedientEntity);
		}
		
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
	public void createFromImport(Long entitatId, MetaExpedientExportDto metaExpedient, String rolActual, Long organId) {
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
				metaExpedient.getClassificacioSia(),
				metaExpedient.isNotificacioActiva(),
				metaExpedient.isPermetMetadocsGenerals(),
				entitat,
				metaExpedientPare,
				organGestorId == null ? null : organGestorRepository.findOne(organGestorId),
				metaExpedient.isGestioAmbGrupsActiva()).
				expressioNumero(metaExpedient.getExpressioNumero()).build();
		MetaExpedientEntity metaExpedientEntity = metaExpedientRepository.save(entity);
		if (metaExpedient.getEstructuraCarpetes() != null) {
			//crear estructura carpetes per defecte
			metaExpedientHelper.crearEstructuraCarpetes(
					metaExpedient.getEstructuraCarpetes(), 
					metaExpedientEntity);
		}
		
		if ("IPA_ORGAN_ADMIN".equals(rolActual)) {
			metaExpedientHelper.canviarRevisioADisseny(entitatId, metaExpedientEntity.getId(), organId);
		} else {
			metaExpedientEntity.updateRevisioEstat(MetaExpedientRevisioEstatEnumDto.REVISAT);
		}

		if (metaExpedient.getMetaDocuments() != null) {
			for (MetaDocumentDto metaDocumentDto : metaExpedient.getMetaDocuments()) {
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
		
		if (metaExpedient.getMetaDades() != null) {
			for (MetaDadaDto metaDadaDto : metaExpedient.getMetaDades()) {
				if (metaDadaDto.getTipus() == MetaDadaTipusEnumDto.DOMINI) {
					List<DominiEntity> dominis = dominiRepository.findByEntitatAndCodi(entitat, metaDadaDto.getCodi());
					if (dominis == null || dominis.isEmpty() && metaDadaDto.getDomini() != null) {
						dominiHelper.create(entitatId, metaDadaDto.getDomini(), false);
					}
				}
				metaDadaHelper.create(entitatId, entity.getId(), metaDadaDto, rolActual, organId);
			}
		}
		
		if (metaExpedient.getEstats() != null) {
			for (ExpedientEstatDto expedientEstatDto : metaExpedient.getEstats()) {
				expedientEstatDto.setMetaExpedientId(entity.getId());
				ExpedientEstatDto expedientEstatCreated = expedientEstatHelper.createExpedientEstat(
						entitatId,
						expedientEstatDto,
						rolActual,
						organId);
				
				if (metaExpedient.getTasques() != null) {
					for (MetaExpedientTascaDto metaExpedientTascaDto : metaExpedient.getTasques()) {
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
		
		if (metaExpedient.getTasques() != null) {
			for (MetaExpedientTascaDto metaExpedientTascaDto : metaExpedient.getTasques()) {
				metaExpedientHelper.tascaCreate(
						entitatId,
						entity.getId(),
						metaExpedientTascaDto,
						rolActual,
						organId);
			}
		}
		
		if (metaExpedient.getGrups() != null) {
			for (GrupDto grupDto : metaExpedient.getGrups()) {
				
				List<GrupEntity> grups = grupRepository.findByRolDescricpio(entitat, grupDto.getRol(), grupDto.getDescripcio());
				
				GrupEntity grup = null;
				if (grups == null || grups.isEmpty()) {
					GrupDto grupCreated = grupHelper.create(
							entitatId,
							grupDto);
					grup = grupRepository.findOne(grupCreated.getId());
				} else {
					grup = grups.get(0);
				}
				entity.addGrup(grup);
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
						DominiEntity domini = dominiRepository.findByEntitatAndCodi(entitat, metaDadaDto.getCodi()).get(0);
						metaDadaDto.setDomini(conversioTipusHelper.convertir(domini, DominiDto.class));
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
			metaNodeHelper.omplirPermisosPerMetaNode(resposta, null, null);
			omplirMetaDocumentsPerMetaExpedient(metaExpedient, resposta);
			resposta.setNumComentaris(metaExpedient.getComentaris().size());
		}
		return resposta;
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
			metaNodeHelper.omplirPermisosPerMetaNode(resposta, null, null);
			omplirMetaDocumentsPerMetaExpedient(metaExpedient, resposta);
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
		if (resposta != null) {
			metaNodeHelper.omplirMetaDadesPerMetaNode(resposta);
			metaNodeHelper.omplirPermisosPerMetaNode(resposta, null, null);
			omplirMetaDocumentsPerMetaExpedient(metaExpedient, resposta);
		}
		return resposta;
	}

	
	@Transactional(readOnly = true)
	@Override
	public List<MetaExpedientDto> findByCodiSia(Long entitatId, String codiSia) {
		logger.debug(
				"Consulta del meta-expedient per entitat i codi SIA (" + "entitatId=" + entitatId + ", " + "codi=" + codiSia +
						")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		List<MetaExpedientEntity> metaExpedients = metaExpedientRepository.findByEntitatAndClassificacioSia(entitat, codiSia);
		
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
		logger.debug(
				"Consulta de meta-expedients actius de l'entitat amb el permis CREATE (" + "entitatId=" + entitatId +
						")");

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
		logger.debug("Consulta de meta-expedients de l'entitat amb el permis READ (" + "entitatId=" + entitatId + ")");

		return conversioTipusHelper.convertirList(
				metaExpedientHelper.findAmbPermis(
						entitatId,
						ExtendedPermission.READ,
						true,
						filtreNomOrCodiSia, 
						"IPA_ADMIN".equals(rolActual),
						"IPA_ORGAN_ADMIN".equals(rolActual),
						organId, 
						comu), // TODO especificar organId quan és admin organ
				MetaExpedientDto.class);

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
		metaNodeHelper.omplirMetaDadesPerMetaNodes(resposta.getContingut());
		omplirMetaDocumentsPerMetaExpedients(resposta.getContingut());
		metaNodeHelper.omplirPermisosPerMetaNodes(resposta.getContingut(), true);
		for (MetaExpedientDto metaExpedient : resposta.getContingut()) {
			MetaExpedientEntity metaExpedientEntity = metaExpedientRepository.findOne(metaExpedient.getId());
			metaExpedient.setExpedientEstatsCount(expedientEstatRepository.countByMetaExpedient(metaExpedientEntity));
			metaExpedient.setExpedientTasquesCount(
					metaExpedientTascaRepository.countByMetaExpedient(metaExpedientEntity));
			metaExpedient.setGrupsCount(metaExpedientEntity.getGrups().size());
			metaExpedient.setNumComentaris(metaExpedientEntity.getComentaris().size());
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
		if (rolActual.equals("IPA_REVISIO")) {
			entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false, false, false);
		} else {
			entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, true, false, false, false);
		}
		if (paginacioHelper.esPaginacioActivada(paginacioParams)) {
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
							filtre.getClassificacioSia() == null || filtre.getClassificacioSia().isEmpty(),
							filtre.getClassificacioSia() != null ? filtre.getClassificacioSia().trim() : "",
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
					MetaExpedientDto.class);
		} else {
			return paginacioHelper.toPaginaDto(
					metaExpedientRepository.findByEntitat(
							entitat,
							filtre.getCodi() == null || filtre.getCodi().isEmpty(),
							filtre.getCodi() != null ? filtre.getCodi().trim() : "",
							filtre.getNom() == null || filtre.getNom().isEmpty(),
							filtre.getNom() != null ? filtre.getNom().trim() : "",
							filtre.getClassificacioSia() == null || filtre.getClassificacioSia().isEmpty(),
							filtre.getClassificacioSia() != null ? filtre.getClassificacioSia().trim() : "",
							filtre.getActiu() == null,
							filtre.getActiu() != null ? filtre.getActiu().getValue() : null,
							filtre.getOrganGestorId() == null,
							filtre.getOrganGestorId() != null ? organGestorRepository.findOne(
							filtre.getOrganGestorId()) : null,
							filtre.getAmbit() == null ,
							filtre.getAmbit() == MetaExpedientAmbitEnumDto.COMUNS ? true : false,
							filtre.getRevisioEstats()[0] == null,
							filtre.getRevisioEstats()[0] == null ? null : filtre.getRevisioEstats(),					
							paginacioHelper.toSpringDataSort(paginacioParams)),
					MetaExpedientDto.class);
		}

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
		if (candidateMetaExpIds.size() == 0) {
			return new PaginaDto<MetaExpedientDto>();
		} else if (paginacioHelper.esPaginacioActivada(paginacioParams)) {
			return paginacioHelper.toPaginaDto(
					metaExpedientRepository.findByOrganGestor(
							entitat,
							filtre.getCodi() == null || filtre.getCodi().isEmpty(),
							filtre.getCodi() != null ? filtre.getCodi().trim() : "",
							filtre.getNom() == null || filtre.getNom().isEmpty(),
							filtre.getNom() != null ? filtre.getNom().trim() : "",
							filtre.getClassificacioSia() == null || filtre.getClassificacioSia().isEmpty(),
							filtre.getClassificacioSia() != null ? filtre.getClassificacioSia().trim() : "",
							filtre.getActiu() == null,
							filtre.getActiu() != null ? filtre.getActiu().getValue() : null,
							filtre.getOrganGestorId() == null,
							filtre.getOrganGestorId() != null ? organGestorRepository.findOne(
									filtre.getOrganGestorId()) : null,
							candidateMetaExpIds,
							filtre.getRevisioEstat() == null,
							filtre.getRevisioEstat(),
							paginacioHelper.toSpringDataPageable(paginacioParams)),
					MetaExpedientDto.class);
		} else {
			return paginacioHelper.toPaginaDto(
					metaExpedientRepository.findByOrganGestor(
							entitat,
							filtre.getCodi() == null || filtre.getCodi().isEmpty(),
							filtre.getCodi() != null ? filtre.getCodi().trim() : "",
							filtre.getNom() == null || filtre.getNom().isEmpty(),
							filtre.getNom() != null ? filtre.getNom().trim() : "",
							filtre.getClassificacioSia() == null || filtre.getClassificacioSia().isEmpty(),
							filtre.getClassificacioSia() != null ? filtre.getClassificacioSia().trim() : "",
							filtre.getActiu() == null,
							filtre.getActiu() != null ? filtre.getActiu().getValue() : null,
							filtre.getOrganGestorId() == null,
							filtre.getOrganGestorId() != null ? organGestorRepository.findOne(
									filtre.getOrganGestorId()) : null,
							candidateMetaExpIds,
							filtre.getRevisioEstat() == null,
							filtre.getRevisioEstat(),
							paginacioHelper.toSpringDataSort(paginacioParams)),
					MetaExpedientDto.class);
		}
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
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			List<Sid> sids = new ArrayList<Sid>();
			sids.add(new PrincipalSid(auth.getName()));
			for (GrantedAuthority ga : auth.getAuthorities()) {
				sids.add(new GrantedAuthoritySid(ga.getAuthority()));
			}
			Iterator<GrupEntity> it = grups.iterator();
			while (it.hasNext()) {
				GrupEntity grupEntity = it.next();
				boolean isGranted = false;
				for (Sid sid : sids) {
					if (sid.equals(new GrantedAuthoritySid(grupEntity.getRol()))) {
						isGranted = true;
					}
				}
				if (!isGranted) {
					it.remove();
				}
			}
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
			entityComprovarHelper.comprovarMetaExpedientPerExpedient(
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

		return paginacioHelper.toPaginaDto(
				metaExpedientTascaRepository.findByEntitatAndMetaExpedientAndFiltre(
						entitat,
						metaExpedient,
						paginacioParams.getFiltre() == null,
						paginacioParams.getFiltre(),
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
		
		if (rolActual.equals("IPA_REVISIO")) {
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
	public Integer getMetaExpedientsAmbOrganNoSincronitzat(Long entitatId) {
		Integer organsNoSincronitzats = metaExpedientsAmbOrganNoSincronitzat.get(entitatId);
		if (organsNoSincronitzats == null) {
			organsNoSincronitzats = metaExpedientRepository.countByEntitatIdAndOrganNoSincronitzatTrue(entitatId);
			metaExpedientsAmbOrganNoSincronitzat.put(entitatId, organsNoSincronitzats);
		}
		return organsNoSincronitzats;
	}

	@Override
	@Transactional
	public void actualitzaProcediments(EntitatDto entitatDto, Locale locale) {

		logger.debug("[PROCEDIMENTS] Inici actualitzar procediments");
		MessageHelper.setCurrentLocale(locale);
		metaExpedientHelper.actualitzarProcediments(entitatDto, locale);

	}

	private void omplirMetaDocumentsPerMetaExpedients(List<MetaExpedientDto> metaExpedients) {
		List<Long> metaExpedientIds = new ArrayList<Long>();
		for (MetaExpedientDto metaExpedient : metaExpedients) {
			metaExpedientIds.add(metaExpedient.getId());
		}
		List<MetaDocumentEntity> metaDocumentsDelsMetaExpedients = null;
		// Si passam una llista buida dona un error a la consulta.
		if (metaExpedientIds.size() > 0) {
			metaDocumentsDelsMetaExpedients = metaDocumentRepository.findByMetaExpedientIdIn(metaExpedientIds);
		}
		for (MetaExpedientDto metaExpedient : metaExpedients) {
			List<MetaDocumentDto> metaDocuments = new ArrayList<MetaDocumentDto>();
			if (metaDocumentsDelsMetaExpedients != null) {
				for (MetaDocumentEntity metaDocument : metaDocumentsDelsMetaExpedients) {
					if (metaDocument.getMetaExpedient().getId().equals(metaExpedient.getId())) {
						metaDocuments.add(conversioTipusHelper.convertir(metaDocument, MetaDocumentDto.class));
					}
				}
			}
			metaExpedient.setMetaDocuments(metaDocuments);
		}
	}

	private void omplirMetaDocumentsPerMetaExpedient(MetaExpedientEntity metaExpedient, MetaExpedientDto dto) {
		List<MetaDocumentEntity> metaDocumentsDelMetaExpedient = metaDocumentRepository.findByMetaExpedient(
				metaExpedient);
		List<MetaDocumentDto> metaDocuments = new ArrayList<MetaDocumentDto>();
		for (MetaDocumentEntity metaDocument : metaDocumentsDelMetaExpedient) {
			metaDocuments.add(conversioTipusHelper.convertir(metaDocument, MetaDocumentDto.class));
		}
		dto.setMetaDocuments(metaDocuments);
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

}