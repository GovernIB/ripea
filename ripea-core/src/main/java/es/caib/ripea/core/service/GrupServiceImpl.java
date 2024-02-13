package es.caib.ripea.core.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.core.api.dto.GrupDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.PermisDto;
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
import es.caib.ripea.core.helper.MetaExpedientHelper;
import es.caib.ripea.core.helper.OrganGestorHelper;
import es.caib.ripea.core.helper.PaginacioHelper;
import es.caib.ripea.core.helper.PermisosHelper;
import es.caib.ripea.core.repository.ExpedientPeticioRepository;
import es.caib.ripea.core.repository.GrupRepository;
import es.caib.ripea.core.repository.MetaExpedientRepository;
import es.caib.ripea.core.repository.OrganGestorRepository;

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
				organGestorRepository.findOne(grupDto.getOrganGestorId()));
		
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
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false, false, false);

		Page<GrupEntity> page = grupRepository.findByEntitatAndProcediment(
				entitat,
				paginacioParams.getFiltre() == null,
				paginacioParams.getFiltre() != null ? paginacioParams.getFiltre() : "",
				metaExpedientId == null,
				metaExpedientId,
				organId == null,
				organId,
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

		return pageDto;
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
		
		GrupEntity grupEntity = grupRepository.findOne(id);
		
		if (grupEntity.equals(metaExpedientEntity.getGrupPerDefecte())) {
			metaExpedientEntity.setGrupPerDefecte(null);
		}
		metaExpedientEntity.removeGrup(grupEntity);
		
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
	
	@Transactional(readOnly = true)
	@Override
	public List<GrupDto> findGrupsNoRelacionatAmbMetaExpedient(Long entitatId, Long metaExpedientId, Long organGestorId) {
	
		entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		
		List<GrupEntity> grupsProcediment = metaExpedientRepository.findOne(metaExpedientId).getGrups();
		
		List<GrupEntity> grupsAll = grupRepository.findByEntitatId(entitatId);
		
		List<GrupDto> grupsDto = new ArrayList<>();
	
		for (GrupEntity grupEntity : grupsAll) {
			
			boolean keepChecking = false;
			if (organGestorId != null) {
				if (grupEntity.getOrganGestor() != null && organGestorHelper.findParesIds(grupEntity.getOrganGestor().getId(), true).contains(organGestorId)) {
					keepChecking = true;
				}
			} else {
				keepChecking = true;
			}
			
			if (keepChecking) {
				boolean contains = false;
				for (GrupEntity gr : grupsProcediment) {
					if (gr.getId().equals(grupEntity.getId())) {
						contains = true;
						break;
					}
				}
				if (!contains) {
					grupsDto.add( conversioTipusHelper.convertir(grupEntity, GrupDto.class));
				} 
			}

		}

		return grupsDto;
	}
	
	
	@Transactional(readOnly = true)
	@Override
	public List<GrupDto> findGrups(
			Long entitatId,
			Long organGestorId,
			Long metaExpedientId) {
		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId);
		
		List<GrupEntity> grups = grupRepository.findByEntitatAndOrgan(
				entitat,
				metaExpedientId == null,
				metaExpedientId,
				organGestorId == null,
				organGestorId);
		
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
			
			OrganGestorEntity org = metaExpedient.getOrganGestor();
			if (cacheHelper.mostrarLogsGrups())
				logger.info("orgMetaExpedient");
			if (org == null) {
				org = organGestorRepository.findByCodi(exPet.getRegistre().getDestiCodi());
				if (cacheHelper.mostrarLogsGrups())
					logger.info("orgPeticio=");
			}

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