package es.caib.ripea.persistence.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import es.caib.ripea.persistence.entity.EntitatEntity;

@Component
public interface EntitatRepository extends JpaRepository<EntitatEntity, Long> {

	EntitatEntity findByCodi(String codi);

	EntitatEntity findByUnitatArrel(String unitatArrel);

	List<EntitatEntity> findByActiva(boolean activa);

	Page<EntitatEntity> findBy(
			Pageable pageable);

	List<EntitatEntity> findBy(
			Sort sort);

	@Query("select org.entitat " + 
			"from " + 
			"    OrganGestorEntity org " + 
			" where org.id in (:ids)")
	public List<EntitatEntity> findByOrgansIds(@Param("ids") List<Long> ids);
	
	@Modifying
 	@Query(value = "UPDATE IPA_ENTITAT " +
 			"SET CREATEDBY_CODI = CASE WHEN CREATEDBY_CODI = :codiAntic THEN :codiNou ELSE CREATEDBY_CODI END, " +
 			"    LASTMODIFIEDBY_CODI = CASE WHEN LASTMODIFIEDBY_CODI = :codiAntic THEN :codiNou ELSE LASTMODIFIEDBY_CODI END " +
 			"WHERE CREATEDBY_CODI = :codiAntic OR LASTMODIFIEDBY_CODI = :codiAntic",
 			nativeQuery = true)
	public int updateUsuariAuditoria(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);

	@Modifying
 	@Query(value = "UPDATE IPA_HISTORIC " +
 			"SET CREATEDBY_CODI = CASE WHEN CREATEDBY_CODI = :codiAntic THEN :codiNou ELSE CREATEDBY_CODI END, " +
 			"    LASTMODIFIEDBY_CODI = CASE WHEN LASTMODIFIEDBY_CODI = :codiAntic THEN :codiNou ELSE LASTMODIFIEDBY_CODI END " +
 			"WHERE CREATEDBY_CODI = :codiAntic OR LASTMODIFIEDBY_CODI = :codiAntic",
 			nativeQuery = true)
	public int updateUsuariAuditoriaHistoric(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);	
}