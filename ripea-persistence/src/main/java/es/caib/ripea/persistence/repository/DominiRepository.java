package es.caib.ripea.persistence.repository;

import es.caib.ripea.persistence.entity.DominiEntity;
import es.caib.ripea.persistence.entity.EntitatEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.List;

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
}