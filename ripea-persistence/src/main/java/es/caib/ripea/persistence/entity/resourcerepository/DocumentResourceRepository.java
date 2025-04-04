package es.caib.ripea.persistence.entity.resourcerepository;

import java.util.List;

import es.caib.ripea.persistence.base.repository.BaseRepository;
import es.caib.ripea.persistence.entity.resourceentity.DocumentResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.ExpedientResourceEntity;

public interface DocumentResourceRepository extends BaseRepository<DocumentResourceEntity, Long> {
    List<DocumentResourceEntity> findAllByPareId(Long pareId);
    List<DocumentResourceEntity> findByExpedientAndEsborrat(ExpedientResourceEntity expedient, int esborrat);
}