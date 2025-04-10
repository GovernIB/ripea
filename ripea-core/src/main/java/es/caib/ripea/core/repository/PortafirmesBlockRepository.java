/**
 * 
 */
package es.caib.ripea.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.ripea.core.entity.DocumentEnviamentEntity;
import es.caib.ripea.core.entity.PortafirmesBlockEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus signatura-block.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface PortafirmesBlockRepository extends JpaRepository<PortafirmesBlockEntity, Long> {
	List<PortafirmesBlockEntity> findByEnviament(DocumentEnviamentEntity enviament);
	
	@Modifying
 	@Query(value = "UPDATE IPA_PORTAFIRMES_BLOCK " +
 			"SET CREATEDBY_CODI = CASE WHEN CREATEDBY_CODI = :codiAntic THEN :codiNou ELSE CREATEDBY_CODI END, " +
 			"    LASTMODIFIEDBY_CODI = CASE WHEN LASTMODIFIEDBY_CODI = :codiAntic THEN :codiNou ELSE LASTMODIFIEDBY_CODI END " +
 			"WHERE CREATEDBY_CODI = :codiAntic OR LASTMODIFIEDBY_CODI = :codiAntic",
 			nativeQuery = true)
	public int updateUsuariAuditoria(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);
}
