package es.caib.ripea.persistence.entity.resourcerepository;

import es.caib.ripea.persistence.base.repository.BaseRepository;
import es.caib.ripea.persistence.entity.resourceentity.DocumentResourceEntity;

import java.util.List;

public interface DocumentResourceRepository extends BaseRepository<DocumentResourceEntity, Long> {
    List<DocumentResourceEntity> findAllByPareId(Long pareId);
}