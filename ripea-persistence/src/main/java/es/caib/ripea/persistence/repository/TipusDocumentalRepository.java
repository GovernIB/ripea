package es.caib.ripea.persistence.repository;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.TipusDocumentalEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.List;

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
}