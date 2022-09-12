/**
 * 
 */
package es.caib.ripea.core.repository;

import es.caib.ripea.core.entity.AclClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus ACL-SID.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AclClassRepository extends JpaRepository<AclClassEntity, Long> {

	AclClassEntity findByClassname(String classname);

}