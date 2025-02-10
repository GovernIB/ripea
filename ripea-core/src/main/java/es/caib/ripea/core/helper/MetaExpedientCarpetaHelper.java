package es.caib.ripea.core.helper;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.ripea.core.api.dto.MetaExpedientCarpetaDto;
import es.caib.ripea.core.persistence.MetaExpedientCarpetaEntity;
import es.caib.ripea.core.persistence.MetaExpedientEntity;
import es.caib.ripea.core.repository.MetaExpedientCarpetaRepository;

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
		MetaExpedientCarpetaEntity metaExpedientCarpeta = metaExpedientCarpetaRepository.findOne(carpetaId);
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
		MetaExpedientCarpetaEntity metaExpedientCarpeta = metaExpedientCarpetaRepository.findOne(carpetaIdJstree);
		metaExpedientCarpetaRepository.delete(metaExpedientCarpeta);
		return conversioTipusHelper.convertir(
				metaExpedientCarpeta, 
				MetaExpedientCarpetaDto.class);
	}
	
	public void removeAllCarpetes(MetaExpedientEntity metaExpedient) {
		List<MetaExpedientCarpetaEntity> metaExpedientCarpetes = metaExpedientCarpetaRepository.findByMetaExpedientAndPare(metaExpedient, null);
		metaExpedientCarpetaRepository.delete(metaExpedientCarpetes);
	}
}
