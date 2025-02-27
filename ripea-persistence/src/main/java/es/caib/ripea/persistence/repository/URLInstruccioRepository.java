package es.caib.ripea.persistence.repository;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.URLInstruccioEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface URLInstruccioRepository extends JpaRepository<URLInstruccioEntity, Long> {

	List<URLInstruccioEntity> findByEntitatOrderByNomAsc(EntitatEntity entitat);
	
	@Query(	"from " +
			"    URLInstruccioEntity url " +
			"where " +
			"    url.entitat = :entitat " +
			"and (:esNullCodi = true or lower(url.codi) like lower('%'||:codi||'%')) " + 
			"and (:esNullNom = true or lower(url.nom) like lower('%'||:nom||'%')) " + 
			"and (:esNullDescripcio = true or lower(url.descripcio) like lower('%'||:descripcio||'%')) ")
	Page<URLInstruccioEntity> findByEntitat(
			@Param("entitat") EntitatEntity entitat, 
			@Param("esNullCodi") boolean esNullCodi,
			@Param("codi") String codi,
			@Param("esNullNom") boolean esNullNom,
			@Param("nom") String nom,
			@Param("esNullDescripcio") boolean esNullDescripcio,
			@Param("descripcio") String descripcio,
			Pageable pageable);
	
	URLInstruccioEntity findByCodiAndEntitat(String codi, EntitatEntity entitat);
	
	List<URLInstruccioEntity> findByEntitat(EntitatEntity entitat);
	
	List<URLInstruccioEntity> findByEntitatAndCodiInOrderByIdAsc(
			EntitatEntity entitat,
			List<String> dominiCodis);
	
	
	List<URLInstruccioEntity> findByEntitatAndCodi(
			EntitatEntity entitat,
			String dominiCodi);
}