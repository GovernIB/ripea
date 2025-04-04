package es.caib.ripea.persistence.entity.resourcerepository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.ripea.persistence.base.repository.BaseRepository;
import es.caib.ripea.persistence.entity.resourceentity.MetaExpedientResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.MetaExpedientSequenciaResourceEntity;

public interface MetaExpedientSequenciaResourceRepository extends BaseRepository<MetaExpedientSequenciaResourceEntity, Long> {

    Optional<MetaExpedientSequenciaResourceEntity> findByMetaExpedientAndAny(MetaExpedientResourceEntity metaExpedient, int any);

    @Query("select metaexp_seq.valor from MetaExpedientSequenciaResourceEntity metaexp_seq where metaexp_seq.metaExpedient = :metaExpedient and metaexp_seq.any = :any")
    Optional<Long> findValorByMetaExpedientAndAny(
            @Param("metaExpedient") MetaExpedientResourceEntity metaExpedient,
            @Param("any") int any);
}