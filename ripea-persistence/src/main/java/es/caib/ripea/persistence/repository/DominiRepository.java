package es.caib.ripea.persistence.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import es.caib.ripea.persistence.entity.DominiEntity;
import es.caib.ripea.persistence.entity.EntitatEntity;

@Component
public interface DominiRepository extends JpaRepository<DominiEntity, Long> {

	List<DominiEntity> findByEntitatOrderByNomAsc(EntitatEntity entitat);
	
	@Query(	"from " +
			"    DominiEntity dom " +
			"where " +
			"    dom.entitat = :entitat " +
			"and (:esNullFiltre = true or lower(dom.codi) like lower('%'||:filtre||'%') or lower(dom.nom) like lower('%'||:filtre||'%') or lower(dom.descripcio) like lower('%'||:filtre||'%')) ")
	Page<DominiEntity> findByEntitat(
			@Param("entitat") EntitatEntity entitat, 
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,	
			Pageable pageable);
	DominiEntity findByCodiAndEntitat(String codi, EntitatEntity entitat);
	
	
	List<DominiEntity> findByEntitatAndCodiInOrderByIdAsc(
			EntitatEntity entitat,
			List<String> dominiCodis);
	
	
	List<DominiEntity> findByEntitatAndCodi(
			EntitatEntity entitat,
			String dominiCodi);
	
	@Modifying
 	@Query(value = "UPDATE IPA_DOMINI " +
 			"SET CREATEDBY_CODI = CASE WHEN CREATEDBY_CODI = :codiAntic THEN :codiNou ELSE CREATEDBY_CODI END, " +
 			"    LASTMODIFIEDBY_CODI = CASE WHEN LASTMODIFIEDBY_CODI = :codiAntic THEN :codiNou ELSE LASTMODIFIEDBY_CODI END " +
 			"WHERE CREATEDBY_CODI = :codiAntic OR LASTMODIFIEDBY_CODI = :codiAntic",
 			nativeQuery = true)
	public int updateUsuariAuditoria(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);
}