package es.caib.ripea.persistence.entity.resourcerepository;

import java.util.List;

import es.caib.ripea.persistence.base.repository.BaseRepository;
import es.caib.ripea.persistence.entity.resourceentity.MetaDocumentResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.MetaExpedientResourceEntity;
import es.caib.ripea.service.intf.dto.MultiplicitatEnumDto;

public interface MetaDocumentResourceRepository extends BaseRepository<MetaDocumentResourceEntity, Long> {
	List<MetaDocumentResourceEntity> findByMetaExpedientAndMultiplicitatIn(MetaExpedientResourceEntity metaExpedient, MultiplicitatEnumDto[] multiplicitats);
}