/**
 * 
 */
package es.caib.ripea.core.helper;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import es.caib.ripea.core.api.dto.CarpetaDto;
import es.caib.ripea.core.entity.CarpetaEntity;
import es.caib.ripea.core.entity.ContingutEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.repository.CarpetaRepository;
import es.caib.ripea.core.repository.ContingutRepository;
import es.caib.ripea.core.repository.EntitatRepository;

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



	public CarpetaDto create(
			Long entitatId,
			Long pareId,
			String nom,
			boolean alreadyCreatedInDB,
			Long carpetaId,
			boolean alreadyCreatedInArxiu,
			String arxiuUuid) {
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
					false, false, null);
			ExpedientEntity expedient = contingutHelper.getExpedientSuperior(
					pare,
					true,
					false,
					false, false, null);
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
			contingutHelper.arxiuPropagarModificacio(
					carpetaEntity,
					null,
					false,
					false,
					null);
		}
		CarpetaDto dto = toCarpetaDto(carpetaEntity);
		return dto;
	}

	public CarpetaDto toCarpetaDto(
			CarpetaEntity carpeta) {
		return (CarpetaDto)contingutHelper.toContingutDto(
				carpeta,
				false,
				false,
				false,
				false,
				false,
				false,
				false, null, false);
	}

	private static final Logger logger = LoggerFactory.getLogger(CarpetaHelper.class);


}
