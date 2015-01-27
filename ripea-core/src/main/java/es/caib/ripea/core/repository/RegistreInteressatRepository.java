/**
 * 
 */
package es.caib.ripea.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.ripea.core.entity.RegistreInteressatEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus registre interessat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface RegistreInteressatRepository extends JpaRepository<RegistreInteressatEntity, Long> {

}
