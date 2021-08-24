package es.caib.ripea.core.repository.config;

import es.caib.ripea.core.entity.config.ConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus notificacio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ConfigRepository extends JpaRepository<ConfigEntity, String> {

}