package es.caib.ripea.core.service;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.core.api.dto.OrganGestorDto;
import es.caib.ripea.core.api.dto.OrganGestorFiltreDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.PermisDto;
import es.caib.ripea.core.api.dto.PermisOrganGestorDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.service.OrganGestorService;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.MetaExpedientOrganGestorEntity;
import es.caib.ripea.core.entity.MetaNodeEntity;
import es.caib.ripea.core.entity.OrganGestorEntity;
import es.caib.ripea.core.helper.CacheHelper;
import es.caib.ripea.core.helper.ConversioTipusHelper;
import es.caib.ripea.core.helper.EntityComprovarHelper;
import es.caib.ripea.core.helper.OrganGestorHelper;
import es.caib.ripea.core.helper.PaginacioHelper;
import es.caib.ripea.core.helper.PermisosHelper;
import es.caib.ripea.core.helper.PluginHelper;
import es.caib.ripea.core.helper.UsuariHelper;
import es.caib.ripea.core.repository.ExpedientRepository;
import es.caib.ripea.core.repository.MetaExpedientOrganGestorRepository;
import es.caib.ripea.core.repository.OrganGestorRepository;
import es.caib.ripea.core.security.ExtendedPermission;
import es.caib.ripea.plugin.unitat.NodeDir3;

@Service
public class OrganGestorServiceImpl implements OrganGestorService {

	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private OrganGestorRepository organGestorRepository;
	@Autowired
	private MetaExpedientOrganGestorRepository metaExpedientOrganGestorRepository;
	@Autowired
	private ExpedientRepository expedientRepository;
	@Autowired
	private PermisosHelper permisosHelper;
	@Autowired
	private PaginacioHelper paginacioHelper;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private CacheHelper cacheHelper;
	@Autowired
	private OrganGestorHelper organGestorHelper;
	@Autowired
	private UsuariHelper usuariHelper;
	
	
	@Override
	@Transactional(readOnly = true)
	public List<OrganGestorDto> findAll() {
		List<OrganGestorEntity> organs = organGestorRepository.findAll();
		return conversioTipusHelper.convertirList(organs, OrganGestorDto.class);
	}
	
	
	@Transactional(readOnly = true)
	@Override
	public OrganGestorDto findById(Long entitatId, Long id) {
		logger.debug("Consulta del organ gestor (" + "entitatId=" + entitatId + ", " + "id=" + id + ")");

		OrganGestorEntity organGestor = entityComprovarHelper.comprovarOrganGestorAdmin(entitatId, id);
		OrganGestorDto resposta = conversioTipusHelper.convertir(organGestor, OrganGestorDto.class);
		resposta.setPareId(organGestor.getPare() != null ? organGestor.getPare().getId() : null);
		return resposta;
	}
	
	@Transactional
	@Override
	public OrganGestorDto create(Long entitatId, OrganGestorDto organGestorDto) {
		logger.debug(
				"Creant un nou organ (" + "entitatId=" + entitatId + ", " + "organGestor=" + organGestorDto +
						")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);
		
		OrganGestorEntity organPareEntity = null;
		if (organGestorDto.getPareId() != null) {
			organPareEntity = organGestorRepository.findOne(organGestorDto.getPareId());
		}
		
		OrganGestorEntity entity = OrganGestorEntity.getBuilder(
				organGestorDto.getCodi()).
				nom(organGestorDto.getNom()).
				entitat(entitat).
				pare(organPareEntity).
				gestioDirect(true).
				build();
		
		OrganGestorEntity organGestorEntity = organGestorRepository.save(entity);
		
		return conversioTipusHelper.convertir(organGestorEntity, OrganGestorDto.class);
	}

	@Transactional
	@Override
	public OrganGestorDto update(Long entitatId, OrganGestorDto organGestorDto) {
		logger.debug(
				"Actualitzant organ gestor existent (" + "entitatId=" + entitatId + ", " + "organGestorDto=" +
						organGestorDto + ")");
		entityComprovarHelper.comprovarEntitatPerMetaExpedients(entitatId);

		OrganGestorEntity organGestorEntity = entityComprovarHelper.comprovarOrganGestorAdmin(entitatId, organGestorDto.getId());
		
		OrganGestorEntity organPareEntity = null;
		if (organGestorDto.getPareId() != null) {
			organPareEntity = organGestorRepository.findOne(organGestorDto.getPareId());
		}
		
		organGestorEntity.update(
				organGestorDto.getCodi(),
				organGestorDto.getNom(),
				organPareEntity,
				true);

		return conversioTipusHelper.convertir(organGestorEntity, OrganGestorDto.class);
	}
	
	@Transactional
	@Override
	public void delete(Long entitatId, Long id) {
		logger.debug("Esborrant organ gestor (id=" + id + ")");
		OrganGestorEntity organGestor = entityComprovarHelper.comprovarOrganGestorAdmin(entitatId, id);
		
		organGestorRepository.delete(organGestor);
	}
	
	
	@Override
	@Transactional(readOnly = true)
	public OrganGestorDto findItem(Long id) {
		OrganGestorEntity organGestor = organGestorRepository.findOne(id);
		if (organGestor == null) {
			throw new NotFoundException(id, OrganGestorEntity.class);
		}
		OrganGestorDto resposta = conversioTipusHelper.convertir(organGestor, OrganGestorDto.class);
		return resposta;
	}
	
	@Override
	@Transactional(readOnly = true)
	public OrganGestorDto findItemByEntitatAndCodi(Long entitatId, String codi) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false, false, false);
		OrganGestorEntity organGestor = organGestorRepository.findByEntitatAndCodi(entitat, codi);
		if (organGestor == null) {
			throw new NotFoundException(codi, OrganGestorEntity.class);
		}
		OrganGestorDto resposta = conversioTipusHelper.convertir(organGestor, OrganGestorDto.class);
		return resposta;
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrganGestorDto> findByEntitat(Long entitatId) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false, false, false);
		List<OrganGestorEntity> organs = organGestorRepository.findByEntitat(entitat);
		return conversioTipusHelper.convertirList(organs, OrganGestorDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrganGestorDto> findByEntitat(
			Long entitatId,
			String filter) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false, 
				false, false);
		List<OrganGestorEntity> organs = organGestorRepository.findByEntitatAndFiltre(
				entitat,
				filter == null || filter.isEmpty(),
				filter);
		return conversioTipusHelper.convertirList(
				organs,
				OrganGestorDto.class);
	}

	@Override
	@Transactional
	public boolean syncDir3OrgansGestors(Long entitatId) throws Exception {
	    EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, true, false, false, false);
		if (entitat.getUnitatArrel() == null || entitat.getUnitatArrel().isEmpty()) {
			throw new Exception("L'entitat actual no té cap codi DIR3 associat");
		}
		List<OrganGestorEntity> organismesDIR3 = new ArrayList<OrganGestorEntity>();
		List<OrganGestorDto> organismes = findOrganismesByEntitat(entitat.getUnitatArrel());
		for (OrganGestorDto o : organismes) {
			OrganGestorEntity organDB = organGestorRepository.findByEntitatAndCodi(entitat, o.getCodi());
			if (organDB == null) { // create it
				organDB = new OrganGestorEntity();
				organDB.setCodi(o.getCodi());
				organDB.setEntitat(entitat);
				organDB.setNom(o.getNom());
				organDB.setPare(organGestorRepository.findByEntitatAndCodi(entitat, o.getPareCodi()));
				organGestorRepository.save(organDB);
			} else { // update it
				if (!organDB.isGestioDirect()) {
					organDB.setNom(o.getNom());
					organDB.setActiu(true);
					organDB.setPare(organGestorRepository.findByEntitatAndCodi(entitat, o.getPareCodi()));
					organGestorRepository.flush();
				}
			}
			organismesDIR3.add(organDB);
		}
		// Processam els organs gestors que ja no estan a dir3 i tenen instancies a la bbdd
		List<OrganGestorEntity> organismesNotInDIR3 = organGestorRepository.findByEntitat(entitat);
		organismesNotInDIR3.removeAll(organismesDIR3);
		for (OrganGestorEntity o : organismesNotInDIR3) {
			if (!o.isGestioDirect()) {
				
				List<MetaExpedientOrganGestorEntity> metaexporg = metaExpedientOrganGestorRepository.findByOrganGestor(o);
				List<ExpedientEntity> expedients = expedientRepository.findByOrganGestor(o);
				
				if ((o.getMetaExpedients() == null || o.getMetaExpedients().size() == 0) && (expedients == null || expedients.isEmpty()) && (metaexporg == null || metaexporg.isEmpty())) {
					organGestorRepository.delete(o.getId());
				} else {
					o.setActiu(false);
					organGestorRepository.flush();
				}
			}
		}
		return true;
	}

	@Override
	@Transactional(readOnly = true)
	public PaginaDto<OrganGestorDto> findAmbFiltrePaginat(
			Long entitatId,
			OrganGestorFiltreDto filtre, 
			PaginacioParamsDto paginacioParams) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, true, false, false, false);
		Page<OrganGestorEntity> organs = organGestorRepository.findAmbFiltrePaginat(
				entitat,
				filtre.getCodi() == null || filtre.getCodi().isEmpty(),
				filtre.getCodi(),
				filtre.getNom() == null || filtre.getNom().isEmpty(),
				filtre.getNom(),
				paginacioHelper.toSpringDataPageable(paginacioParams));
		PaginaDto<OrganGestorDto> paginaOrgans = paginacioHelper.toPaginaDto(organs, OrganGestorDto.class);
		for (OrganGestorDto organ : paginaOrgans.getContingut()) {
			List<PermisDto> permisos = permisosHelper.findPermisos(organ.getId(), OrganGestorEntity.class);
			organ.setPermisos(permisos);
		}
		return paginaOrgans;
	}
	
	@Transactional(readOnly = true)
	@Override
	public List<OrganGestorDto> findAccessiblesUsuariActualRolAdmin(Long entitatId, Long organGestorId) {
		return findAccessiblesUsuariActualRolAdmin(entitatId, organGestorId, null);
	}

	@Transactional(readOnly = true)
	@Override
	public List<OrganGestorDto> findAccessiblesUsuariActualRolAdmin(Long entitatId, Long organGestorId, String filter) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!permisosHelper.isGrantedAny(
				organGestorId,
				OrganGestorEntity.class,
				new Permission[] { ExtendedPermission.ADMINISTRATION },
				auth)) {
			return new ArrayList<OrganGestorDto>();
		}
		OrganGestorEntity organGestor = organGestorRepository.findOne(organGestorId);			
		List<OrganGestorEntity> organGestorsCanditats = organGestor.getAllChildren();
		List<OrganGestorEntity> filtrats = organGestorRepository.findByCanditatsAndFiltre(
				organGestorsCanditats, filter == null || filter.isEmpty(), filter);
		return conversioTipusHelper.convertirList(filtrats, OrganGestorDto.class);
	}
	
	@Transactional(readOnly = true)
	@Override
	public List<OrganGestorDto> findAccessiblesUsuariActualRolUsuari(Long entitatId, String filter, boolean directOrganPermisRequired) {
		
		List<OrganGestorEntity> filtrats = new ArrayList<OrganGestorEntity>();
		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false, 
				false, 
				false);
		
		// Cercam els metaExpedients amb permisos assignats directament
		List<Long> metaExpedientIdPermesos = toListLong(permisosHelper.getObjectsIdsWithPermission(
				MetaNodeEntity.class,
				ExtendedPermission.READ));
		
		// Si l'usuari actual te permis direct al metaExpedient, automaticament te permis per tots unitats fills del entitat
		if (metaExpedientIdPermesos != null && !metaExpedientIdPermesos.isEmpty() && !directOrganPermisRequired) {

			filtrats = organGestorRepository.findByEntitatAndFiltre(
					entitat,
					filter == null || filter.isEmpty(),
					filter);
		} else {
			
			List<OrganGestorEntity> organGestorsCanditats = entityComprovarHelper.getOrgansByOrgansAndCombinacioMetaExpedientsOrgansPermissions(entitat);
			organGestorsCanditats = !organGestorsCanditats.isEmpty() ? organGestorsCanditats : null;
			filtrats = organGestorRepository.findByCanditatsAndFiltre(organGestorsCanditats, filter == null || filter.isEmpty(), filter);
		}
		return conversioTipusHelper.convertirList(filtrats, OrganGestorDto.class);
	}
	
	

	@Transactional(readOnly = true)
	@Override
	public List<OrganGestorDto> findOrganismesEntitatAmbPermis(Long entitatId) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false, false, false);
		return conversioTipusHelper.convertirList(
				organGestorHelper.findAmbEntitatPermis(
						entitat,
						ExtendedPermission.ADMINISTRATION),
				OrganGestorDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public List<OrganGestorDto> findPermesosByEntitatAndExpedientTipusIdAndFiltre(
			Long entitatId,
			Long metaExpedientId,
			String filter, 
			Long expedientId) {
		List<OrganGestorEntity> organsPermesos = findPermesosByEntitatAndExpedientTipusIdAndFiltre(
				entitatId,
				metaExpedientId,
				expedientId == null ? ExtendedPermission.CREATE : ExtendedPermission.WRITE,
				filter, 
				expedientId);
		return conversioTipusHelper.convertirList(
				organsPermesos,
				OrganGestorDto.class);	
	}
	
	

	@Transactional(readOnly = true)
	@Override
	public List<PermisOrganGestorDto> findPermisos(Long entitatId) {
		return findPermisos(entitatId, null);
	}

	@Transactional(readOnly = true)
	@Override
	public List<PermisOrganGestorDto> findPermisos(Long entitatId, Long organId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Consulta com a administrador els permisos dels organs gestors de l'entitat (" + "id=" + entitatId + ")");
		entityComprovarHelper.comprovarEntitat(entitatId, false, false, false, false, false);
		List<PermisOrganGestorDto> results = new ArrayList<PermisOrganGestorDto>();
		boolean esAdministradorEntitat = permisosHelper.isGrantedAll(
				entitatId,
				EntitatEntity.class,
				new Permission[] { ExtendedPermission.ADMINISTRATION },
				auth);
		if (!esAdministradorEntitat) {
			return results;
		}
		List<OrganGestorDto> organs;
		if (organId == null) {
			organs = findByEntitat(entitatId);	
		} else {
			organs = new ArrayList<OrganGestorDto>();
			organs.add(findItem(organId));
		}
		for (OrganGestorDto o: organs) {
			List<PermisDto> permisosOrgan = permisosHelper.findPermisos(o.getId(), OrganGestorEntity.class);
			for (PermisDto p: permisosOrgan) {
				PermisOrganGestorDto permisOrgan = new PermisOrganGestorDto();
				try {
					BeanUtils.copyProperties(permisOrgan, p);
					permisOrgan.setPrincipalCodiNom(usuariHelper.getUsuariByCodi(permisOrgan.getPrincipalNom()).getNom() + " (" + permisOrgan.getPrincipalNom() + ")");
					
				} catch (IllegalAccessException | InvocationTargetException e) {
					e.printStackTrace();
				} catch (NotFoundException ex) {
					logger.debug("No s'ha trobat cap usuari amb el codi " + p.getPrincipalNom());
				}
				permisOrgan.setOrganGestor(o);
				results.add(permisOrgan);
			}
		}
		return results;
	}

	@Transactional
	@Override
	public void updatePermis(Long id, PermisDto permis, Long entitatId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Modificació com a administrador del permis de l'entitat (" + "id=" + id + ", " + "permis=" + permis + ")");
		boolean esAdministradorEntitat = permisosHelper.isGrantedAll(
				entitatId,
				EntitatEntity.class,
				new Permission[] { ExtendedPermission.ADMINISTRATION }, auth);
		if (!esAdministradorEntitat) {
			logger.error("Aquest usuari no té permisos d'administrador sobre l'entitat (id=" + id + ", usuari=" + auth.getName() + ")");
			throw new SecurityException("Sense permisos per a gestionar aquest organ gestor");
		}
		permisosHelper.updatePermis(id, OrganGestorEntity.class, permis);
		cacheHelper.evictEntitatsAccessiblesAllUsuaris();
	}
	@Transactional
	@Override
	public void deletePermis(Long id, Long permisId, Long entitatId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Eliminació del permis de l'òrgan gestor (" + "id=" + id + ", " + "permisId=" + permisId + ")");
		boolean esAdministradorEntitat = permisosHelper.isGrantedAll(
				entitatId,
				EntitatEntity.class,
				new Permission[] { ExtendedPermission.ADMINISTRATION },
				auth);
		if (!esAdministradorEntitat) {
			logger.error("Aquest usuari no té permisos d'administrador sobre l'òrgan gestor (" + "id=" + id + ", " + "usuari=" + auth.getName() + ")");
			throw new SecurityException("Sense permisos per administrar aquesta entitat");
		}
		permisosHelper.deletePermis(id, OrganGestorEntity.class, permisId);
		cacheHelper.evictEntitatsAccessiblesAllUsuaris();
	}

	private List<OrganGestorEntity> findPermesosByEntitatAndExpedientTipusIdAndFiltre(
			Long entitatId,
			Long metaExpedientId,
			Permission permis,
			String filtre, 
			Long expedientId) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, true, false, false, false, false);
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(entitat, metaExpedientId);
		List<OrganGestorEntity> organsGestors = null;
		if (metaExpedient.getOrganGestor() != null) {
			// S'han de retornar els fills de l'òrgan gestor del metaExpedient si l'usuari actual
			// te permisos per l'òrgan gestor.
			organsGestors = organGestorRepository.findByEntitatAndFiltreAndPareIdIn(
					entitat,
					filtre == null,
					filtre,
					Arrays.asList(metaExpedient.getOrganGestor().getId()));
		} else {
			// Cercam las parelles metaExpedient-organ amb permisos assignats 
			List<MetaExpedientOrganGestorEntity> metaExpedientOrgansGestors = metaExpedientOrganGestorRepository.findByMetaExpedient(metaExpedient);
			permisosHelper.filterGrantedAll(
					metaExpedientOrgansGestors,
					MetaExpedientOrganGestorEntity.class,
					new Permission[] { permis });

			if (!metaExpedientOrgansGestors.isEmpty()) {
				List<Long> organIds = metaExpedientOrganGestorRepository.findOrganGestorIdsByMetaExpedientOrganGestors(metaExpedientOrgansGestors);
				organGestorHelper.afegirOrganGestorFillsIds(entitat, organIds);
				
				organsGestors = organGestorRepository.findByEntitatAndFiltreAndIds(
						entitat,
						filtre == null || filtre.isEmpty(),
						filtre, 
						organIds);
				
			}
			
			// Si l'usuari actual te permis direct al metaExpedient, automaticament te permis per tots unitats fills del entitat
			if (organsGestors == null || organsGestors.isEmpty()) {
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				boolean metaNodeHasPermis = permisosHelper.isGrantedAll(
						metaExpedientId,
						MetaNodeEntity.class,
						new Permission[] {permis},
						auth);
				if (metaNodeHasPermis) {
					OrganGestorEntity organGestorEntitat = organGestorRepository.findByEntitatAndCodi(
								entitat,
								entitat.getUnitatArrel());
					organsGestors = organGestorRepository.findByEntitatAndFiltreAndPareIdIn(
							entitat,
							filtre == null,
							filtre,
							Arrays.asList(organGestorEntitat.getId()));
					organsGestors.add(0, organGestorEntitat);
				}
			}
		}
		
		// if we modify expedient we have to insure that we can still see its organ in dropdown even if permissions were removed 
		if (expedientId != null) {
			ExpedientEntity expedientEntity = entityComprovarHelper.comprovarExpedient(
					entitatId,
					expedientId,
					false,
					false,
					false,
					false,
					false,
					false);
			
			OrganGestorEntity organGestorEntity = expedientEntity.getOrganGestor();
			
			if (organsGestors == null) {
				organsGestors = new ArrayList<>();
			}
			boolean alreadyInTheList = false;
			for (OrganGestorEntity organGestor : organsGestors) {
				if (organGestor.getId().equals(organGestorEntity.getId())) {
					alreadyInTheList = true;
				}
			}
			if (!alreadyInTheList) {
				organsGestors.add(0, organGestorEntity);
			}
		}
		
		
		return organsGestors;
	}

	private List<OrganGestorDto> findOrganismesByEntitat(String codiDir3) {
		List<OrganGestorDto> organismes = new ArrayList<OrganGestorDto>();
		Map<String, NodeDir3> organigramaDir3 = pluginHelper.getOrganigramaOrganGestor(codiDir3);
		if (organigramaDir3 != null) {
		NodeDir3 arrel = organigramaDir3.get(codiDir3);
		OrganGestorDto organisme = new OrganGestorDto();
		organisme.setCodi(arrel.getCodi());
		organisme.setNom(arrel.getDenominacio());
		organisme.setPareCodi(null);
		organismes.add(organisme);
		findOrganismesFills(arrel, organismes);
		}
		return organismes;
	}
	
	private void findOrganismesFills(NodeDir3 root, List<OrganGestorDto> organismes) {
		for (NodeDir3 fill: root.getFills()) {
			OrganGestorDto organisme = new OrganGestorDto();
			organisme.setCodi(fill.getCodi());
			organisme.setNom(fill.getDenominacio());
			organisme.setPareCodi(root.getCodi());
			organismes.add(organisme);
			findOrganismesFills(fill, organismes);
		}
	}

	private List<Long> toListLong(List<Serializable> original) {
		List<Long> listLong = new ArrayList<Long>(original.size());
		for (Serializable s: original) { 
			listLong.add((Long)s); 
		}
		return listLong;
	}

	private static final Logger logger = LoggerFactory.getLogger(EntitatServiceImpl.class);

}
