/**
 * 
 */
package es.caib.ripea.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

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
}
