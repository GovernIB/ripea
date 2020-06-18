/**
 * 
 */
package es.caib.ripea.core.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.core.api.dto.CarpetaDto;
import es.caib.ripea.core.api.dto.LogTipusEnumDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.service.CarpetaService;
import es.caib.ripea.core.entity.CarpetaEntity;
import es.caib.ripea.core.entity.ContingutEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.helper.CarpetaHelper;
import es.caib.ripea.core.helper.ContingutHelper;
import es.caib.ripea.core.helper.ContingutLogHelper;
import es.caib.ripea.core.helper.ConversioTipusHelper;
import es.caib.ripea.core.helper.EntityComprovarHelper;
import es.caib.ripea.core.helper.PermisosHelper;
import es.caib.ripea.core.helper.PluginHelper;
import es.caib.ripea.core.repository.CarpetaRepository;
import es.caib.ripea.core.repository.ContingutRepository;
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
	private ContingutRepository contingutRepository;
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
	@Resource
	private CarpetaHelper carpetaHelper;



	@Transactional
	@Override
	public CarpetaDto create(
			Long entitatId,
			Long pareId,
			String nom) {

		return carpetaHelper.create(
				entitatId,
				pareId,
				nom,
				false,
				null,
				false,
				null);
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
		CarpetaDto dto = carpetaHelper.toCarpetaDto(carpeta);
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
		return carpetaHelper.toCarpetaDto(carpeta);
	}

	@Transactional
	@Override
	public List<CarpetaDto> findByEntitatAndExpedient(Long entitatId, Long expedientId) throws NotFoundException {
		logger.debug("Obtenint la carpeta ("
				+ "entitatId=" + entitatId + ", "
				+ "expedientId=" + expedientId + ")");
		List<CarpetaDto> carpetes = new ArrayList<CarpetaDto>();
		ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(
				entitatId, 
				expedientId, 
				true, 
				true, 
				false, 
				false, 
				false);
		List<CarpetaEntity> carpetesEntity = carpetaRepository.findByPare(expedient);
		for (CarpetaEntity carpetaEntity : carpetesEntity) {
			carpetes.add(carpetaHelper.toCarpetaDto(carpetaEntity));
		}
		return carpetes;
	}

	private static final Logger logger = LoggerFactory.getLogger(CarpetaServiceImpl.class);

}
