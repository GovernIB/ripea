/**
 * 
 */
package es.caib.ripea.core.persistence.repository;

import es.caib.ripea.core.persistence.entity.MetaExpedientComentariEntity;
import es.caib.ripea.core.persistence.entity.MetaExpedientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

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
	
	
	public List<MetaExpedientComentariEntity> findByEmailEnviatFalseAndCreatedDateGreaterThan(Date createdDate);


}
