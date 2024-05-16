/**
 * 
 */
package es.caib.ripea.core.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import es.caib.ripea.core.api.dto.ArbreDto;
import es.caib.ripea.core.api.dto.ArbreJsonDto;
import es.caib.ripea.core.api.dto.ArbreNodeDto;
import es.caib.ripea.core.api.dto.CarpetaDto;
import es.caib.ripea.core.api.dto.ExpedientCarpetaArbreDto;
import es.caib.ripea.core.entity.CarpetaEntity;
import es.caib.ripea.core.entity.ContingutEntity;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientCarpetaArbreEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.repository.CarpetaRepository;
import es.caib.ripea.core.repository.ContingutRepository;
import es.caib.ripea.core.repository.EntitatRepository;
import es.caib.ripea.core.repository.ExpedientCarpetaArbreRepository;

/**
 * Helper per a convertir les dades de paginació entre el DTO
 * i Spring-Data.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class CarpetaHelper {

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
	private ExpedientCarpetaArbreRepository expedientCarpetaArbreRepository;
	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private CacheHelper cacheHelper;
	
	public CarpetaDto create(
			Long entitatId,
			Long pareId,
			String nom,
			boolean alreadyCreatedInDB,
			Long carpetaId,
			boolean alreadyCreatedInArxiu,
			String arxiuUuid, 
			boolean fromAnotacio, 
			String rolActual, 
			boolean comprovarAgafatPerUsuariActual) {
		logger.debug("Creant nova carpeta ("
				+ "entitatId=" + entitatId + ", "
				+ "pareId=" + pareId + ", "
				+ "alreadyCreatedInDB=" + alreadyCreatedInDB + ", "
				+ "carpetaId=" + carpetaId + ", "
				+ "alreadyCreatedInArxiu=" + alreadyCreatedInArxiu + ", "
				+ "arxiuUuid=" + arxiuUuid + ")");
		CarpetaEntity carpetaEntity;
		boolean throwException = false;
		if (throwException) {
			throw new RuntimeException("EXCEPION BEFORE CREATING CARPETA IN DB!!!!!! ");
		}
		if (alreadyCreatedInDB) {
			carpetaEntity = carpetaRepository.findOne(carpetaId);
		} else {
			// TODO: això causa problemes al intentar obtenir el pare d'un metaexpedient sense pare
			ContingutEntity pare = contingutHelper.comprovarContingutDinsExpedientModificable(
					entitatId,
					pareId,
					false,
					false,
					false,
					false, 
					false, 
					comprovarAgafatPerUsuariActual, 
					rolActual);
			ExpedientEntity expedient = contingutHelper.getExpedientSuperior(
					pare,
					true,
					false,
					false, 
					rolActual);
			contingutHelper.comprovarNomValid(
					pare,
					nom,
					null,
					CarpetaEntity.class);
			carpetaEntity = CarpetaEntity.getBuilder(
					nom,
					pare,
					pare.getEntitat(),
					expedient).build();
			carpetaEntity = carpetaRepository.save(carpetaEntity);
			pare.addFill(carpetaEntity);
			contingutRepository.save(pare);
			// Registra al log la creació de la carpeta
			contingutLogHelper.logCreacio(
					carpetaEntity,
					true,
					true);
		}
		boolean throwException1 = false;
		if (throwException1) {
			throw new RuntimeException("EXCEPION BEFORE CREATING CARPETA IN ARXIU!!!!!! ");
		}
		if (alreadyCreatedInArxiu) {
			carpetaEntity.updateArxiu(arxiuUuid);
		} else {
			contingutHelper.arxiuPropagarModificacio(carpetaEntity);
		}
		CarpetaDto dto = toCarpetaDto(carpetaEntity);
		return dto;
	}

	public CarpetaDto toCarpetaDto(
			CarpetaEntity carpeta) {
		return (CarpetaDto) contingutHelper.toContingutDto(
				carpeta, false, false);
	}
	
	public List<ArbreDto<ExpedientCarpetaArbreDto>> obtenirArbreCarpetesPerExpedient(Long entitatId, ExpedientEntity expedient) {
		List<ArbreDto<ExpedientCarpetaArbreDto>> expedients = new ArrayList<ArbreDto<ExpedientCarpetaArbreDto>>();

		long t0 = System.currentTimeMillis();
		if (cacheHelper.mostrarLogsRendiment())
			logger.info("CarpetaHelper.obtenirArbreCarpetesPerExpedient start ( entitatId=" + entitatId + 
					", expedient=" + expedient.getNom() + ")");
		
		long t1 = System.currentTimeMillis();
		
		// Recupera carptes expedient actual
		List<ExpedientCarpetaArbreDto> carpetesExpedient = findCarpetesExpedient(entitatId, expedient);

		if (cacheHelper.mostrarLogsRendiment())
			logger.info("CarpetaHelper.obtenirArbreCarpetesPerExpedient findCarpetesExpedient time: " + (System.currentTimeMillis() - t1) + " ms");
		
		// Afegeix l'expedient com carpeta arrel
		ExpedientCarpetaArbreDto expedientArbre = new ExpedientCarpetaArbreDto();
		expedientArbre.setId(expedient.getId());
		expedientArbre.setNom(expedient.getNom());
		ArbreDto<ExpedientCarpetaArbreDto> expedientArrel = new ArbreDto<ExpedientCarpetaArbreDto>(true);
			
		ArbreNodeDto<ExpedientCarpetaArbreDto> currentArbreNode =  new ArbreNodeDto<ExpedientCarpetaArbreDto>(
				null,
				expedientArbre);
		
		long t2 = System.currentTimeMillis();
		for (ExpedientCarpetaArbreDto fill: carpetesExpedient) {
			// recuperar estructura per cada fill recursivament
			currentArbreNode.addFill(
					obtenirArbreCarpetesPerMetaExpedient(
							fill,
							currentArbreNode));
		}
		
		if (cacheHelper.mostrarLogsRendiment())
			logger.info("CarpetaHelper.obtenirArbreCarpetesPerExpedient obtenirArbreCarpetesPerMetaExpedient time:  " + (System.currentTimeMillis() - t2) + " ms");
		
		expedientArrel.setArrel(currentArbreNode);
		expedients.add(expedientArrel);
		
		if (cacheHelper.mostrarLogsRendiment())
			logger.info("CarpetaHelper.obtenirArbreCarpetesPerExpedient end:  " + (System.currentTimeMillis() - t0) + " ms");
		
		return expedients;
	}

	public Map<String, Long> crearEstructuraCarpetes(
			Long entitatId,
			Set<ArbreJsonDto> estructuraCarpetes,
			Long expedientId,
			String carpetaDestiId) {
		ContingutEntity contingut = contingutHelper.comprovarContingutDinsExpedientModificable(
				entitatId,
				expedientId,
				false,
				false,
				false,
				false, false, true, null);
		Map<String, Long> carpetaNovaId = new HashMap<String, Long>();
		
		for (ArbreJsonDto carpeta: estructuraCarpetes) {
			ContingutEntity pare = null;
			crearCarpeta(
					contingut.getEntitat(),
					carpeta,
					pare,
					carpetaDestiId,
					carpetaNovaId);
		}
		
		return carpetaNovaId;
	}

	public void crearCarpeta(
			EntitatEntity entitat,
			ArbreJsonDto carpeta,
			ContingutEntity pare,
			String carpetaDestiId, 
			Map<String, Long> carpetaNovaId) {

		// crear carpeta actual
		Long carpetaId = null;
		String carpetaDestiIdJstree = null;
		try {
			carpetaId = Long.valueOf(carpeta.getId());
		} catch (NumberFormatException nfe) {}
		
		if (carpetaId != null) {
			pare = entityComprovarHelper.comprovarContingut(carpetaId);
		} else {
			carpetaDestiIdJstree = carpeta.getId();
			CarpetaDto carpetaCreada = create(
					entitat.getId(), 
					pare.getId(), 
					carpeta.getText(), 
					false,
					null,
					false,
					null, 
					false, 
					null, 
					true);
			carpetaNovaId.put(carpetaDestiIdJstree, carpetaCreada.getId());
		}
		
		// crear recursivament totes les carpetes
		if (!carpeta.getChildren().isEmpty()) {
			for (ArbreJsonDto subcarpeta : carpeta.getChildren()) {
				crearCarpeta(
						entitat,
						subcarpeta, 
						pare,
						carpetaDestiId,
						carpetaNovaId);
			}
		}
	}
	
	public ArbreNodeDto<ExpedientCarpetaArbreDto> obtenirArbreCarpetesPerMetaExpedient(
			ExpedientCarpetaArbreDto metaExpedientCarpetaDto,
			ArbreNodeDto<ExpedientCarpetaArbreDto> pare) {
		ArbreNodeDto<ExpedientCarpetaArbreDto> currentArbreNode =  new ArbreNodeDto<ExpedientCarpetaArbreDto>(
				pare,
				metaExpedientCarpetaDto);
		// crear estructura carpetes a partir del pare actual
		for (ExpedientCarpetaArbreDto fill: metaExpedientCarpetaDto.getFills()) {
			// recuperar estructura per cada fill recursivament
			currentArbreNode.addFill(
					obtenirArbreCarpetesPerMetaExpedient(
							fill,
							currentArbreNode));
		}
		return currentArbreNode;
	}
	
	private List<ExpedientCarpetaArbreDto> findCarpetesExpedient(Long entitatId, ExpedientEntity expedient) {
		List<ExpedientCarpetaArbreEntity> expedientCarpetes = expedientCarpetaArbreRepository.findByPare(entitatId, expedient.getId());
		return conversioTipusHelper.convertirList(
				expedientCarpetes, 
				ExpedientCarpetaArbreDto.class);
	}

	private static final Logger logger = LoggerFactory.getLogger(CarpetaHelper.class);


}
