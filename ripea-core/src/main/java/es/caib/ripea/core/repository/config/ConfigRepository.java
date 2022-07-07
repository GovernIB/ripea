package es.caib.ripea.core.repository.config;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.core.entity.config.ConfigEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus notificacio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ConfigRepository extends JpaRepository<ConfigEntity, String> {
	
    ConfigEntity findByKeyAndEntitatCodi(String key, String entitatCodi);

    List<ConfigEntity> findByEntitatCodiIsNull();

    @Query("FROM ConfigEntity c WHERE c.key like concat('%', :key, '%') AND c.entitatCodi IS NOT NULL")
    List<ConfigEntity> findLikeKeyEntitatNotNull(@Param("key") String key);

	@Query("FROM ConfigEntity c WHERE c.entitatCodi IS NULL AND c.configurable = true")
	List<ConfigEntity> findByEntitatCodiIsNullAndConfigurableIsTrue();

    @Transactional
    @Modifying
    @Query("DELETE FROM ConfigEntity c WHERE c.entitatCodi = :entitatCodi")
    int deleteByEntitatCodi(@Param("entitatCodi") String entitatCodi);

    @Query( "from ConfigEntity c where c.entitatCodi = :entitatCodi order by c.position asc")
	public List<ConfigEntity> findAllPerEntitat(@Param("entitatCodi") String entitatCodi);
	
	
	@Query( "from ConfigEntity c where c.key = :key")
	public ConfigEntity findPerKey(@Param("key") String key);
	
	
	@Query( "from ConfigEntity c where c.configurable = true" )
	public List<ConfigEntity> findConfigurables();
	
	
	@Query( "from ConfigEntity c where c.configurable = true and c.entitatCodi = null" )
	public List<ConfigEntity> findConfigurablesAmbEntitatNull();
}