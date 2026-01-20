package es.caib.ripea.persistence.entity.resourcerepository;

import es.caib.ripea.persistence.base.repository.BaseRepository;
import es.caib.ripea.persistence.entity.resourceentity.MetaExpedientEstatResourceEntity;

public interface MetaExpedientEstatResourceRepository extends BaseRepository<MetaExpedientEstatResourceEntity, Long> {
	int countByMetaExpedientId(Long metaExpedientId);
}