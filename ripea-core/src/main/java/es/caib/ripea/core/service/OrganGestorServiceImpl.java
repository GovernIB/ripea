package es.caib.ripea.core.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

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
import es.caib.ripea.core.api.service.OrganGestorService;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.OrganGestorEntity;
import es.caib.ripea.core.helper.CacheHelper;
import es.caib.ripea.core.helper.ConversioTipusHelper;
import es.caib.ripea.core.helper.EntityComprovarHelper;
import es.caib.ripea.core.helper.PaginacioHelper;
import es.caib.ripea.core.helper.PermisosHelper;
import es.caib.ripea.core.repository.OrganGestorRepository;
import es.caib.ripea.core.security.ExtendedPermission;

@Service
public class OrganGestorServiceImpl implements OrganGestorService {
    @Autowired
    private EntityComprovarHelper entityComprovarHelper;
    @Autowired
    private ConversioTipusHelper conversioTipusHelper;
    @Resource
    private OrganGestorRepository organGestorRepository;
    @Autowired
    private PermisosHelper permisosHelper;
    @Autowired
    private PaginacioHelper paginacioHelper;
    @Autowired
    CacheHelper cacheHelper;

    @Transactional(readOnly = true)
    public List<OrganGestorDto> findAll() {
	List<OrganGestorEntity> organs = organGestorRepository.findAll();
	return conversioTipusHelper.convertirList(organs, OrganGestorDto.class);
    }

    @Transactional(readOnly = true)
    public OrganGestorDto findItem(Long id) {
	OrganGestorEntity organGestor = organGestorRepository.findOne(id);
	OrganGestorDto resposta = conversioTipusHelper.convertir(organGestor, OrganGestorDto.class);
	return resposta;
    }

    @Transactional(readOnly = true)
    public List<OrganGestorDto> findByEntitat(Long entitatId) {
	EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, true, false);
	List<OrganGestorEntity> organs = organGestorRepository.findByEntitat(entitat);
	return conversioTipusHelper.convertirList(organs, OrganGestorDto.class);
    }

    @Override
    @Transactional
    public boolean syncDir3OrgansGestors(Long entitatId) {
	EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, true, false);
	List<OrganGestorDto> organismes = cacheHelper.findOrganismesByEntitat(entitat.getUnitatArrel());
	for (OrganGestorDto o : organismes) {
	    OrganGestorEntity organDB = organGestorRepository.findByCodiDir3(o.getCodi());
	    if (organDB == null) { // create it
		organDB = new OrganGestorEntity();
		organDB.setCodiDir3(o.getCodi());
		organDB.setCodi(o.getCodi());
		organDB.setEntitat(entitat);
		organDB.setNom(o.getNom());
		organGestorRepository.save(organDB);

	    } else { // update it
		organDB.setNom(o.getNom());
		organGestorRepository.flush();
	    }

	}
	return true;
    }

    @Override
    @Transactional(readOnly = true)
    public PaginaDto<OrganGestorDto> findOrgansGestorsAmbFiltrePaginat(Long entitatId, OrganGestorFiltreDto filtre,
	    PaginacioParamsDto paginacioParams) {

	EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, true, false);
	Page<OrganGestorEntity> organs = null;
	if (filtre == null) {
	    organs = organGestorRepository.findByEntitat(entitat,
		    paginacioHelper.toSpringDataPageable(paginacioParams));
	} else {
	    organs = organGestorRepository.findByEntitatAndFiltre(entitat,
		    filtre.getCodi() == null || filtre.getCodi().isEmpty(),
		    filtre.getCodi() == null ? "" : filtre.getCodi(),
		    filtre.getNom() == null || filtre.getNom().isEmpty(),
		    filtre.getNom() == null ? "" : filtre.getNom(),
		    paginacioHelper.toSpringDataPageable(paginacioParams));
	}

	PaginaDto<OrganGestorDto> paginaOrgans = paginacioHelper.toPaginaDto(organs, OrganGestorDto.class);

	for (OrganGestorDto organ : paginaOrgans.getContingut()) {
	    List<PermisDto> permisos = permisosHelper.findPermisos(organ.getId(), OrganGestorEntity.class);
	    organ.setPermisos(permisos);
	}
	return paginaOrgans;
    }

    @Transactional
    @Override
    public List<PermisOrganGestorDto> findPermisos(Long entitatId) {
	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	logger.debug("Consulta com a administrador els permisos dels organs gestors de l'entitat (" + "id=" + entitatId
		+ ")");

	entityComprovarHelper.comprovarEntitat(entitatId, false, false, false);

	List<PermisOrganGestorDto> results = new ArrayList<PermisOrganGestorDto>();
	boolean esAdministradorEntitat = permisosHelper.isGrantedAll(entitatId, EntitatEntity.class,
		new Permission[] { ExtendedPermission.ADMINISTRATION }, auth);
	if (!esAdministradorEntitat) {
	    return results;
	}
	List<OrganGestorDto> organs = findByEntitat(entitatId);
	for (OrganGestorDto o : organs) {
	    List<PermisDto> permisosOrgan = permisosHelper.findPermisos(o.getId(), OrganGestorEntity.class);
	    for (PermisDto p : permisosOrgan) {
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
	logger.debug("Modificació com a administrador del permis de l'entitat (" + "id=" + id + ", " + "permis="
		+ permis + ")");
	boolean esAdministradorEntitat = permisosHelper.isGrantedAll(
		entitatId,
		EntitatEntity.class,
		new Permission[] {ExtendedPermission.ADMINISTRATION},
		auth);
	if (!esAdministradorEntitat) {
	    logger.error("Aquest usuari no té permisos d'administrador sobre l'entitat (id=" + id + ", usuari="
		    + auth.getName() + ")");
	    throw new SecurityException("Sense permisos per a gestionar aquest organ gestor");
	}
	permisosHelper.updatePermis(id, OrganGestorEntity.class, permis);
    }

    @Transactional
    @Override
    public void deletePermis(Long id, Long permisId, Long entitatId) {
	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	logger.debug("Eliminació del permis de l'òrgan gestor (" + "id=" + id + ", " + "permisId="
		+ permisId + ")");
	boolean esAdministradorEntitat = permisosHelper.isGrantedAll(entitatId, EntitatEntity.class,
		new Permission[] { ExtendedPermission.ADMINISTRATION }, auth);
	if (!esAdministradorEntitat) {
	    logger.error("Aquest usuari no té permisos d'administrador sobre l'òrgan gestor (" + "id=" + id + ", "
		    + "usuari=" + auth.getName() + ")");
	    throw new SecurityException("Sense permisos per administrar aquesta entitat");
	}
	permisosHelper.deletePermis(id, OrganGestorEntity.class, permisId);
    }

    private static final Logger logger = LoggerFactory.getLogger(EntitatServiceImpl.class);

}
