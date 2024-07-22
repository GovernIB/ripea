package es.caib.ripea.core.service;

import es.caib.ripea.core.api.dto.GrupDto;
import es.caib.ripea.core.api.dto.GrupFiltreDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.PermisDto;
import es.caib.ripea.core.api.dto.ResultDto;
import es.caib.ripea.core.api.dto.ResultEnumDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.service.GrupService;
import es.caib.ripea.core.api.utils.Utils;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientPeticioEntity;
import es.caib.ripea.core.entity.GrupEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.OrganGestorEntity;
import es.caib.ripea.core.helper.CacheHelper;
import es.caib.ripea.core.helper.ConversioTipusHelper;
import es.caib.ripea.core.helper.EntityComprovarHelper;
import es.caib.ripea.core.helper.GrupHelper;
import es.caib.ripea.core.helper.HibernateHelper;
import es.caib.ripea.core.helper.MetaExpedientHelper;
import es.caib.ripea.core.helper.OrganGestorCacheHelper;
import es.caib.ripea.core.helper.OrganGestorHelper;
import es.caib.ripea.core.helper.PaginacioHelper;
import es.caib.ripea.core.helper.PermisosHelper;
import es.caib.ripea.core.repository.ExpedientPeticioRepository;
import es.caib.ripea.core.repository.GrupRepository;
import es.caib.ripea.core.repository.MetaExpedientRepository;
import es.caib.ripea.core.repository.OrganGestorRepository;
import es.caib.ripea.core.security.ExtendedPermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class GrupServiceImpl implements GrupService {
	
	
	@Autowired
	private GrupRepository grupRepository;
	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private PaginacioHelper paginacioHelper;
	@Autowired
	private MetaExpedientRepository metaExpedientRepository;
	@Autowired
	private MetaExpedientHelper metaExpedientHelper;
	@Autowired
	private GrupHelper grupHelper;
	@Autowired
	private PermisosHelper permisosHelper;
	@Autowired
	private OrganGestorRepository organGestorRepository;
	@Autowired
	private ExpedientPeticioRepository expedientPeticioRepository;
	@Autowired
	private OrganGestorHelper organGestorHelper;
	@Autowired
	private CacheHelper cacheHelper;
    @Autowired
    private OrganGestorCacheHelper organGestorCacheHelper;


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
		
		GrupEntity grupEntity = grupRepository.findOne(grupDto.getId());

		grupEntity.update(
				grupDto.getCodi(),
				grupDto.getDescripcio(),
				grupDto.getOrganGestorId() != null ? organGestorRepository.findOne(grupDto.getOrganGestorId()) : null);
		
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

		GrupEntity grupEntity = grupRepository.findOne(id);
		
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
		GrupEntity entity = grupRepository.findOne(id);

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
			OrganGestorEntity organ = organGestorRepository.findOne(organId);
			codisOrgansFills = organGestorCacheHelper.getCodisOrgansFills(entitat.getCodi(), organ.getCodi());
		}

		Page<GrupEntity> page = grupRepository.findByEntitatAndProcediment(
				entitat,
				paginacioParams.getFiltre() == null,
				paginacioParams.getFiltre() != null ? paginacioParams.getFiltre() : "",
				metaExpedientId == null,
				metaExpedientId,
				true, //No filtram per organs
				codisOrgansFills,
				paginacioHelper.toSpringDataPageable(paginacioParams));

		PaginaDto<GrupDto> pageDto = null;
		if (metaExpedientId != null) {
			GrupEntity grupPerDefecte = metaExpedientRepository.findOne(metaExpedientId).getGrupPerDefecte();
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
			OrganGestorEntity organ = organGestorRepository.findOne(organId);
			codisOrgansFills = organGestorCacheHelper.getCodisOrgansFills(entitat.getCodi(), organ.getCodi());
		}

		if (resultEnum == ResultEnumDto.PAGE) {
			// ================================  RETURNS PAGE (DATATABLE) ==========================================
		
			Page<GrupEntity> page = grupRepository.findByEntitatAndProcediment(
					entitat,
					Utils.isEmpty(filtre.getCodi()),
					Utils.getEmptyStringIfNull(filtre.getCodi()),
					Utils.isEmpty(filtre.getDescripcio()),
					Utils.getEmptyStringIfNull(filtre.getDescripcio()),
					metaExpedientId == null,
					metaExpedientId,
					filtre.getOrganGestorAscendentId() == null,
					filtre.getOrganGestorAscendentId(),
//					organId == null,
//					organId,
//					codisOrgansFills == null,
					true, //No filtram per organs del usuari
					codisOrgansFills,
					paginacioHelper.toSpringDataPageable(paginacioParams));
			
			PaginaDto<GrupDto> pageDto = null;
			if (metaExpedientId != null) {
				GrupEntity grupPerDefecte = metaExpedientRepository.findOne(metaExpedientId).getGrupPerDefecte();
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
//					organId == null,
//					organId);
//					codisOrgansFills == null,
					true, //No filtram per organs del usuari
					codisOrgansFills);
			
			result.setIds(ids);
			
		}

		if (result.getPagina()!=null && result.getPagina().getContingut()!=null) {
			for (GrupDto grup : result.getPagina().getContingut()) {
				grup.setEditableUsuari(
						codisOrgansFills == null ||
						(grup.getOrganGestor()!=null && codisOrgansFills.contains(grup.getOrganGestor().getCodi())));
			}
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
		
		MetaExpedientEntity metaExpedientEntity = metaExpedientRepository.findOne(metaExpedientId);
		
		GrupEntity grupEntity = grupRepository.findOne(id);
		
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
		
		MetaExpedientEntity metaExpedientEntity = metaExpedientRepository.findOne(metaExpedientId);
		
		GrupEntity grupEntity = HibernateHelper.deproxy(grupRepository.findOne(id));

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
		
		metaExpedientEntity.setGrupPerDefecte(grupRepository.findOne(grupId));
		
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
		
		MetaExpedientEntity metaExpedientEntity = metaExpedientRepository.findOne(procedimentId);
		return metaExpedientEntity.getGrupPerDefecte() != null;
	}
	
	@Transactional(readOnly = true)
	@Override
	public List<GrupDto> findGrupsNoRelacionatAmbMetaExpedient(Long entitatId, Long metaExpedientId, Long adminOrganId) {
	
		entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		
		MetaExpedientEntity metaExpedient = metaExpedientRepository.findOne(metaExpedientId);
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
			OrganGestorEntity organ = organGestorRepository.findOne(organGestorId);
			codisOrgansFills = organGestorCacheHelper.getCodisOrgansFills(entitat.getCodi(), organ.getCodi());
		}
		
		List<GrupEntity> grups = grupRepository.findByEntitatAndOrgan(
				entitat,
				metaExpedientId == null,
				metaExpedientId,
				codisOrgansFills == null,
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
				OrganGestorEntity organ = organGestorRepository.findOne(organGestorId);
				codisOrgansFills = organGestorCacheHelper.getCodisOrgansFills(entitat.getCodi(), organ.getCodi());
			}

			grups = grupRepository.findByEntitatAndOrgan(
					entitat,
					true,
					null,
					codisOrgansFills == null,
					codisOrgansFills);
			if ("tothom".equals(rolActual)) {
				permisosHelper.filterGrantedAny(
						grups,
						GrupEntity.class,
						new Permission[] { ExtendedPermission.READ });
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
				grupRepository.findOne(grupId), 
				GrupDto.class);
	}
	
	@Transactional(readOnly = true)
	@Override
	public GrupDto findGrupByExpedientPeticioAndProcedimentId(
			Long expedientPeticioId,
			Long procedimentId) {
		
		GrupEntity grup = null;
		
		MetaExpedientEntity metaExpedient = metaExpedientRepository.findOne(procedimentId);
		
		if (cacheHelper.mostrarLogsGrups())
			logger.info("findGrupByExpedientPeticioAndProcedimentId start (metaExpedient=" + metaExpedient.getId() + ", " + metaExpedient.getCodi());
		
		List<GrupEntity> grups = metaExpedient.getGrups();

		if (Utils.isNotEmpty(grups)) {
			
			if (cacheHelper.mostrarLogsGrups())
				logger.info("grups");
			
			ExpedientPeticioEntity exPet = expedientPeticioRepository.findOne(expedientPeticioId);
			
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