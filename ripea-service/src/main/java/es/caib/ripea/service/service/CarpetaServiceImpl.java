/**
 * 
 */
package es.caib.ripea.service.service;

import es.caib.ripea.persistence.entity.CarpetaEntity;
import es.caib.ripea.persistence.entity.ContingutEntity;
import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.ExpedientEntity;
import es.caib.ripea.persistence.repository.CarpetaRepository;
import es.caib.ripea.persistence.repository.ContingutRepository;
import es.caib.ripea.persistence.repository.EntitatRepository;
import es.caib.ripea.service.helper.*;
import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.exception.ContingutNotUniqueException;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.service.CarpetaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
		ContingutEntity pare = pareId != null ? contingutRepository.getOne(pareId) : null;
		if (! checkCarpetaUniqueContraint(nom, pare, entitatId)) {
			throw new ContingutNotUniqueException();
		}
		
		return carpetaHelper.create(
				entitatId,
				pareId,
				nom,
				false,
				null,
				false,
				null, 
				false, 
				null, 
				true);
	}

	@Transactional
	@Override
	public void update(
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
				false, false, true, null);
		if (! checkCarpetaUniqueContraint(nom, contingut.getPare(), entitatId)) {
			throw new ContingutNotUniqueException();
		}
		CarpetaEntity carpeta = entityComprovarHelper.comprovarCarpeta(
				contingut.getEntitat(),
				id);

		contingutHelper.comprovarNomValid(
				carpeta.getPare(),
				nom,
				id,
				CarpetaEntity.class);
		String nomOriginal = carpeta.getNom();
		carpeta.updateNom(
				nom);
		// Registra al log la modificació de la carpeta
		contingutLogHelper.log(
				carpeta,
				LogTipusEnumDto.MODIFICACIO,
				(!nomOriginal.equals(carpeta.getNom())) ? carpeta.getNom() : null,
				null,
				true,
				true);
		contingutHelper.arxiuPropagarModificacio(carpeta);
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
				expedientId, 
				true, 
				true, 
				false, 
				false, 
				false, 
				null);
		List<CarpetaEntity> carpetesEntity = carpetaRepository.findByPare(expedient);
		for (CarpetaEntity carpetaEntity : carpetesEntity) {
			carpetes.add(carpetaHelper.toCarpetaDto(carpetaEntity));
		}
		return carpetes;
	}

	@Transactional(readOnly = true)
	@Override
	public List<ArbreDto<ExpedientCarpetaArbreDto>> findArbreCarpetesExpedient(
			Long entitatId,
			List<ExpedientDto> expedientsMetaExpedient,
			Long contingutId,
			String rolActual) {

		ContingutEntity contingut = contingutHelper.comprovarContingutDinsExpedientModificable(
				entitatId,
				contingutId,
				false,
				false,
				false,
				false, false, true, rolActual);

		List<ArbreDto<ExpedientCarpetaArbreDto>> arbreExpedients = new ArrayList<ArbreDto<ExpedientCarpetaArbreDto>>();
		
		if (expedientsMetaExpedient != null) {
			for (ExpedientDto expedientDto : expedientsMetaExpedient) {
				ExpedientEntity expedient = entityComprovarHelper.comprovarExpedient(entitatId, expedientDto.getId());
				ArbreDto<ExpedientCarpetaArbreDto> arbreExpedient = carpetaHelper.obtenirArbreCarpetesPerExpedient(entitatId, expedient);
				
				arbreExpedients.add(arbreExpedient);
			}
		} else {
			// Moure contingut dins del mateix expedient
			ExpedientEntity expedient = contingut instanceof ExpedientEntity ? (ExpedientEntity)contingut : ((CarpetaEntity)contingut).getExpedient();
			ArbreDto<ExpedientCarpetaArbreDto> arbreExpedient = carpetaHelper.obtenirArbreCarpetesPerExpedient(entitatId, expedient);

			arbreExpedients.add(arbreExpedient);
		}
		
		return arbreExpedients;
	}
	
	@Override
	public FitxerDto exportIndexCarpetes(
			Long entitatId, 
			Set<Long> carpetaIds,
			String format) throws IOException {
		if (carpetaIds.size() == 1)
			logger.debug("Exportant índex de  (" + "entitatId=" + entitatId + ", " + "carpetaIds=" + carpetaIds.iterator().next() + ")");
		EntitatEntity entitatActual = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false, true, false);
		List<CarpetaEntity> carpetes = new ArrayList<CarpetaEntity>();
		for (Long carpetaId : carpetaIds) {
			CarpetaEntity carpeta = entityComprovarHelper.comprovarCarpeta(
					entitatActual,
					carpetaId);
			carpetes.add(carpeta);
		}
		
		FitxerDto resultat = new FitxerDto();
		
		try {
			resultat = carpetaHelper.exportarCarpetes(
					entitatActual, 
					carpetes,
					format);	
		} catch (Exception ex) {
			throw new RuntimeException("Hi ha hagut un problema generant l'índex de les carpetes", ex);
		}
		return resultat;
	}
	
	private boolean checkCarpetaUniqueContraint (String nom, ContingutEntity pare, Long entitatId) {
		EntitatEntity entitat = entitatId != null ? entitatRepository.getOne(entitatId) : null;
		return  contingutHelper.checkUniqueContraint(nom, pare, entitat, ContingutTipusEnumDto.CARPETA);
	}
	
	private static final Logger logger = LoggerFactory.getLogger(CarpetaServiceImpl.class);

}
