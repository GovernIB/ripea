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
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.PermisDto;
import es.caib.ripea.core.api.dto.PermisOrganGestorDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.service.OrganGestorService;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.MetaExpedientOrganGestorEntity;
import es.caib.ripea.core.entity.OrganGestorEntity;
import es.caib.ripea.core.helper.CacheHelper;
import es.caib.ripea.core.helper.ConversioTipusHelper;
import es.caib.ripea.core.helper.EntityComprovarHelper;
import es.caib.ripea.core.helper.OrganGestorHelper;
import es.caib.ripea.core.helper.PaginacioHelper;
import es.caib.ripea.core.helper.PermisosHelper;
import es.caib.ripea.core.helper.PluginHelper;
import es.caib.ripea.core.repository.MetaExpedientOrganGestorRepository;
import es.caib.ripea.core.repository.MetaExpedientRepository;
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
    private MetaExpedientRepository metaExpedientRepository;
    @Autowired
    private MetaExpedientOrganGestorRepository metaExpedientOrganGestorRepository;
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
    
    @Transactional(readOnly = true)
    public List<OrganGestorDto> findAll() {
        List<OrganGestorEntity> organs = organGestorRepository.findAll();
        return conversioTipusHelper.convertirList(organs, OrganGestorDto.class);
    }

    @Transactional(readOnly = true)
    public OrganGestorDto findItem(Long id) {
        OrganGestorEntity organGestor = organGestorRepository.findOne(id);
        if (organGestor == null) {
        	throw new NotFoundException(id, OrganGestorEntity.class);
        }
        OrganGestorDto resposta = conversioTipusHelper.convertir(organGestor, OrganGestorDto.class);
        return resposta;
    }

    @Transactional(readOnly = true)
    public List<OrganGestorDto> findByEntitat(Long entitatId) {
        EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false, false);
        List<OrganGestorEntity> organs = organGestorRepository.findByEntitat(entitat);
        return conversioTipusHelper.convertirList(organs, OrganGestorDto.class);
    }

	@Override
	@Transactional(readOnly = true)
	public List<OrganGestorDto> findByEntitat(
			Long entitatId,
			String filterText) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false, false);
		List<OrganGestorEntity> organs = organGestorRepository.findByEntitatAndFiltre(
				entitat,
				filterText == null || filterText.isEmpty(),
				filterText);
		return conversioTipusHelper.convertirList(
				organs,
				OrganGestorDto.class);
	}
	
	
    
    @Override
    @Transactional
    public boolean syncDir3OrgansGestors(Long entitatId) throws Exception {
        EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, true, false, false);
		if (entitat.getUnitatArrel() == null || entitat.getUnitatArrel().isEmpty()) {
			throw new Exception("L'entitat actual no té cap codi DIR3 associat");
		}
		
		List<OrganGestorEntity> organismesDIR3 = new ArrayList<OrganGestorEntity>();
        
        List<OrganGestorDto> organismes = findOrganismesByEntitat(entitat.getUnitatArrel());
        for (OrganGestorDto o : organismes) {
            OrganGestorEntity organDB = organGestorRepository.findByCodiAndEntitat(o.getCodi(), entitat);
            if (organDB == null) { // create it
                organDB = new OrganGestorEntity();
                organDB.setCodi(o.getCodi());
                organDB.setEntitat(entitat);
                organDB.setNom(o.getNom());
                organDB.setPare(organGestorRepository.findByCodiAndEntitat(o.getPareCodi(), entitat));
                organGestorRepository.save(organDB);

            } else { // update it
                organDB.setNom(o.getNom());
                organDB.setActiu(true);
                organDB.setPare(organGestorRepository.findByCodiAndEntitat(o.getPareCodi(), entitat));
                organGestorRepository.flush();
                
            }
            
            organismesDIR3.add(organDB);
        }
        
        // Processam els organs gestors que ja no estan a dir3 i tenen instancies a la bbdd
        List<OrganGestorEntity> organismesNotInDIR3 = organGestorRepository.findByEntitat(entitat);
		organismesNotInDIR3.removeAll(organismesDIR3);
		for (OrganGestorEntity o : organismesNotInDIR3) {
			if (o.getMetaExpedients() == null || o.getMetaExpedients().size() == 0) {
				organGestorRepository.delete(o.getId());
			} else {
				o.setActiu(false);
				organGestorRepository.flush();
			}
		}

        return true;
    }
    
    @Override
    @Transactional(readOnly = true)
    public PaginaDto<OrganGestorDto> findOrgansGestorsAmbFiltrePaginat(Long entitatId,
                                                                       PaginacioParamsDto paginacioParams) {

        EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, true, false, false);

        Page<OrganGestorEntity> organs = organGestorRepository.findByEntitatAndFiltre(entitat,
                paginacioParams.getFiltre() == null,
                paginacioParams.getFiltre(),
                paginacioHelper.toSpringDataPageable(paginacioParams));


        PaginaDto<OrganGestorDto> paginaOrgans = paginacioHelper.toPaginaDto(organs, OrganGestorDto.class);

        for (OrganGestorDto organ : paginaOrgans.getContingut()) {
            List<PermisDto> permisos = permisosHelper.findPermisos(organ.getId(), OrganGestorEntity.class);
            organ.setPermisos(permisos);
        }
        return paginaOrgans;
    }


    @Override
    public List<OrganGestorDto> findAccessiblesUsuariActual(Long entitatId, Long organGestorId) {
       return findAccessiblesUsuariActual(entitatId, organGestorId, null);
    }
    
    @Transactional
    @Override
	public List<OrganGestorDto> findAccessiblesUsuariActual(Long entitatId, Long organGestorId, String filterText) {
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
				organGestorsCanditats, filterText == null || filterText.isEmpty(), filterText);

        return conversioTipusHelper.convertirList(filtrats, OrganGestorDto.class);
    }
    
    @Override
    public List<OrganGestorDto> findOrganismesEntitatAmbPermis(Long entitatId) {
    	EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false, false);
    	return conversioTipusHelper.convertirList(
    			organGestorHelper.findAmbEntitatPermis(
    					entitat,
    					ExtendedPermission.ADMINISTRATION),
				OrganGestorDto.class);	
    }
    
    @Transactional
    @Override
    public List<PermisOrganGestorDto> findPermisos(Long entitatId) {

        return findPermisos(entitatId, null);
    }

    @Transactional
    @Override
    public List<PermisOrganGestorDto> findPermisos(Long entitatId, Long organId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        logger.debug("Consulta com a administrador els permisos dels organs gestors de l'entitat (" + "id="
                + entitatId + ")");
        entityComprovarHelper.comprovarEntitat(entitatId, false, false, false, false);
        List<PermisOrganGestorDto> results = new ArrayList<PermisOrganGestorDto>();
        boolean esAdministradorEntitat = permisosHelper.isGrantedAll(entitatId, EntitatEntity.class,
                new Permission[] { ExtendedPermission.ADMINISTRATION }, auth);
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
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
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
        logger.debug("Modificació com a administrador del permis de l'entitat (" + "id=" + id + ", "
                + "permis=" + permis + ")");
        boolean esAdministradorEntitat = permisosHelper.isGrantedAll(entitatId, EntitatEntity.class,
                new Permission[] { ExtendedPermission.ADMINISTRATION }, auth);
        if (!esAdministradorEntitat) {
            logger.error("Aquest usuari no té permisos d'administrador sobre l'entitat (id=" + id
                    + ", usuari=" + auth.getName() + ")");
            throw new SecurityException("Sense permisos per a gestionar aquest organ gestor");
        }
        permisosHelper.updatePermis(id, OrganGestorEntity.class, permis);
        cacheHelper.evictEntitatsAccessiblesAllUsuaris();
    }

    @Transactional
    @Override
    public void deletePermis(Long id, Long permisId, Long entitatId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        logger.debug("Eliminació del permis de l'òrgan gestor (" + "id=" + id + ", " + "permisId=" + permisId
                + ")");
        boolean esAdministradorEntitat = permisosHelper.isGrantedAll(entitatId, EntitatEntity.class,
                new Permission[] { ExtendedPermission.ADMINISTRATION }, auth);
        if (!esAdministradorEntitat) {
            logger.error("Aquest usuari no té permisos d'administrador sobre l'òrgan gestor (" + "id=" + id
                    + ", " + "usuari=" + auth.getName() + ")");
            throw new SecurityException("Sense permisos per administrar aquesta entitat");
        }
        permisosHelper.deletePermis(id, OrganGestorEntity.class, permisId);
        cacheHelper.evictEntitatsAccessiblesAllUsuaris();
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
        for (NodeDir3 fill : root.getFills()) {
            OrganGestorDto organisme = new OrganGestorDto();
            organisme.setCodi(fill.getCodi());
            organisme.setNom(fill.getDenominacio());
            organisme.setPareCodi(root.getCodi());
            organismes.add(organisme);
            findOrganismesFills(fill, organismes);
        }
    }

	private void findPermesosByEntitatAndExpedientTipusIdAndFiltre(
			Long entitatId,
			Long metaExpedientId,
			Permission permis,
			String filtre) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, true, false, false, false);
		MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(entitat, metaExpedientId);
		List<OrganGestorEntity> organsGestors;
		if (metaExpedient.getOrganGestor() != null) {
			// S'han de retornar els fills de l'òrgan gestor del metaExpedient
			organsGestors = organGestorRepository.findByEntitatAndFiltreAndPareIdIn(
					entitat,
					filtre == null,
					filtre,
					Arrays.asList(metaExpedient.getOrganGestor().getId()));
		} else {
			// Cercam las parelles metaExpedient-organ amb permisos assignats directament
			List<Long> metaExpedientOrganIds = toListLong(permisosHelper.getObjectsIdsWithPermission(
					MetaExpedientOrganGestorEntity.class,
					permis));
			organsGestors = metaExpedientOrganGestorRepository.findOrganGestorByMetaExpedientAndFiltreAndOrganGestorPareIdIn(
					metaExpedient,
					filtre == null,
					filtre,
					metaExpedientOrganIds);
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
