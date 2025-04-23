/**
 * 
 */
package es.caib.ripea.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.UsuariEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface UsuariRepository extends JpaRepository<UsuariEntity, String> {
	
	public UsuariEntity findByCodi(String codi);

	public UsuariEntity findByNif(String nif);

	public List<UsuariEntity> findByProcediment(MetaExpedientEntity procediment);

	@Query(   "select "
			+ "    u "
			+ "from "
			+ "    UsuariEntity u "
			+ "where "
			+ "    lower(u.nom) like concat('%', lower(?1), '%') "
			+ "order by "
			+ "    u.nom desc")
	public List<UsuariEntity> findByText(String text);

	@Modifying
 	@Query(value = "UPDATE IPA_ACL_SID SET SID = :codiNou WHERE SID = :codiAntic AND PRINCIPAL = 1"
 			+ " AND NOT EXISTS (SELECT 1 FROM IPA_ACL_SID WHERE SID = :codiNou AND PRINCIPAL = 1)", nativeQuery = true)
	public int updateUsuariPermis(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);
	
	 @Modifying
     @Query(value = "UPDATE IPA_USUARI_VIAFIRMA_RIPEA SET RIPEA_USER_CODI = :codiNou WHERE RIPEA_USER_CODI = :codiAntic", nativeQuery = true)
	 public int updateUsuariViaFirma(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);
}
