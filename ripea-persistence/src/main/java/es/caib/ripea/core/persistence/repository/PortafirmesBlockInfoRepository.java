/**
 * 
 */
package es.caib.ripea.core.persistence.repository;

import es.caib.ripea.core.persistence.entity.PortafirmesBlockEntity;
import es.caib.ripea.core.persistence.entity.PortafirmesBlockInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

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
