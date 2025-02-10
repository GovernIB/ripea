/**
 * 
 */
package es.caib.ripea.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.ripea.core.persistence.PortafirmesBlockEntity;
import es.caib.ripea.core.persistence.PortafirmesBlockInfoEntity;

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
}
