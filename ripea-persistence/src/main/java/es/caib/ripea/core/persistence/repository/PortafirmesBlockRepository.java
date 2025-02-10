/**
 * 
 */
package es.caib.ripea.core.persistence.repository;

import es.caib.ripea.core.persistence.entity.DocumentEnviamentEntity;
import es.caib.ripea.core.persistence.entity.PortafirmesBlockEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus signatura-block.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface PortafirmesBlockRepository extends JpaRepository<PortafirmesBlockEntity, Long> {
	List<PortafirmesBlockEntity> findByEnviament(DocumentEnviamentEntity enviament);
}
