package es.caib.ripea.persistence.entity.resourcerepository;

import es.caib.ripea.persistence.base.repository.BaseRepository;
import es.caib.ripea.persistence.entity.resourceentity.DadaResourceEntity;

import java.util.List;

public interface DadaResourceRepository extends BaseRepository<DadaResourceEntity, Long> {
    List<DadaResourceEntity> findAllByMetaDadaIdOrderByOrdreAsc(Long id);
    List<DadaResourceEntity> findAllByNodeIdAndMetaDadaIdOrderByOrdreAsc(Long nodeId, Long metaDataId);
}