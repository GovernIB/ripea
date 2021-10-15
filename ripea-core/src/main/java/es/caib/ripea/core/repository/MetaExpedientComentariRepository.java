/**
 * 
 */
package es.caib.ripea.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.ripea.core.entity.MetaExpedientComentariEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;


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


}
