package es.caib.ripea.service.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.ExpedientPeticioEntity;
import es.caib.ripea.persistence.entity.GrupEntity;
import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.persistence.entity.OrganGestorEntity;
import es.caib.ripea.persistence.repository.ExpedientPeticioRepository;
import es.caib.ripea.persistence.repository.GrupRepository;
import es.caib.ripea.persistence.repository.MetaExpedientRepository;
import es.caib.ripea.persistence.repository.OrganGestorRepository;
import es.caib.ripea.persistence.repository.command.GrupRepositoryCommnand;
import es.caib.ripea.service.helper.CacheHelper;
import es.caib.ripea.service.helper.ConversioTipusHelper;
import es.caib.ripea.service.helper.EntityComprovarHelper;
import es.caib.ripea.service.helper.GrupHelper;
import es.caib.ripea.service.helper.HibernateHelper;
import es.caib.ripea.service.helper.MetaExpedientHelper;
import es.caib.ripea.service.helper.OrganGestorCacheHelper;
import es.caib.ripea.service.helper.OrganGestorHelper;
import es.caib.ripea.service.helper.PaginacioHelper;
import es.caib.ripea.service.helper.PermisosHelper;
import es.caib.ripea.service.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.ripea.service.intf.dto.GrupDto;
import es.caib.ripea.service.intf.dto.GrupFiltreDto;
import es.caib.ripea.service.intf.dto.PaginaDto;
import es.caib.ripea.service.intf.dto.PaginacioParamsDto;
import es.caib.ripea.service.intf.dto.PermisDto;
import es.caib.ripea.service.intf.dto.ResultDto;
import es.caib.ripea.service.intf.dto.ResultEnumDto;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.service.GrupService;
import es.caib.ripea.service.intf.utils.Utils;
import es.caib.ripea.service.permission.ExtendedPermission;

@Service
public class GrupServiceImpl implements GrupService {
	
	@Autowired private GrupRepository grupRepository;
	@Autowired private GrupRepositoryCommnand grupRepositoryCommnand;
	@Autowired private EntityComprovarHelper entityComprovarHelper;
	@Autowired private ConversioTipusHelper conversioTipusHelper;
	@Autowired private PaginacioHelper paginacioHelper;
	@Autowired private MetaExpedientRepository metaExpedientRepository;
	@Autowired private MetaExpedientHelper metaExpedientHelper;
	@Autowired private GrupHelper grupHelper;
	@Autowired private PermisosHelper permisosHelper;
	@Autowired private OrganGestorRepository organGestorRepository;
	@Autowired private ExpedientPeticioRepository expedientPeticioRepository;
	@Autowired private OrganGestorHelper organGestorHelper;
	@Autowired private CacheHelper cacheHelper;
    @Autowired private OrganGestorCacheHelper organGestorCacheHelper;

	@Transactional
	@Override
	public GrupDto create(
			Long entitatId,
			GrupDto grupDto) throws NotFoundException {

		return grupHelper.create(
				entitatId,
				grupDto);
	}

	@Transactional
	@Override
	public GrupDto update(
			Long entitatId,
			GrupDto grupDto) throws NotFoundException {
		logger.debug("Actualitzant el grup per l'entitat (" +
				"entitatId=" + entitatId +
				"grupId=" + grupDto.getId() + ")");
		entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false, 
				false, 
				true);
		
		GrupEntity grupEntity = grupRepository.getOne(grupDto.getId());

		grupEntity.update(
				grupDto.getCodi(),
				grupDto.getDescripcio(),
				grupDto.getOrganGestorId() != null ? organGestorRepository.getOne(grupDto.getOrganGestorId()) : null);
		
		GrupDto dto = conversioTipusHelper.convertir(
				grupEntity,
				GrupDto.class);
		return dto;
	}

	@Transactional
	@Override
	public GrupDto delete(
			Long entitatId,
			Long id) throws NotFoundException {
		logger.debug("Esborrant el tipus documental per l'entitat (" +
				"entitatId=" + entitatId +
				"grupId=" + id + ")");
		entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false, 
				false, 
				true);

		GrupEntity grupEntity = grupRepository.getOne(id);
		
		grupRepository.delete(grupEntity);

		GrupDto dto = conversioTipusHelper.convertir(
				grupEntity,
				GrupDto.class);
		return dto;
	}

	@Transactional
	@Override
	public GrupDto findById(Long id) throws NotFoundException {
		logger.debug("Consultant el tipus documental per l'entitat (" +
				"grupId=" + id + ")");
		GrupEntity entity = grupRepository.getOne(id);

		GrupDto dto = conversioTipusHelper.convertir(
				entity,
				GrupDto.class);
		dto.setOrganGestorId(entity.getOrganGestor() != null ? entity.getOrganGestor().getId() : null);
		return dto;
	}


	@Transactional
	@Override
	public PaginaDto<GrupDto> findByEntitatPaginat(
			Long entitatId,
			Long metaExpedientId, 
			PaginacioParamsDto paginacioParams, 
			Long organId) throws NotFoundException {

		List<String> codisOrgansFills = null;
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false, false, false);

		if (organId != null) {
			OrganGestorEntity organ = organGestorRepository.getOne(organId);
//			codisOrgansFills = organGestorCacheHelper.getCodisOrgansFills(entitat.getCodi(), organ.getCodi());
		}

		Page<GrupEntity> page = grupRepositoryCommnand.findByEntitatAndProcediment(
				entitat,
				paginacioParams.getFiltre() != null ? paginacioParams.getFiltre() : "",
				metaExpedientId,
				null, // No filtram per organs
//				codisOrgansFills,
				paginacioHelper.toSpringDataPageable(paginacioParams));

		PaginaDto<GrupDto> pageDto = null;
		if (metaExpedientId != null) {
			GrupEntity grupPerDefecte = metaExpedientRepository.getOne(metaExpedientId).getGrupPerDefecte();
			pageDto = paginacioHelper.toPaginaDto(
					page,
					GrupDto.class);
			
			if (grupPerDefecte != null) {
				for (GrupDto grup : pageDto.getContingut()) {
					if (grup.getId().equals(grupPerDefecte.getId())) {
						grup.setPerDefecte(true);
					}
				}
			}

		} else {
			
			pageDto = paginacioHelper.toPaginaDto(
					page,
					GrupDto.class);
			
			omplirPermisosPerGrups(pageDto.getContingut());
		}

		if (pageDto!=null && pageDto.getContingut()!=null) {
			for (GrupDto grup : pageDto.getContingut()) {
				grup.setEditableUsuari(
						codisOrgansFills == null ||
								(grup.getOrganGestor()!=null && codisOrgansFills.contains(grup.getOrganGestor().getCodi())));
			}
		}

		return pageDto;
	}
	
	
	@Transactional
	@Override
	public ResultDto<GrupDto> findByEntitat(
			Long entitatId,
			Long metaExpedientId, 
			PaginacioParamsDto paginacioParams, 
			Long organId, 
			GrupFiltreDto filtre, 
			ResultEnumDto resultEnum) throws NotFoundException {
		
		ResultDto<GrupDto> result = new ResultDto<>();
		List<String> codisOrgansFills = null;

		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
		if (organId != null) {
			OrganGestorEntity organ = organGestorRepository.getOne(organId);
			codisOrgansFills = organGestorCacheHelper.getCodisOrgansFills(entitat.getCodi(), organ.getCodi());
		}

		if (resultEnum == ResultEnumDto.PAGE) {
			// ================================  RETURNS PAGE (DATATABLE) ==========================================
		
			Page<GrupEntity> page = grupRepositoryCommnand.findByEntitatAndProcediment(
					entitat,
					filtre.getCodi(),
					filtre.getDescripcio(),
					metaExpedientId,
					filtre.getOrganGestorAscendentId(),
					codisOrgansFills,
					paginacioHelper.toSpringDataPageable(paginacioParams));
			
			PaginaDto<GrupDto> pageDto = null;
			if (metaExpedientId != null) {
				GrupEntity grupPerDefecte = metaExpedientRepository.getOne(metaExpedientId).getGrupPerDefecte();
				pageDto = paginacioHelper.toPaginaDto(
						page,
						GrupDto.class);
				
				if (grupPerDefecte != null) {
					for (GrupDto grup : pageDto.getContingut()) {
						if (grup.getId().equals(grupPerDefecte.getId())) {
							grup.setPerDefecte(true);
						}
					}
				}

			} else {
				
				pageDto = paginacioHelper.toPaginaDto(
						page,
						GrupDto.class);
				
				omplirPermisosPerGrups(pageDto.getContingut());
			}

			result.setPagina(pageDto);
			
			
		} else {
			// ==================================  RETURNS IDS (SELECCIONAR TOTS) ============================================
			List<Long> ids = grupRepository.findIdsByEntitatAndProcediment(
					entitat,
					Utils.isEmpty(filtre.getCodi()),
					Utils.getEmptyStringIfNull(filtre.getCodi()),
					Utils.isEmpty(filtre.getDescripcio()),
					Utils.getEmptyStringIfNull(filtre.getDescripcio()),
					metaExpedientId == null,
					metaExpedientId,
					filtre.getOrganGestorAscendentId() == null,
					filtre.getOrganGestorAscendentId(),
					codisOrgansFills == null,
					codisOrgansFills);
			
			result.setIds(ids);
			
		}

		return result;
	}


	
	public void omplirPermisosPerGrups(List<GrupDto> grups) {

		List<Serializable> ids = new ArrayList<Serializable>();
		for (GrupDto entitat: grups) {
			ids.add(entitat.getId());
		}
		Map<Serializable, List<PermisDto>> permisos = permisosHelper.findPermisos(ids, GrupEntity.class);
		for (GrupDto entitat : grups)
			entitat.setPermisos(permisos.get(entitat.getId()));
	}

	
	
	
	
	@Transactional
	@Override
	public void relacionarAmbMetaExpedient(
			Long entitatId,
			Long metaExpedientId,
			Long id, 
			String rolActual, 
			Long organId, 
			boolean marcarPerDefecte) {
		logger.debug("Relacionant un grup amb metaxpedient (" +
				"metaExpedientId=" + metaExpedientId + ", " +
				"id=" + id + ")");
		
		entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false, false, false);
		
		MetaExpedientEntity metaExpedientEntity = metaExpedientRepository.getOne(metaExpedientId);
		
		GrupEntity grupEntity = grupRepository.getOne(id);
		
		metaExpedientEntity.addGrup(grupEntity);
		
		if (marcarPerDefecte) {
			metaExpedientEntity.setGrupPerDefecte(grupEntity);
		}
		
		if (rolActual.equals("IPA_ORGAN_ADMIN")) {
			metaExpedientHelper.canviarRevisioADisseny(entitatId, metaExpedientEntity.getId(), organId);
		}

	}
	
	
	@Override
	@Transactional
	public void desvincularAmbMetaExpedient(
			Long entitatId,
			Long metaExpedientId,
			Long id, 
			String rolActual, 
			Long organId) throws NotFoundException {
		logger.debug("Desvinculant un grup amb metaxpedient (" +
				"metaExpedientId=" + metaExpedientId + ", " +
				"id=" + id + ")");
		
		entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false, false, false);
		
		MetaExpedientEntity metaExpedientEntity = metaExpedientRepository.getOne(metaExpedientId);
		
		GrupEntity grupEntity = HibernateHelper.deproxy(grupRepository.getOne(id));

		metaExpedientEntity.removeGrup(grupEntity);
		
		if (metaExpedientEntity.getGrupPerDefecte() != null && grupEntity.getId().equals(metaExpedientEntity.getGrupPerDefecte().getId())) {
			metaExpedientEntity.setGrupPerDefecte(null);
		}
		
		if (rolActual.equals("IPA_ORGAN_ADMIN")) {
			metaExpedientHelper.canviarRevisioADisseny(entitatId, metaExpedientEntity.getId(), organId);
		}
	}
	
	
	
	@Transactional
	@Override
	public void marcarPerDefecte(
			Long entitatId, 
			Long procedimentId,
			Long grupId) throws NotFoundException {
		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false, 
				false, 
				true);
		
		MetaExpedientEntity metaExpedientEntity = entityComprovarHelper.comprovarMetaExpedient(
				entitat, 
				procedimentId);
		
		metaExpedientEntity.setGrupPerDefecte(grupRepository.getOne(grupId));
		
	}
	
	@Transactional
	@Override
	public void esborrarPerDefecte(
			Long entitatId, 
			Long procedimentId,
			Long grupId) throws NotFoundException {
		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false, 
				false, 
				true);
		
		MetaExpedientEntity metaExpedientEntity = entityComprovarHelper.comprovarMetaExpedient(
				entitat, 
				procedimentId);
		
		metaExpedientEntity.setGrupPerDefecte(null);
		
	}
	
	@Transactional
	@Override
	public boolean checkIfHasGrupPerDefecte(
			Long procedimentId) throws NotFoundException {
		
		MetaExpedientEntity metaExpedientEntity = metaExpedientRepository.getOne(procedimentId);
		return metaExpedientEntity.getGrupPerDefecte() != null;
	}
	
	@Transactional(readOnly = true)
	@Override
	public List<GrupDto> findGrupsNoRelacionatAmbMetaExpedient(Long entitatId, Long metaExpedientId, Long adminOrganId) {
	
		entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		
		MetaExpedientEntity metaExpedient = metaExpedientRepository.getOne(metaExpedientId);
		Long procedimentOrganId = metaExpedient.getOrganGestor() != null ? metaExpedient.getOrganGestor().getId() : null;
		List<GrupEntity> grupsProcedimentExisting = metaExpedient.getGrups();
		
		List<GrupEntity> grups = grupRepository.findByEntitatId(entitatId);
		
		
		// remove grups already related to procediment
		for (Iterator<GrupEntity> iter = grups.iterator(); iter.hasNext();) {
			GrupEntity grup = iter.next();
			
			boolean contains = false;
			for (GrupEntity gr : grupsProcedimentExisting) {
				if (gr.getId().equals(grup.getId())) {
					contains = true;
					break;
				}
			}
			if (contains) {
				iter.remove();
			}
		}
		
		// if is called by administador d'organ only leave grups assigned to organ or descendents
		if (adminOrganId != null) {
			for (Iterator<GrupEntity> iter = grups.iterator(); iter.hasNext();) {
				GrupEntity grup = iter.next();
				if (grup.getOrganGestor() == null || !organGestorHelper.findParesIds(grup.getOrganGestor().getId(), true).contains(adminOrganId)) {
					iter.remove();
				}
			}
		} 
		
		// if procediment belongs to organ remove grups that belong to other organ
		if (procedimentOrganId != null) {
			for (Iterator<GrupEntity> iter = grups.iterator(); iter.hasNext();) {
				GrupEntity grup = iter.next();
				if (grup.getOrganGestor() != null && !organGestorHelper.findParesIds(grup.getOrganGestor().getId(), true).contains(procedimentOrganId)) {
					iter.remove();
				}
			}
		}

		return conversioTipusHelper.convertirList(grups, GrupDto.class);
	}
	
	
	@Transactional(readOnly = true)
	@Override
	public List<GrupDto> findGrups(
			Long entitatId,
			Long organGestorId,
			Long metaExpedientId) {
		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
		List<String> codisOrgansFills = null;

		if (organGestorId != null) {
			OrganGestorEntity organ = organGestorRepository.getOne(organGestorId);
			codisOrgansFills = organGestorCacheHelper.getCodisOrgansFills(entitat.getCodi(), organ.getCodi());
		}
		
		List<GrupEntity> grups = grupRepositoryCommnand.findByEntitatAndOrgan(
				entitat,
				metaExpedientId,
				codisOrgansFills);
		
		return conversioTipusHelper.convertirList(
				grups, 
				GrupDto.class);
	}
	
	
	@Transactional(readOnly = true)
	@Override
	public List<GrupDto> findGrupsPermesosProcedimentsGestioActiva(
			Long entitatId,
			String rolActual, 
			Long organGestorId) {
		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
		
		List<GrupEntity> grups = new ArrayList<>();
		
		List<MetaExpedientEntity> metaExpedientsEnt = metaExpedientHelper.findAmbPermis(
				entitatId,
				ExtendedPermission.READ,
				true,
				null, 
				"IPA_ADMIN".equals(rolActual),
				"IPA_ORGAN_ADMIN".equals(rolActual),
				null, 
				false);
		
		boolean isAnyGestioAmbGrupsActiva = false;
		for (MetaExpedientEntity metaExpedientEntity : metaExpedientsEnt) {
			if (metaExpedientEntity.isGestioAmbGrupsActiva()) {
				isAnyGestioAmbGrupsActiva = true;
				break;
			}
		}
		
		if (isAnyGestioAmbGrupsActiva) {

			List<String> codisOrgansFills = null;

			if (organGestorId != null) {
				OrganGestorEntity organ = organGestorRepository.getOne(organGestorId);
				codisOrgansFills = organGestorCacheHelper.getCodisOrgansFills(entitat.getCodi(), organ.getCodi());
			}

			grups = grupRepositoryCommnand.findByEntitatAndOrgan(entitat, null, codisOrgansFills);
			if ("tothom".equals(rolActual)) {
				permisosHelper.filterGrantedAny(
						grups,
						new ObjectIdentifierExtractor<GrupEntity>() {
							public Long getObjectIdentifier(GrupEntity entitat) {
								return entitat.getId();
							}
						},
						GrupEntity.class,
						new Permission[] { ExtendedPermission.READ },
						SecurityContextHolder.getContext().getAuthentication());
			}
		}
		
		return conversioTipusHelper.convertirList(
				grups, 
				GrupDto.class);
	}


	@Transactional(readOnly = true)
	@Override
	public GrupDto findGrupById(
			Long grupId) {
		
		return conversioTipusHelper.convertir(
				grupRepository.findById(grupId),
				GrupDto.class);
	}
	
	@Transactional(readOnly = true)
	@Override
	public GrupDto findGrupByExpedientPeticioAndProcedimentId(
			Long expedientPeticioId,
			Long procedimentId) {
		
		GrupEntity grup = null;
		
		MetaExpedientEntity metaExpedient = metaExpedientRepository.getOne(procedimentId);
		
		if (cacheHelper.mostrarLogsGrups())
			logger.info("findGrupByExpedientPeticioAndProcedimentId start (metaExpedient=" + metaExpedient.getId() + ", " + metaExpedient.getCodi());
		
		List<GrupEntity> grups = metaExpedient.getGrups();

		if (Utils.isNotEmpty(grups)) {
			
			if (cacheHelper.mostrarLogsGrups())
				logger.info("grups");
			
			ExpedientPeticioEntity exPet = expedientPeticioRepository.getOne(expedientPeticioId);
			
			if (cacheHelper.mostrarLogsGrups())
				logger.info("exPet=" + exPet.getId() + ", " + exPet.getIdentificador());

			OrganGestorEntity org = organGestorRepository.findByCodi(exPet.getRegistre().getDestiCodi());

			while (grup == null && org != null) {
				
				if (cacheHelper.mostrarLogsGrups())
					logger.info("organ=" + org.getId() + ", " + org.getCodi());
				
				for (GrupEntity grupEntity : grups) {
					
					if (cacheHelper.mostrarLogsGrups())
						logger.info("grup=" + grupEntity.getId() + ", " + grupEntity.getCodi() + ", " + grupEntity.getOrganGestor());
					
					if (grupEntity.getOrganGestor() != null && grupEntity.getOrganGestor().getId().equals(org.getId())) {
						grup = grupEntity;
						
						if (cacheHelper.mostrarLogsGrups())
							logger.info("grupTrobat=" + grup.getId());
						break;
					}
				}
				org = org.getPare();

			}
			
			if (grup == null ) {
				grup = metaExpedient.getGrupPerDefecte();
			}
			
		} 
		
		if (cacheHelper.mostrarLogsGrups())
			logger.info("findGrupByExpedientPeticioAndProcedimentId end (metaExpedient=" + metaExpedient.getId() + ", " + metaExpedient.getCodi());
		
		if (grup != null ) {
			return conversioTipusHelper.convertir(grup, GrupDto.class);
		} else {
			return null;
		}
		

	}
	
	@Transactional(readOnly = true)
	public boolean checkIfAlreadyExistsWithCodi(
			Long entitatId,
			String codi, 
			Long grupId) {
		GrupEntity grup = grupRepository.findByEntitatIdAndCodi(entitatId, codi);
		return  grup!= null && !Objects.equals(grup.getId(), grupId);
	}
	
	@Transactional
	@Override
	public List<PermisDto> findPermisos(Long id) {
		return permisosHelper.findPermisos(id, GrupEntity.class);
	}
	
	@Transactional
	@Override
	@CacheEvict(value = "entitatsUsuari", allEntries = true)
	public void updatePermis(Long id, PermisDto permis) {

		permisosHelper.updatePermis(id, GrupEntity.class, permis);
	}
	@Transactional
	@Override
	@CacheEvict(value = "entitatsUsuari", allEntries = true)
	public void deletePermis(Long id, Long permisId) {

		permisosHelper.deletePermis(id, GrupEntity.class, permisId);
	}
	
	private static final Logger logger = LoggerFactory.getLogger(GrupServiceImpl.class);
}
