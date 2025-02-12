/**
 * 
 */
package es.caib.ripea.service.service;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.UsuariEntity;
import es.caib.ripea.persistence.repository.EntitatRepository;
import es.caib.ripea.persistence.repository.UsuariRepository;
import es.caib.ripea.service.helper.*;
import es.caib.ripea.service.intf.dto.EntitatDto;
import es.caib.ripea.service.intf.dto.PaginaDto;
import es.caib.ripea.service.intf.dto.PaginacioParamsDto;
import es.caib.ripea.service.intf.dto.PermisDto;
import es.caib.ripea.service.intf.service.EntitatService;
import es.caib.ripea.service.intf.utils.Utils;
import es.caib.ripea.service.permission.ExtendedPermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Implementació del servei de gestió d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class EntitatServiceImpl implements EntitatService {

	@Autowired
	private EntitatRepository entitatRepository;
	@Autowired
	private UsuariRepository usuariRepository;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private PaginacioHelper paginacioHelper;
	@Autowired
	private PermisosHelper permisosHelper;
	@Autowired
	private CacheHelper cacheHelper;
	@Autowired
	private PermisosEntitatHelper permisosEntitatHelper;
	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private ConfigHelper configHelper;
	
	@Transactional
	@Override
	@CacheEvict(value = "entitatsUsuari", allEntries = true)
	public EntitatDto create(EntitatDto entitat) {

		logger.debug("Creant una nova entitat (entitat=" + entitat + ")");
		EntitatEntity entity = EntitatEntity.getBuilder(entitat.getCodi(), entitat.getNom(), entitat.getDescripcio(), entitat.getCif(),
								entitat.getUnitatArrel()).logoImgBytes(entitat.getLogoImgBytes()).capsaleraColorFons(entitat.getCapsaleraColorFons()).
								capsaleraColorLletra(entitat.getCapsaleraColorLletra()).build();
		configHelper.crearConfigsEntitat(entitat.getCodi());
		return conversioTipusHelper.convertir(entitatRepository.save(entity), EntitatDto.class);
	}

	@Transactional
	@Override
	public EntitatDto update(EntitatDto entitat) {

		logger.debug("Actualitzant entitat existent (entitat=" + entitat + ")");
		EntitatEntity entity = entityComprovarHelper.comprovarEntitat(entitat.getId(), false, false, false, false, false);
		entity.update(
				entitat.getCodi(),
				entitat.getNom(),
				entitat.getDescripcio(),
				entitat.getCif(),
				entitat.getUnitatArrel(),
				entitat.getCapsaleraColorFons(),
				entitat.getCapsaleraColorLletra());

	
		if (Utils.isNotEmpty(entitat.getLogoImgBytes())) {
			entity.updateLogoImgBytes(entitat.getLogoImgBytes());
		} else if (!entitat.isLogo()) {
			entity.updateLogoImgBytes(null);
		}

		return conversioTipusHelper.convertir(entity, EntitatDto.class);
	}
	
	
	@Transactional
	@Override
	public byte[] getLogo() throws NoSuchFileException, IOException {

		String filePath = configHelper.getConfig("es.caib.ripea.capsalera.logo");
		Path path = Paths.get(filePath);
		return Files.readAllBytes(path);
	}
	

	@Transactional
	@Override
	public EntitatDto updateActiva(Long id, boolean activa) {

		logger.debug("Actualitzant propietat activa d'una entitat existent (id=" + id + ", activa=" + activa + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(id, false, false, false, false, false);
		entitat.updateActiva(activa);
		return conversioTipusHelper.convertir(entitat, EntitatDto.class);
	}

	@Transactional
	@Override
	@CacheEvict(value = "entitatsUsuari", allEntries = true)
	public EntitatDto delete(Long id) {

		logger.debug("Esborrant entitat (id=" + id +  ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(id, false, false, false, false, false);
		configHelper.deleteConfigEntitat(entitat.getCodi());
		entitatRepository.delete(entitat);
		permisosHelper.deleteAcl(entitat.getId(), EntitatEntity.class);
		return conversioTipusHelper.convertir(entitat, EntitatDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public EntitatDto findById(Long id) {

		logger.debug("Consulta de l'entitat (id=" + id + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(id, false, false, false, false, false);
		EntitatDto dto = conversioTipusHelper.convertir(entitat, EntitatDto.class);
		permisosEntitatHelper.omplirPermisosPerEntitat(dto);
		return dto;
	}

	@Transactional(readOnly = true)
	@Override
	public EntitatDto findByCodi(String codi) {

		logger.debug("Consulta de l'entitat amb codi (codi=" + codi + ")");
		EntitatDto entitat = conversioTipusHelper.convertir(entitatRepository.findByCodi(codi), EntitatDto.class);
		if (entitat != null) {
			permisosEntitatHelper.omplirPermisosPerEntitat(entitat);
		}
		return entitat;
	}
	
	@Transactional(readOnly = true)
	@Override
	public EntitatDto findByUnitatArrel(String unitatArrel) {

		logger.debug("Consulta de l'entitat amb unitatArrel (unitatArrel=" + unitatArrel + ")");
		EntitatDto entitat = conversioTipusHelper.convertir(entitatRepository.findByUnitatArrel(unitatArrel), EntitatDto.class);
		if (entitat != null) {
			permisosEntitatHelper.omplirPermisosPerEntitat(entitat);
		}
		return entitat;
	}

	@Transactional(readOnly = true)
	@Override
	public PaginaDto<EntitatDto> findPaginat(PaginacioParamsDto paginacioParams) {

		logger.debug("Consulta de totes les entitats paginades (paginacioParams=" + paginacioParams + ")");
		PaginaDto<EntitatDto> resposta = paginacioHelper.esPaginacioActivada(paginacioParams) ?
						paginacioHelper.toPaginaDto(entitatRepository.findBy(paginacioHelper.toSpringDataPageable(paginacioParams)), EntitatDto.class)
						: paginacioHelper.toPaginaDto(entitatRepository.findBy(paginacioHelper.toSpringDataSort(paginacioParams)), EntitatDto.class);
		permisosEntitatHelper.omplirPermisosPerEntitats(resposta.getContingut(), true);
		return resposta;
	}
	
	
	@Transactional(readOnly = true)
	@Override
	public List<EntitatDto> findAll() {
		logger.debug("Consulta de totes les entitats");
		return conversioTipusHelper.convertirList(entitatRepository.findAll(), EntitatDto.class);
	}
	

	@Transactional(readOnly = true)
	@Override
	public List<EntitatDto> findAccessiblesUsuariActual() {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Consulta les entitats accessibles per l'usuari actual (usuari=" + auth.getName() + ")");
		return cacheHelper.findEntitatsAccessiblesUsuari(auth.getName());
	}
	
	@Transactional(readOnly = true)
	@Override
	public void evictEntitatsAccessiblesUsuari() {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Consulta les entitats accessibles per l'usuari actual (usuari=" + auth.getName() + ")");
		cacheHelper.evictEntitatsAccessiblesUsuari(auth.getName());
	}
	

	@Transactional
	@Override
	public List<PermisDto> findPermisSuper(Long id) {

		logger.debug("Consulta com a superusuari dels permisos de l'entitat (id=" + id + ")");
		entityComprovarHelper.comprovarEntitat(id, false, false, false, false, false);
		return permisosHelper.findPermisos(id, EntitatEntity.class);
	}
	@Transactional
	@Override
	@CacheEvict(value = "entitatsUsuari", allEntries = true)
	public void updatePermisSuper(Long id, PermisDto permis) {

		logger.debug("Modificació com a superusuari del permis de l'entitat (id=" + id + ", permis=" + permis + ")");
		entityComprovarHelper.comprovarEntitat(id, false, false, false, false, false);
		permisosHelper.updatePermis(id, EntitatEntity.class, permis);
	}
	@Transactional
	@Override
	@CacheEvict(value = "entitatsUsuari", allEntries = true)
	public void deletePermisSuper(Long id, Long permisId) {

		logger.debug("Eliminació com a superusuari del permis de l'entitat (id=" + id + ", permisId=" + permisId + ")");
		entityComprovarHelper.comprovarEntitat(id, false, false, false, false, false);
		permisosHelper.deletePermis(id, EntitatEntity.class, permisId);
	}

	@Transactional
	@Override
	public List<PermisDto> findPermisAdmin(Long id) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Consulta com a administrador del permis de l'entitat (id=" + id + ")");
		entityComprovarHelper.comprovarEntitat(id, false, false, false, false, false);
		boolean esAdministradorEntitat = permisosHelper.isGrantedAll(id, EntitatEntity.class, new Permission[] {ExtendedPermission.ADMINISTRATION}, auth);
		if (!esAdministradorEntitat) {
			logger.error("Aquest usuari no té permisos d'administrador sobre l'entitat (id=" + id + ", usuari=" + auth.getName() + ")");
			throw new SecurityException("Sense permisos per administrar aquesta entitat");
		}
		return permisosHelper.findPermisos(id, EntitatEntity.class);
	}

	@Transactional
	@Override
	@CacheEvict(value = "entitatsUsuari", allEntries = true)
	public void updatePermisAdmin(Long id, PermisDto permis) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Modificació com a administrador del permis de l'entitat (id=" + id + ", permis=" + permis + ")");
		entityComprovarHelper.comprovarEntitat(id, false, false, false, false, false);
		boolean esAdministradorEntitat = permisosHelper.isGrantedAll(id, EntitatEntity.class, new Permission[] {ExtendedPermission.ADMINISTRATION}, auth);
		if (!esAdministradorEntitat) {
			logger.error("Aquest usuari no té permisos d'administrador sobre l'entitat (id=" + id + ", usuari=" + auth.getName() + ")");
			throw new SecurityException("Sense permisos per administrar aquesta entitat");
		}
		permisosHelper.updatePermis(id, EntitatEntity.class, permis);
	}

	@Transactional
	@Override
	@CacheEvict(value = "entitatsUsuari", allEntries = true)
	public void deletePermisAdmin(Long id, Long permisId) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Eliminació com a administrador del permis de l'entitat (id=" + id + ", permisId=" + permisId + ")");
		entityComprovarHelper.comprovarEntitat(id, false, false, false, false, false);
		boolean esAdministradorEntitat = permisosHelper.isGrantedAll(id, EntitatEntity.class, new Permission[] {ExtendedPermission.ADMINISTRATION}, auth);
		if (!esAdministradorEntitat) {
			logger.error("Aquest usuari no té permisos d'administrador sobre l'entitat (id=" + id + ", usuari=" + auth.getName() + ")");
			throw new SecurityException("Sense permisos per administrar aquesta entitat");
		}
		permisosHelper.deletePermis(id, EntitatEntity.class, permisId);
	}

	@Override
	public void setConfigEntitat(EntitatDto entitatDto) {
		ConfigHelper.setEntitat(entitatDto);		
	}

	@Transactional
	@Override
	public void removeEntitatPerDefecteUsuari(String usuariCodi) {
		UsuariEntity usuari = usuariRepository.findByCodi(usuariCodi);
		usuari.removeEntitatPerDefecte();
	}

	private static final Logger logger = LoggerFactory.getLogger(EntitatServiceImpl.class);

}
