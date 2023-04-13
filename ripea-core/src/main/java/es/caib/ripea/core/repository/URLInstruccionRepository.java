/**
 * 
 */
package es.caib.ripea.core.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.URLInstruccionEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus url instrucció.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface URLInstruccionRepository extends JpaRepository<URLInstruccionEntity, Long> {

	List<URLInstruccionEntity> findByEntitatOrderByNomAsc(EntitatEntity entitat);
	
	@Query(	"from " +
			"    URLInstruccionEntity url " +
			"where " +
			"    url.entitat = :entitat " +
			"and (:esNullCodi = true or lower(url.codi) like lower('%'||:codi||'%')) " + 
			"and (:esNullNom = true or lower(url.nom) like lower('%'||:nom||'%')) " + 
			"and (:esNullDescripcio = true or lower(url.descripcio) like lower('%'||:descripcio||'%')) ")
	Page<URLInstruccionEntity> findByEntitat(
			@Param("entitat") EntitatEntity entitat, 
			@Param("esNullCodi") boolean esNullCodi,
			@Param("codi") String codi,
			@Param("esNullNom") boolean esNullNom,
			@Param("nom") String nom,
			@Param("esNullDescripcio") boolean esNullDescripcio,
			@Param("descripcio") String descripcio,
			Pageable pageable);
	
	URLInstruccionEntity findByCodiAndEntitat(String codi, EntitatEntity entitat);
	
	List<URLInstruccionEntity> findByEntitat(EntitatEntity entitat);
	
	List<URLInstruccionEntity> findByEntitatAndCodiInOrderByIdAsc(
			EntitatEntity entitat,
			List<String> dominiCodis);
	
	
	List<URLInstruccionEntity> findByEntitatAndCodi(
			EntitatEntity entitat,
			String dominiCodi);
	
	

}
