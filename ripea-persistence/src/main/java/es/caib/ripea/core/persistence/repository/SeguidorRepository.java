/**
 * 
 */
package es.caib.ripea.core.persistence.repository;

import es.caib.ripea.core.persistence.entity.InteressatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus interessat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface SeguidorRepository extends JpaRepository<InteressatEntity, Long> {

}
