/**
 * 
 */
package es.caib.ripea.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.ripea.core.entity.PortafirmesBlockEntity;
import es.caib.ripea.core.entity.PortafirmesBlockInfoEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus signatura-info.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface PortafirmesBlockInfoRepository extends JpaRepository<PortafirmesBlockInfoEntity, Long> {
	PortafirmesBlockInfoEntity findBySignerIdAndPortafirmesBlock(
			String signerId,
			PortafirmesBlockEntity portafirmesBlock);
	PortafirmesBlockInfoEntity findBySignerCodi(String signerCodi);
	
	@Modifying
 	@Query(value = "UPDATE IPA_PORTAFIRMES_BLOCK_INFO " +
 			"SET CREATEDBY_CODI = CASE WHEN CREATEDBY_CODI = :codiAntic THEN :codiNou ELSE CREATEDBY_CODI END, " +
 			"    LASTMODIFIEDBY_CODI = CASE WHEN LASTMODIFIEDBY_CODI = :codiAntic THEN :codiNou ELSE LASTMODIFIEDBY_CODI END " +
 			"WHERE CREATEDBY_CODI = :codiAntic OR LASTMODIFIEDBY_CODI = :codiAntic",
 			nativeQuery = true)
 	void updateUsuariAuditoria(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);
}
