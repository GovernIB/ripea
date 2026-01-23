package es.caib.ripea.persistence.entity.resourcerepository;

import es.caib.ripea.persistence.base.repository.BaseRepository;
import es.caib.ripea.persistence.entity.resourceentity.MetaExpedientTascaValidacioResourceEntity;

public interface MetaExpedientTascaValidacioResourceRepository extends BaseRepository<MetaExpedientTascaValidacioResourceEntity, Long> {
	int countByMetaExpedientTascaId(Long metaExpedientTascaId);
}