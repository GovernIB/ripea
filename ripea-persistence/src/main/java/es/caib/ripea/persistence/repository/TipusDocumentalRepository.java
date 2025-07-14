package es.caib.ripea.persistence.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.TipusDocumentalEntity;

@Component
public interface TipusDocumentalRepository extends JpaRepository<TipusDocumentalEntity, Long> {

	List<TipusDocumentalEntity> findByEntitatOrderByNomEspanyolAsc(EntitatEntity entitat);
	
	List<TipusDocumentalEntity> findByCodi(String codi);
	
	@Query(	"from " +
			"    TipusDocumentalEntity tipusDocumental " +
			"where " +
			"    tipusDocumental.entitat = :entitat " +
			"and (:esNullFiltre = true or lower(tipusDocumental.codi) like lower('%'||:filtre||'%') or lower(tipusDocumental.nomEspanyol) like lower('%'||:filtre||'%') or lower(tipusDocumental.nomCatala) like lower('%'||:filtre||'%')) ")
	Page<TipusDocumentalEntity> findByEntitat(
			@Param("entitat") EntitatEntity entitat, 
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,	
			Pageable pageable);
	
	TipusDocumentalEntity findByCodiAndEntitat(String codi, EntitatEntity entitat);
	
	@Modifying
 	@Query(value = "UPDATE IPA_TIPUS_DOCUMENTAL " +
 			"SET CREATEDBY_CODI = CASE WHEN CREATEDBY_CODI = :codiAntic THEN :codiNou ELSE CREATEDBY_CODI END, " +
 			"    LASTMODIFIEDBY_CODI = CASE WHEN LASTMODIFIEDBY_CODI = :codiAntic THEN :codiNou ELSE LASTMODIFIEDBY_CODI END " +
 			"WHERE CREATEDBY_CODI = :codiAntic OR LASTMODIFIEDBY_CODI = :codiAntic",
 			nativeQuery = true)
	public int updateUsuariAuditoria(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);
}