/**
 * 
 */
package es.caib.ripea.core.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.core.api.dto.ArbreDto;
import es.caib.ripea.core.api.dto.GrupDto;
import es.caib.ripea.core.api.dto.MetaDocumentDto;
import es.caib.ripea.core.api.dto.MetaExpedientAmbitEnumDto;
import es.caib.ripea.core.api.dto.MetaExpedientCarpetaDto;
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.dto.MetaExpedientFiltreDto;
import es.caib.ripea.core.api.dto.MetaExpedientTascaDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.PermisDto;
import es.caib.ripea.core.api.exception.ExisteixenExpedientsEsborratsException;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.service.MetaExpedientService;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.ExpedientEstatEntity;
import es.caib.ripea.core.entity.GrupEntity;
import es.caib.ripea.core.entity.MetaDocumentEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.MetaExpedientTascaEntity;
import es.caib.ripea.core.entity.MetaNodeEntity;
import es.caib.ripea.core.helper.ConversioTipusHelper;
import es.caib.ripea.core.helper.EntityComprovarHelper;
import es.caib.ripea.core.helper.MetaExpedientCarpetaHelper;
import es.caib.ripea.core.helper.MetaExpedientHelper;
import es.caib.ripea.core.helper.MetaNodeHelper;
import es.caib.ripea.core.helper.PaginacioHelper;
import es.caib.ripea.core.helper.PermisosHelper;
import es.caib.ripea.core.repository.ExpedientEstatRepository;
import es.caib.ripea.core.repository.ExpedientRepository;
import es.caib.ripea.core.repository.MetaDocumentRepository;
import es.caib.ripea.core.repository.MetaExpedientRepository;
import es.caib.ripea.core.repository.MetaExpedientTascaRepository;
import es.caib.ripea.core.repository.OrganGestorRepository;
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

	@Transactional
	@Override
	public MetaExpedientDto create(Long entitatId, MetaExpedientDto metaExpedient) {
		logger.debug(
				"Creant un nou meta-expedient (" + "entitatId=" + entitatId + ", " + "metaExpedient=" + metaExpedient +
						")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		if (metaExpedient.getOrganGestor() != null) {
			entityComprovarHelper.comprovarOrganGestor(entitatId, metaExpedient.getOrganGestor().getId());
		}
		MetaExpedientEntity metaExpedientPare = null;
		if (metaExpedient.getPareId() != null) {
			metaExpedientPare = entityComprovarHelper.comprovarMetaExpedient(
					entitat,
					metaExpedient.getPareId(),
					false,
					false,
					false,
					false);
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
				metaExpedient.isGestioAmbGrupsActiva()).build();
		MetaExpedientEntity metaExpedientEntity = metaExpedientRepository.save(entity);
		
		//crear estructura carpetes per defecte
		metaExpedientHelper.crearEstructuraCarpetes(
				metaExpedient.getEstructuraCarpetes(), 
				metaExpedientEntity);
		return conversioTipusHelper.convertir(metaExpedientEntity, MetaExpedientDto.class);
	}

	@Transactional
	@Override
	public MetaExpedientDto update(Long entitatId, MetaExpedientDto metaExpedient) {
		logger.debug(
				"Actualitzant meta-expedient existent (" + "entitatId=" + entitatId + ", " + "metaExpedient=" +
						metaExpedient + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		MetaExpedientEntity metaExpedientEntity;
		MetaExpedientEntity metaExpedientPare = null;
		metaExpedientEntity = entityComprovarHelper.comprovarMetaExpedientAdmin(
				entitat,
				metaExpedient.getId(),
				false,
				false,
				false,
				false);

		if (metaExpedient.getPareId() != null) {
			metaExpedientPare = entityComprovarHelper.comprovarMetaExpedientAdmin(
					entitat,
					metaExpedient.getPareId(),
					false,
					false,
					false,
					false);
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
		
		//crear estructura carpetes per defecte
		metaExpedientHelper.crearEstructuraCarpetes(
				metaExpedient.getEstructuraCarpetes(), 
				metaExpedientEntity);
		return conversioTipusHelper.convertir(metaExpedientEntity, MetaExpedientDto.class);
	}

	@Transactional
	@Override
	public MetaExpedientDto updateActiu(Long entitatId, Long id, boolean actiu) {
		logger.debug(
				"Actualitzant propietat activa d'un meta-expedient existent (" + "entitatId=" + entitatId + ", " +
						"id=" + id + ", " + "actiu=" + actiu + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedientAdmin(
				entitat,
				id,
				false,
				false,
				false,
				false);

		metaExpedient.updateActiu(actiu);
		return conversioTipusHelper.convertir(metaExpedient, MetaExpedientDto.class);
	}

	@Transactional
	@Override
	public MetaExpedientDto delete(Long entitatId, Long id) {
		logger.debug("Esborrant meta-expedient (id=" + id + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		MetaExpedientEntity metaExpedient;
		metaExpedient = entityComprovarHelper.comprovarMetaExpedientAdmin(entitat, id, false, false, false, false);

			
		List<ExpedientEntity> expedients = expedientRepository.findByMetaExpedient(metaExpedient);
		boolean allEsborats = true;
		for (ExpedientEntity expedientEntity : expedients) {
			if (expedientEntity.getEsborrat() == 0) {
				allEsborats = false;
			}
		}
		if (allEsborats == true && expedients.size() > 0)
			throw new ExisteixenExpedientsEsborratsException();

		//esborrar les carpetes per defecte
		metaExpedientCarpetaHelper.removeAllCarpetes(metaExpedient);
		
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
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
				entitat,
				id,
				false,
				false,
				false,
				false);
		MetaExpedientDto resposta = conversioTipusHelper.convertir(metaExpedient, MetaExpedientDto.class);
		if (resposta != null) {
			metaNodeHelper.omplirMetaDadesPerMetaNode(resposta);
			metaNodeHelper.omplirPermisosPerMetaNode(resposta, false);
			omplirMetaDocumentsPerMetaExpedient(metaExpedient, resposta);
		}
		return resposta;
	}
	

	
	
	

	@Transactional(readOnly = true)
	@Override
	public MetaExpedientDto getAndCheckAdminPermission(Long entitatId, Long id) {
		logger.debug("Consulta del meta-expedient (" + "entitatId=" + entitatId + ", " + "id=" + id + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedientAdmin(
				entitat,
				id,
				false,
				false,
				false,
				false);
		MetaExpedientDto resposta = conversioTipusHelper.convertir(metaExpedient, MetaExpedientDto.class);
		if (resposta != null) {
			metaNodeHelper.omplirMetaDadesPerMetaNode(resposta);
			metaNodeHelper.omplirPermisosPerMetaNode(resposta, false);
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
			metaNodeHelper.omplirPermisosPerMetaNode(resposta, false);
			omplirMetaDocumentsPerMetaExpedient(metaExpedient, resposta);
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
	public List<MetaExpedientDto> findActiusAmbEntitatPerAdmin(Long entitatId) {
		logger.debug("Consulta de meta-expedients actius de l'entitat per admins (" + "entitatId=" + entitatId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		return conversioTipusHelper.convertirList(
				metaExpedientRepository.findByEntitatAndActiuTrueOrderByNomAsc(entitat),
				MetaExpedientDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public List<MetaExpedientDto> findActiusAmbEntitatPerCreacio(Long entitatId) {
		logger.debug(
				"Consulta de meta-expedients actius de l'entitat amb el permis CREATE (" + "entitatId=" + entitatId +
						")");
		return findActiusAmbEntitatPermis(entitatId, new Permission[] { ExtendedPermission.CREATE }, null);
	}

	@Transactional(readOnly = true)
	@Override
	public List<MetaExpedientDto> findActiusAmbEntitatPerModificacio(Long entitatId) {
		logger.debug("Consulta de meta-expedients actius de l'entitat amb el permis WRITE (" + "entitatId=" + entitatId + ")");
		return findActiusAmbEntitatPermis(
				entitatId,
				new Permission[] { ExtendedPermission.WRITE },
				null);
	}

	@Transactional(readOnly = true)
	@Override
	public List<MetaExpedientDto> findActiusAmbEntitatPerLectura(
			Long entitatId,
			String filtreNomOrCodiSia) {
		logger.debug("Consulta de meta-expedients de l'entitat amb el permis READ (" + "entitatId=" + entitatId + ")");
		return findActiusAmbEntitatPermis(
				entitatId,
				new Permission[] { ExtendedPermission.READ },
				filtreNomOrCodiSia);
	}

	@Transactional(readOnly = true)
	@Override
	public PaginaDto<MetaExpedientDto> findByEntitatOrOrganGestor(
			Long entitatId,
			Long organGestorId,
			MetaExpedientFiltreDto filtre,
			boolean isRolActualAdministradorOrgan,
			PaginacioParamsDto paginacioParams) {
		PaginaDto<MetaExpedientDto> resposta = null;
		if (isRolActualAdministradorOrgan) {
			resposta = findByOrganGestor(entitatId, organGestorId, filtre, paginacioParams);
		} else {
			resposta = findByEntitat(entitatId, filtre, paginacioParams);
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
		}
		return resposta;
	}

	private PaginaDto<MetaExpedientDto> findByEntitat(
			Long entitatId,
			MetaExpedientFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		// check permis administracio d'entitat
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, true, false, false);
		if (paginacioHelper.esPaginacioActivada(paginacioParams)) {
			return paginacioHelper.toPaginaDto(
					metaExpedientRepository.findByEntitat(
							entitat,
							filtre.getCodi() == null || filtre.getCodi().isEmpty(),
							filtre.getCodi(),
							filtre.getNom() == null || filtre.getNom().isEmpty(),
							filtre.getNom(),
							filtre.getActiu() == null,
							filtre.getActiu() != null ? filtre.getActiu().getValue() : null,
							filtre.getOrganGestorId() == null,
							filtre.getOrganGestorId() != null ? organGestorRepository.findOne(
							filtre.getOrganGestorId()) : null,
							filtre.getAmbit() == null ,
							filtre.getAmbit() == MetaExpedientAmbitEnumDto.COMUNS ? true : false,
							paginacioHelper.toSpringDataPageable(paginacioParams)),
					MetaExpedientDto.class);
		} else {
			return paginacioHelper.toPaginaDto(
					metaExpedientRepository.findByEntitat(
							entitat,
							filtre.getCodi() == null || filtre.getCodi().isEmpty(),
							filtre.getCodi(),
							filtre.getNom() == null || filtre.getNom().isEmpty(),
							filtre.getNom(),
							filtre.getActiu() == null,
							filtre.getActiu() != null ? filtre.getActiu().getValue() : null,
							filtre.getOrganGestorId() == null,
							filtre.getOrganGestorId() != null ? organGestorRepository.findOne(
							filtre.getOrganGestorId()) : null,
							filtre.getAmbit() == null ,
							filtre.getAmbit() == MetaExpedientAmbitEnumDto.COMUNS ? true : false,
							paginacioHelper.toSpringDataSort(paginacioParams)),
					MetaExpedientDto.class);
		}

	}

	private PaginaDto<MetaExpedientDto> findByOrganGestor(
			Long entitatId,
			Long organGestorId,
			MetaExpedientFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		List<Long> candidateMetaExpIds = metaExpedientHelper.findMetaExpedientIdsFiltratsAmbPermisosOrganGestor(
				entitatId, organGestorId);
		if (candidateMetaExpIds.size() == 0) {
			return new PaginaDto<MetaExpedientDto>();

		} else if (paginacioHelper.esPaginacioActivada(paginacioParams)) {
			return paginacioHelper.toPaginaDto(
					metaExpedientRepository.findByEntitat(
							entitat,
							filtre.getCodi() == null || filtre.getCodi().isEmpty(),
							filtre.getCodi(),
							filtre.getNom() == null || filtre.getNom().isEmpty(),
							filtre.getNom(),
							filtre.getActiu() == null,
							filtre.getActiu() != null ? filtre.getActiu().getValue() : null,
							filtre.getOrganGestorId() == null,
							filtre.getOrganGestorId() != null ? organGestorRepository.findOne(
									filtre.getOrganGestorId()) : null,
							candidateMetaExpIds,
							paginacioHelper.toSpringDataPageable(paginacioParams)),
					MetaExpedientDto.class);
		} else {
			return paginacioHelper.toPaginaDto(
					metaExpedientRepository.findByEntitat(
							entitat,
							filtre.getCodi() == null || filtre.getCodi().isEmpty(),
							filtre.getCodi(),
							filtre.getNom() == null || filtre.getNom().isEmpty(),
							filtre.getNom(),
							filtre.getActiu() == null,
							filtre.getActiu() != null ? filtre.getActiu().getValue() : null,
							filtre.getOrganGestorId() == null,
							filtre.getOrganGestorId() != null ? organGestorRepository.findOne(
									filtre.getOrganGestorId()) : null,
							candidateMetaExpIds,
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
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false, true);
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
				entitat,
				id,
				false,
				false,
				false,
				false);
		return metaExpedientHelper.obtenirProximaSequenciaExpedient(metaExpedient, any, false);
	}

	@Transactional(readOnly = true)
	@Override
	public List<GrupDto> findGrupsAmbMetaExpedient(Long entitatId, Long metaExpedientId) {
		logger.debug("Consulta de grups per metaexpedient (" + "metaExpedientId=" + metaExpedientId + ")");
		entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);

		List<GrupEntity> grups = metaExpedientRepository.findOne(metaExpedientId).getGrups();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		List<Sid> sids = new ArrayList<Sid>();
		sids.add(new PrincipalSid(auth.getName()));
		for (GrantedAuthority ga : auth.getAuthorities())
			sids.add(new GrantedAuthoritySid(ga.getAuthority()));

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

		return conversioTipusHelper.convertirList(grups, GrupDto.class);
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
				true);
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
				entitat,
				metaExpedientId,
				false,
				false,
				false,
				false);
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
				true);
		MetaExpedientCarpetaDto carpeteDeleted = metaExpedientHelper.deleteCarpetaMetaExpedient(metaExpedientCarpetaId);
		return carpeteDeleted;
	}
	
	@Transactional
	@Override
	public MetaExpedientTascaDto tascaCreate(
			Long entitatId,
			Long metaExpedientId,
			MetaExpedientTascaDto metaExpedientTasca) throws NotFoundException {
		logger.debug(
				"Creant una nova tasca del meta-expedient (" + "entitatId=" + entitatId + ", " + "metaExpedientId=" +
						metaExpedientId + ", " + "metaExpedientTasca=" + metaExpedientTasca + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
				entitat,
				metaExpedientId,
				false,
				false,
				false,
				false);

		Long idEstatCrear = metaExpedientTasca.getEstatIdCrearTasca();
		ExpedientEstatEntity estatCrearTasca = idEstatCrear != null ? expedientEstatRepository.findOne(
				idEstatCrear) : null;

		Long idEstatFinalitzar = metaExpedientTasca.getEstatIdFinalitzarTasca();
		ExpedientEstatEntity estatFinalitzarTasca = idEstatFinalitzar != null ? expedientEstatRepository.findOne(
				idEstatFinalitzar) : null;

		MetaExpedientTascaEntity entity = MetaExpedientTascaEntity.getBuilder(
				metaExpedientTasca.getCodi(),
				metaExpedientTasca.getNom(),
				metaExpedientTasca.getDescripcio(),
				metaExpedientTasca.getResponsable(),
				metaExpedient,
				metaExpedientTasca.getDataLimit(),
				estatCrearTasca,
				estatFinalitzarTasca).build();
		return conversioTipusHelper.convertir(metaExpedientTascaRepository.save(entity), MetaExpedientTascaDto.class);
	}

	@Transactional
	@Override
	public MetaExpedientTascaDto tascaUpdate(
			Long entitatId,
			Long metaExpedientId,
			MetaExpedientTascaDto metaExpedientTasca) throws NotFoundException {
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
		return conversioTipusHelper.convertir(entity, MetaExpedientTascaDto.class);
	}

	@Transactional
	@Override
	public MetaExpedientTascaDto tascaUpdateActiu(
			Long entitatId,
			Long metaExpedientId,
			Long id,
			boolean activa) throws NotFoundException {
		logger.debug(
				"Actualitzant l'atribut activa de la tasca del meta-expedient (" + "entitatId=" + entitatId + ", " +
						"metaExpedientId=" + metaExpedientId + ", " + "id=" + id + ")");
		MetaExpedientTascaEntity entity = getMetaExpedientTasca(entitatId, metaExpedientId, id);
		entity.updateActiva(activa);
		return conversioTipusHelper.convertir(entity, MetaExpedientTascaDto.class);
	}

	@Transactional
	@Override
	public MetaExpedientTascaDto tascaDelete(Long entitatId, Long metaExpedientId, Long id) throws NotFoundException {
		logger.debug(
				"Esborrant la tasca del meta-expedient (" + "entitatId=" + entitatId + ", " + "metaExpedientId=" +
						metaExpedientId + ", " + "id=" + id + ")");
		MetaExpedientTascaEntity entity = getMetaExpedientTasca(entitatId, metaExpedientId, id);
		metaExpedientTascaRepository.delete(entity);
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
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
				entitat,
				metaExpedientId,
				false,
				false,
				false,
				false);
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
	public List<PermisDto> permisFind(Long entitatId, Long id) {
		logger.debug(
				"Consulta dels permisos del meta-expedient (" + "entitatId=" + entitatId + ", " + "id=" + id + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		entityComprovarHelper.comprovarMetaExpedient(entitat, id, false, false, false, false);
		return permisosHelper.findPermisos(id, MetaNodeEntity.class);
	}

	@Transactional
	@Override
	public void permisUpdate(Long entitatId, Long id, PermisDto permis) {
		logger.debug(
				"Modificació del permis del meta-expedient (" + "entitatId=" + entitatId + ", " + "id=" + id + ", " +
						"permis=" + permis + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		entityComprovarHelper.comprovarMetaExpedient(entitat, id, false, false, false, false);
		permisosHelper.updatePermis(id, MetaNodeEntity.class, permis);
	}

	@Transactional
	@Override
	public void permisDelete(Long entitatId, Long id, Long permisId) {
		logger.debug(
				"Eliminació del permis del meta-expedient (" + "entitatId=" + entitatId + ", " + "id=" + id + ", " +
						"permisId=" + permisId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		entityComprovarHelper.comprovarMetaExpedient(entitat, id, false, false, false, false);
		permisosHelper.deletePermis(id, MetaNodeEntity.class, permisId);
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

	private List<MetaExpedientDto> findActiusAmbEntitatPermis(
			Long entitatId,
			Permission[] permisos,
			String filtreNomOrCodiSia) {

		return conversioTipusHelper.convertirList(
				metaExpedientHelper.findAmbEntitatOrOrganPermis(
						entitatId,
						permisos,
						true,
						filtreNomOrCodiSia),
				MetaExpedientDto.class);
	}

	private MetaExpedientTascaEntity getMetaExpedientTasca(Long entitatId, Long metaExpedientId, Long id) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		entityComprovarHelper.comprovarMetaExpedient(entitat, metaExpedientId, false, false, false, false);
		MetaExpedientTascaEntity entity = metaExpedientTascaRepository.findOne(id);
		if (entity == null || !entity.getMetaExpedient().getId().equals(metaExpedientId)) {
			throw new NotFoundException(id, MetaExpedientTascaEntity.class);
		}
		return entity;
	}

	private static final Logger logger = LoggerFactory.getLogger(MetaExpedientServiceImpl.class);

}