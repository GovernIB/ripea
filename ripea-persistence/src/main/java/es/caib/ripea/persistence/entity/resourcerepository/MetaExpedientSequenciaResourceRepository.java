package es.caib.ripea.persistence.entity.resourcerepository;

import es.caib.ripea.persistence.base.repository.BaseRepository;
import es.caib.ripea.persistence.entity.resourceentity.GrupResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.MetaExpedientResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.MetaExpedientSequenciaResourceEntity;
import es.caib.ripea.service.intf.model.MetaExpedientResource;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Repositori per a la gestió d'expedients.
 *
 * @author Límit Tecnologies
 */
public interface MetaExpedientSequenciaResourceRepository extends BaseRepository<MetaExpedientSequenciaResourceEntity, Long> {

    Optional<MetaExpedientSequenciaResourceEntity> findByMetaExpedientAndAny(MetaExpedientResourceEntity metaExpedient, int any);

    @Query("select metaexp_seq.valor from MetaExpedientSequenciaResourceEntity metaexp_seq where metaexp_seq.metaExpedient = :metaExpedient and metaexp_seq.any = :any")
    Optional<Long> findValorByMetaExpedientAndAny(
            @Param("metaExpedient") MetaExpedientResourceEntity metaExpedient,
            @Param("any") int any);
}
