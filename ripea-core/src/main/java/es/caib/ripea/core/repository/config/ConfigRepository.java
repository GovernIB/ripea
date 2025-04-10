package es.caib.ripea.core.repository.config;

import es.caib.ripea.core.entity.config.ConfigEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus notificacio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ConfigRepository extends JpaRepository<ConfigEntity, String> {

    ConfigEntity findByKey(String key);

    ConfigEntity findByKeyAndEntitatCodi(String key, String entitatCodi);

    List<ConfigEntity> findByEntitatCodiIsNull();
    
    List<ConfigEntity> findByEntitatCodiIsNullAndGroupCode(String groupCode);

    @Query("FROM ConfigEntity c WHERE c.key like concat('%', :key, '%') AND c.entitatCodi IS NOT NULL")
    List<ConfigEntity> findLikeKeyEntitatNotNull(@Param("key") String key);

    @Query("FROM ConfigEntity c WHERE c.key like (:prefix||'.'||c.entitatCodi||:suffix) AND c.entitatCodi IS NOT NULL AND c.configurable = true and c.organCodi IS NULL")
    List<ConfigEntity> findLikeKeyEntitatNotNullAndConfigurable(@Param("prefix") String prefix, @Param("suffix") String suffix);

	@Query("FROM ConfigEntity c WHERE c.entitatCodi IS NULL AND c.configurable = true")
	List<ConfigEntity> findByEntitatCodiIsNullAndConfigurableIsTrue();

    @Query("FROM ConfigEntity c WHERE c.configurable = true AND c.jbossProperty = true")
    List<ConfigEntity> findJBossConfigurables();
    
    
	@Query(	"from " +
			"    ConfigEntity c " +
			"where " +
			"c.organCodi is not null " +
			"and c.key like (:prefix||'.'||c.entitatCodi||'.'||c.organCodi||:suffix) ")
	Page<ConfigEntity> findConfOrgansByKey(
			@Param("prefix") String prefix, 
			@Param("suffix") String suffix,
			Pageable pageable);
	
	
    
	@Query(	"from " +
			"    ConfigEntity c " +
			"where " +
			"c.organCodi is not null " +
			"and c.key like (:prefix||'.'||c.entitatCodi||'.'||c.organCodi||:suffix) ")
	List<ConfigEntity> findConfOrgansByKey(
			@Param("prefix") String prefix, 
			@Param("suffix") String suffix);
    

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

    List<ConfigEntity> findByEntitatCodiAndConfigurableOrganActiuTrueAndOrganCodiIsNotNull(String entitatCodi);
    
	 @Modifying
     @Query(value = "UPDATE IPA_CONFIG SET LASTMODIFIEDBY_CODI = :codiNou WHERE LASTMODIFIEDBY_CODI = :codiAntic", nativeQuery = true)
	 public int updateUsuariAuditoria(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);
}
