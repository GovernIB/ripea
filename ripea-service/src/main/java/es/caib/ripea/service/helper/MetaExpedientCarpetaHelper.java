package es.caib.ripea.service.helper;

import es.caib.ripea.core.persistence.entity.MetaExpedientCarpetaEntity;
import es.caib.ripea.core.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.core.persistence.repository.MetaExpedientCarpetaRepository;
import es.caib.ripea.service.intf.dto.MetaExpedientCarpetaDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Utilitats comunes per gestionar les carpetes d'un procediment
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class MetaExpedientCarpetaHelper {

	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private MetaExpedientCarpetaRepository metaExpedientCarpetaRepository;

	public List<MetaExpedientCarpetaDto> findCarpetesArrelMetaExpedient(MetaExpedientEntity metaExpedient) {
		List<MetaExpedientCarpetaEntity> metaExpedientCarpetes = metaExpedientCarpetaRepository.findByMetaExpedientAndPare(metaExpedient, null);
		return conversioTipusHelper.convertirList(
				metaExpedientCarpetes, 
				MetaExpedientCarpetaDto.class);
	}
	
	public List<MetaExpedientCarpetaDto> findCarpetesMetaExpedient(MetaExpedientEntity metaExpedient) {
		List<MetaExpedientCarpetaEntity> metaExpedientCarpetes = metaExpedientCarpetaRepository.findByMetaExpedient(metaExpedient);
		return conversioTipusHelper.convertirList(
				metaExpedientCarpetes, 
				MetaExpedientCarpetaDto.class);
	}

	public MetaExpedientCarpetaEntity actualitzarCarpeta(Long carpetaId, String carpetaNom) {
		MetaExpedientCarpetaEntity metaExpedientCarpeta = metaExpedientCarpetaRepository.getOne(carpetaId);
		metaExpedientCarpeta.update(carpetaNom);
		return metaExpedientCarpeta;
	}

	public MetaExpedientCarpetaEntity crearNovaCarpeta(
			String carpetaNom, 
			MetaExpedientCarpetaEntity carpetaPare,
			MetaExpedientEntity metaExpedient) {
		MetaExpedientCarpetaEntity metaExpedientCarpeta = MetaExpedientCarpetaEntity.getBuilder(
				carpetaNom, 
				carpetaPare, 
				metaExpedient).build();
		metaExpedientCarpetaRepository.save(metaExpedientCarpeta);
		return metaExpedientCarpeta;
	}

	public MetaExpedientCarpetaDto deleteCarpeta(Long carpetaIdJstree) {
		MetaExpedientCarpetaEntity metaExpedientCarpeta = metaExpedientCarpetaRepository.getOne(carpetaIdJstree);
		metaExpedientCarpetaRepository.delete(metaExpedientCarpeta);
		return conversioTipusHelper.convertir(
				metaExpedientCarpeta, 
				MetaExpedientCarpetaDto.class);
	}
	
	public void removeAllCarpetes(MetaExpedientEntity metaExpedient) {
		List<MetaExpedientCarpetaEntity> metaExpedientCarpetes = metaExpedientCarpetaRepository.findByMetaExpedientAndPare(metaExpedient, null);
		metaExpedientCarpetaRepository.deleteAll(metaExpedientCarpetes);
	}
}
