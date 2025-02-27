package es.caib.ripea.persistence.repository;

import es.caib.ripea.persistence.entity.ExpedientTascaComentariEntity;
import es.caib.ripea.persistence.entity.ExpedientTascaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ExpedientTascaComentariRepository extends JpaRepository<ExpedientTascaComentariEntity, Long> {
	
	List<ExpedientTascaComentariEntity> findByExpedientTascaOrderByCreatedDateAsc(ExpedientTascaEntity expedientTasca);
	
	@Query(	  "select "
			+ "    count(comment) "
			+ "from "
			+ "    ExpedientTascaComentariEntity comment "
			+ "where "
			+ "    comment.expedientTasca = :expedientTasca")
	long countByExpedientTasca(@Param("expedientTasca") ExpedientTascaEntity expedientTasca);
}