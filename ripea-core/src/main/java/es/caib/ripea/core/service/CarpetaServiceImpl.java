/**
 * 
 */
package es.caib.ripea.core.service;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.core.api.dto.CarpetaDto;
import es.caib.ripea.core.api.dto.LogTipusEnumDto;
import es.caib.ripea.core.api.service.CarpetaService;
import es.caib.ripea.core.entity.CarpetaEntity;
import es.caib.ripea.core.entity.ContingutEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.helper.ContingutHelper;
import es.caib.ripea.core.helper.ContingutLogHelper;
import es.caib.ripea.core.helper.ConversioTipusHelper;
import es.caib.ripea.core.helper.EntityComprovarHelper;
import es.caib.ripea.core.helper.PermisosHelper;
import es.caib.ripea.core.helper.PluginHelper;
import es.caib.ripea.core.repository.CarpetaRepository;
import es.caib.ripea.core.repository.EntitatRepository;

/**
 * Implementació dels mètodes per a gestionar carpetes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class CarpetaServiceImpl implements CarpetaService {

	@Resource
	private EntitatRepository entitatRepository;
	@Resource
	private CarpetaRepository carpetaRepository;

	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private PermisosHelper permisosHelper;
	@Resource
	private ContingutHelper contingutHelper;
	@Resource
	private PluginHelper pluginHelper;
	@Resource
	private EntityComprovarHelper entityComprovarHelper;
	@Resource
	private ContingutLogHelper contingutLogHelper;



	@Transactional
	@Override
	public CarpetaDto create(
			Long entitatId,
			Long contingutId,
			String nom) {
		logger.debug("Creant nova carpeta ("
				+ "entitatId=" + entitatId + ", "
				+ "contingutId=" + contingutId + ", "
				+ "nom=" + nom + ")");
		ContingutEntity contingut = contingutHelper.comprovarContingutDinsExpedientModificable(
				entitatId,
				contingutId,
				false,
				false,
				false,
				false);
		ExpedientEntity expedientSuperior = contingutHelper.getExpedientSuperior(
				contingut,
				true,
				false,
				false);
		contingutHelper.comprovarNomValid(
				contingut,
				nom,
				null,
				CarpetaEntity.class);
		CarpetaEntity carpeta = CarpetaEntity.getBuilder(
				nom,
				contingut,
				contingut.getEntitat(),
				expedientSuperior).build();
		carpeta = carpetaRepository.save(carpeta);
		// Registra al log la creació de la carpeta
		contingutLogHelper.logCreacio(
				carpeta,
				true,
				true);
		CarpetaDto dto = toCarpetaDto(carpeta);
		contingutHelper.arxiuPropagarModificacio(
				carpeta,
				null,
				false,
				false,
				null);
		return dto;
	}

	@Transactional
	@Override
	public CarpetaDto update(
			Long entitatId,
			Long id,
			String nom) {
		logger.debug("Actualitzant dades de la carpeta ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + id + ", "
				+ "nom=" + nom + ")");
		ContingutEntity contingut = contingutHelper.comprovarContingutDinsExpedientModificable(
				entitatId,
				id,
				false,
				false,
				false,
				false);
		CarpetaEntity carpeta = entityComprovarHelper.comprovarCarpeta(
				contingut.getEntitat(),
				id);
		contingutHelper.comprovarNomValid(
				carpeta.getPare(),
				nom,
				id,
				CarpetaEntity.class);
		String nomOriginal = carpeta.getNom();
		carpeta.update(
				nom);
		// Registra al log la modificació de la carpeta
		contingutLogHelper.log(
				carpeta,
				LogTipusEnumDto.MODIFICACIO,
				(!nomOriginal.equals(carpeta.getNom())) ? carpeta.getNom() : null,
				null,
				false,
				false);
		CarpetaDto dto = toCarpetaDto(carpeta);
		contingutHelper.arxiuPropagarModificacio(
				carpeta,
				null,
				false,
				false,
				null);
		return dto;
	}

	@Transactional(readOnly = true)
	@Override
	public CarpetaDto findById(
			Long entitatId,
			Long id) {
		logger.debug("Obtenint la carpeta ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + id + ")");
		ContingutEntity contingut = contingutHelper.comprovarContingutDinsExpedientAccessible(
				entitatId,
				id,
				true,
				false);
		CarpetaEntity carpeta = entityComprovarHelper.comprovarCarpeta(
				contingut.getEntitat(),
				id);
		return toCarpetaDto(carpeta);
	}

	private CarpetaDto toCarpetaDto(
			CarpetaEntity carpeta) {
		return (CarpetaDto)contingutHelper.toContingutDto(
				carpeta,
				false,
				false,
				false,
				false,
				false,
				false,
				false);
	}

	private static final Logger logger = LoggerFactory.getLogger(CarpetaServiceImpl.class);

}
