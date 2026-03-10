package es.caib.ripea.persistence.entity.resourcerepository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.ripea.persistence.base.repository.BaseRepository;
import es.caib.ripea.persistence.entity.resourceentity.MetaExpedientEstatResourceEntity;

public interface MetaExpedientEstatResourceRepository extends BaseRepository<MetaExpedientEstatResourceEntity, Long> {
	
	public int countByMetaExpedientId(Long metaExpedientId);
	
	@Modifying
    @Query("UPDATE MetaExpedientEstatResourceEntity e " +
           "SET e.inicial = false " +
           "WHERE e.metaExpedient.id = :metaExpedientId " +
           "AND e.id <> :excludedId")
    int updateInicialFalseForSameMetaExpedientExcludingId(
            @Param("metaExpedientId") Long metaExpedientId,
            @Param("excludedId") Long excludedId);
}