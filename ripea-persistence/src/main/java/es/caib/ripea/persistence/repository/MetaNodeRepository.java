/**
 * 
 */
package es.caib.ripea.persistence.repository;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.MetaNodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus meta-node.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface MetaNodeRepository extends JpaRepository<MetaNodeEntity, Long> {

	List<MetaNodeEntity> findByEntitat(EntitatEntity entitat);

}
