/**
 * 
 */
package es.caib.ripea.core.persistence.repository;

import es.caib.ripea.core.persistence.entity.DispositiuEnviamentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus document-portafirmes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DispositiuEnviamentRepository extends JpaRepository<DispositiuEnviamentEntity, Long> {

}
