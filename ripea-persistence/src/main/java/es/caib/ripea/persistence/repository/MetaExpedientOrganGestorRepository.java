package es.caib.ripea.persistence.repository;

import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.persistence.entity.MetaExpedientOrganGestorEntity;
import es.caib.ripea.persistence.entity.OrganGestorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface MetaExpedientOrganGestorRepository extends JpaRepository<MetaExpedientOrganGestorEntity, Long> {

	MetaExpedientOrganGestorEntity findByMetaExpedientAndOrganGestor(
			MetaExpedientEntity metaExpedient,
			OrganGestorEntity organGestor);
	
	MetaExpedientOrganGestorEntity findByMetaExpedientIdAndOrganGestorId(
			Long metaExpedientId,
			Long organGestorId);

	List<MetaExpedientOrganGestorEntity> findByMetaExpedient(MetaExpedientEntity metaExpedient);
	
	@Query(	"select distinct " +
			"    meog.organGestor.codi " +
			"from " +
			"    MetaExpedientOrganGestorEntity meog " +
			"where " +
			"    meog.id in (:metaExpedientOrganGestorIds) ")
	public List<String> findOrganGestorCodisByMetaExpedientOrganGestorIds(
			@Param("metaExpedientOrganGestorIds") List<Long> metaExpedientOrganGestorIds);

	@Query(	"select distinct " +
			"    meog.organGestor.codi " +
			"from " +
			"    MetaExpedientOrganGestorEntity meog " +
			"where " +
			"    meog in (:metaExpedientOrganGestors) ")
	public List<String> findOrganGestorCodisByMetaExpedientOrganGestors(
			@Param("metaExpedientOrganGestors") List<MetaExpedientOrganGestorEntity> metaExpedientOrganGestors);
	

	@Query(	"select count(meog.id) from MetaExpedientOrganGestorEntity meog where meog.organGestor = :organGestor")
	Integer countByOrganGestor(@Param("organGestor") OrganGestorEntity organGestor);
}
