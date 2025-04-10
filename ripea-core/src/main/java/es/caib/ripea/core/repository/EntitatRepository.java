/**
 * 
 */
package es.caib.ripea.core.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.ripea.core.entity.EntitatEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface EntitatRepository extends JpaRepository<EntitatEntity, Long> {

	EntitatEntity findByCodi(String codi);

	EntitatEntity findByUnitatArrel(String unitatArrel);

	List<EntitatEntity> findByActiva(boolean activa);

	@Query(	"from " +
			"    EntitatEntity ent " +
			"where " +
			"    :esNullFiltre = true " +
			" or lower(ent.codi) like lower('%'||:filtre||'%') " +
			" or lower(ent.nom) like lower('%'||:filtre||'%') " +
			" or lower(ent.cif) like lower('%'||:filtre||'%')) ")
	Page<EntitatEntity> findByFiltrePaginat(
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,
			Pageable pageable);

	@Query(	"from " +
			"    EntitatEntity ent " +
			"where " +
			"    :esNullFiltre = true " +
			" or lower(ent.codi) like lower('%'||:filtre||'%') " +
			" or lower(ent.nom) like lower('%'||:filtre||'%') " +
			" or lower(ent.cif) like lower('%'||:filtre||'%')) ")
	List<EntitatEntity> findByFiltrePaginat(
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,
			Sort sort);

	

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
