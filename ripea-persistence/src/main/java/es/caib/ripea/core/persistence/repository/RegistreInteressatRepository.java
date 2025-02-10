/**
 * 
 */
package es.caib.ripea.core.persistence.repository;

import es.caib.ripea.core.persistence.entity.RegistreInteressatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus interessat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface RegistreInteressatRepository extends JpaRepository<RegistreInteressatEntity, Long> {

	RegistreInteressatEntity findByRepresentant(RegistreInteressatEntity representant);

}
