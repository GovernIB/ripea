package es.caib.ripea.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.persistence.entity.MetaExpedientOrganGestorEntity;
import es.caib.ripea.persistence.entity.OrganGestorEntity;

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
	
	@Modifying
 	@Query(value = "UPDATE IPA_METAEXP_ORGAN " +
 			"SET CREATEDBY_CODI = CASE WHEN CREATEDBY_CODI = :codiAntic THEN :codiNou ELSE CREATEDBY_CODI END, " +
 			"    LASTMODIFIEDBY_CODI = CASE WHEN LASTMODIFIEDBY_CODI = :codiAntic THEN :codiNou ELSE LASTMODIFIEDBY_CODI END " +
 			"WHERE CREATEDBY_CODI = :codiAntic OR LASTMODIFIEDBY_CODI = :codiAntic",
 			nativeQuery = true)
	public int updateUsuariAuditoria(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);
}
