package es.caib.ripea.persistence.entity.resourcerepository;

import es.caib.ripea.persistence.base.repository.BaseRepository;
import es.caib.ripea.persistence.entity.resourceentity.ExpedientResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.InteressatResourceEntity;

import java.util.List;
import java.util.Optional;

public interface InteressatResourceRepository extends BaseRepository<InteressatResourceEntity, Long> {
    List<InteressatResourceEntity> findByExpedient(ExpedientResourceEntity expedient);
    List<InteressatResourceEntity> findByExpedientId(Long expedientId);

    Optional<InteressatResourceEntity> findByExpedientIdAndDocumentNum(Long expedientId, String documentNum);
}