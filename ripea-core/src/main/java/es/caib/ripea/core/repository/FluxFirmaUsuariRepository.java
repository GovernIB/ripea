/**
 * 
 */
package es.caib.ripea.core.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.FluxFirmaUsuariEntity;
import es.caib.ripea.core.entity.UsuariEntity;

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
	
	 @Modifying
     @Query(value = "UPDATE IPA_FLUX_FIRMA_USUARI SET USUARI_CODI = :codiNou WHERE USUARI_CODI = :codiAntic", nativeQuery = true)
	 public int updateUsuariCodi(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);
	 
	@Modifying
 	@Query(value = "UPDATE IPA_FLUX_FIRMA_USUARI " +
 			"SET CREATEDBY_CODI = CASE WHEN CREATEDBY_CODI = :codiAntic THEN :codiNou ELSE CREATEDBY_CODI END, " +
 			"    LASTMODIFIEDBY_CODI = CASE WHEN LASTMODIFIEDBY_CODI = :codiAntic THEN :codiNou ELSE LASTMODIFIEDBY_CODI END " +
 			"WHERE CREATEDBY_CODI = :codiAntic OR LASTMODIFIEDBY_CODI = :codiAntic",
 			nativeQuery = true)
	public int updateUsuariAuditoria(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);
}