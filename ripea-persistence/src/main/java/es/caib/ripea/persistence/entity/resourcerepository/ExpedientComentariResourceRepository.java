package es.caib.ripea.persistence.entity.resourcerepository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.ripea.persistence.base.repository.BaseRepository;
import es.caib.ripea.persistence.entity.resourceentity.ExpedientComentariResourceEntity;

public interface ExpedientComentariResourceRepository extends BaseRepository<ExpedientComentariResourceEntity, Long> {

	//[expedientId, nombre de comentaris] dels expedients amb algun comentari. Màxim 1000 ids per crida (límit IN d'Oracle).
	@Query("select c.expedient.id, count(c) from ExpedientComentariResourceEntity c " +
			"where c.expedient.id in (:expedientIds) group by c.expedient.id")
	List<Object[]> countByExpedientIds(@Param("expedientIds") List<Long> expedientIds);
}
