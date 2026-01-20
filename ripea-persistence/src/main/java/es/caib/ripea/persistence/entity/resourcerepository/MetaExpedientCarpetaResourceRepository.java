package es.caib.ripea.persistence.entity.resourcerepository;

import es.caib.ripea.persistence.base.repository.BaseRepository;
import es.caib.ripea.persistence.entity.resourceentity.MetaExpedientCarpetaResourceEntity;

public interface MetaExpedientCarpetaResourceRepository extends BaseRepository<MetaExpedientCarpetaResourceEntity, Long> {
	int countByMetaExpedientId(Long metaExpedientId);
}