package es.caib.ripea.persistence.entity.resourcerepository;

import java.util.List;

import es.caib.ripea.persistence.base.repository.BaseRepository;
import es.caib.ripea.persistence.entity.resourceentity.MetaDadaResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.MetaNodeResourceEntity;
import es.caib.ripea.service.intf.dto.MultiplicitatEnumDto;

public interface MetaDadaResourceRepository extends BaseRepository<MetaDadaResourceEntity, Long> {

	List<MetaDadaResourceEntity> findByMetaNodeAndActivaTrueAndMultiplicitatIn(
			MetaNodeResourceEntity metaExpedient,
			MultiplicitatEnumDto[] multiplicitats);
}