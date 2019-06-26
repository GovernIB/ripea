/**
 * 
 */
package es.caib.ripea.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.RegistreInteressatEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus interessat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface RegistreInteressatRepository extends JpaRepository<RegistreInteressatEntity, Long> {


	RegistreInteressatEntity findByRepresentant(RegistreInteressatEntity representant);
}
