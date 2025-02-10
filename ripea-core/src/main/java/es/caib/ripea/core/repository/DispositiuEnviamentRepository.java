/**
 * 
 */
package es.caib.ripea.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.ripea.core.persistence.DispositiuEnviamentEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus document-portafirmes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DispositiuEnviamentRepository extends JpaRepository<DispositiuEnviamentEntity, Long> {

}
