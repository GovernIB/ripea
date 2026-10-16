package es.caib.ripea.persistence.entity.resourcerepository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.ripea.persistence.base.repository.BaseRepository;
import es.caib.ripea.persistence.entity.resourceentity.AlertaResourceEntity;

public interface AlertaResourceRepository extends BaseRepository<AlertaResourceEntity, Long> {

	//[contingutId, nombre d'alertes no llegides] dels continguts amb alguna. Mateix criteri que la col·lecció
	//ContingutResourceEntity.alertes (@Where llegida = false). Màxim 1000 ids per crida (límit IN d'Oracle).
	@Query("select a.contingut.id, count(a) from AlertaResourceEntity a " +
			"where a.contingut.id in (:contingutIds) and a.llegida = false group by a.contingut.id")
	List<Object[]> countNoLlegidesByContingutIds(@Param("contingutIds") List<Long> contingutIds);
}
