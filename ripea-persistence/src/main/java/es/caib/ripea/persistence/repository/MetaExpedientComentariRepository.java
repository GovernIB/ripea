package es.caib.ripea.persistence.repository;

import es.caib.ripea.persistence.entity.MetaExpedientComentariEntity;
import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public interface MetaExpedientComentariRepository extends JpaRepository<MetaExpedientComentariEntity, Long> {
	
	List<MetaExpedientComentariEntity> findByMetaExpedientOrderByCreatedDateAsc(
			MetaExpedientEntity metaExpedient);
	
	@Query(	  "select "
			+ "    count(comment) "
			+ "from "
			+ "    MetaExpedientComentariEntity comment "
			+ "where "
			+ "    comment.metaExpedient = :metaExpedient")
	long countByMetaExpedient(
			@Param("metaExpedient") MetaExpedientEntity metaExpedient);	
	
	
	public List<MetaExpedientComentariEntity> findByEmailEnviatFalseAndCreatedDateGreaterThan(LocalDateTime createdDate);
}