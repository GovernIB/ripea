/**
 * 
 */
package es.caib.ripea.core.persistence.repository;

import es.caib.ripea.core.persistence.entity.EntitatEntity;
import es.caib.ripea.core.persistence.entity.FluxFirmaUsuariEntity;
import es.caib.ripea.core.persistence.entity.UsuariEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades pel manteniment de fluxos d'un usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface FluxFirmaUsuariRepository extends JpaRepository<FluxFirmaUsuariEntity, Long> {

	List<FluxFirmaUsuariEntity> findByEntitatAndUsuariOrderByNomAsc(EntitatEntity entitat, UsuariEntity usuari);
	
	@Query(	"from " +
			"    FluxFirmaUsuariEntity fx " +
			"where " +
			"    fx.entitat = :entitat " +
			"and fx.usuari = :usuari " +
			"and (:esNullNom = true or lower(fx.nom) like lower('%'||:nom||'%')) " + 
			"and (:esNullDescripcio = true or lower(fx.descripcio) like lower('%'||:descripcio||'%')) ")
	Page<FluxFirmaUsuariEntity> findByEntitatAndUsuari(
			@Param("entitat") EntitatEntity entitat, 
			@Param("usuari") UsuariEntity usuari, 
			@Param("esNullNom") boolean esNullNom,
			@Param("nom") String nom,
			@Param("esNullDescripcio") boolean esNullDescripcio,
			@Param("descripcio") String descripcio,
			Pageable pageable);
	
	List<FluxFirmaUsuariEntity> findByEntitatAndUsuari(EntitatEntity entitat, UsuariEntity usuari);

	List<FluxFirmaUsuariEntity> findByEntitat(EntitatEntity entitat);

	FluxFirmaUsuariEntity findByUsuariAndPortafirmesFluxId(UsuariEntity usuari, String plantillaFluxId);
	
//	List<URLInstruccioEntity> findByEntitatAndCodiInOrderByIdAsc(
//			EntitatEntity entitat,
//			List<String> dominiCodis);
//	
//	
//	List<URLInstruccioEntity> findByEntitatAndCodi(
//			EntitatEntity entitat,
//			String dominiCodi);
	
	

}
