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
import es.caib.ripea.core.entity.URLInstruccioEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus url instrucció.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
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
	
	@Modifying
 	@Query(value = "UPDATE IPA_URL_INSTRUCCIO " +
 			"SET CREATEDBY_CODI = CASE WHEN CREATEDBY_CODI = :codiAntic THEN :codiNou ELSE CREATEDBY_CODI END, " +
 			"    LASTMODIFIEDBY_CODI = CASE WHEN LASTMODIFIEDBY_CODI = :codiAntic THEN :codiNou ELSE LASTMODIFIEDBY_CODI END " +
 			"WHERE CREATEDBY_CODI = :codiAntic OR LASTMODIFIEDBY_CODI = :codiAntic",
 			nativeQuery = true)
	public int updateUsuariAuditoria(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);
}