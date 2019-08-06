/**
 * 
 */
package es.caib.ripea.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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

	@Query(   "select "
			+ "    u "
			+ "from "
			+ "    UsuariEntity u "
			+ "where "
			+ "    lower(u.nom) like concat('%', lower(?1), '%') "
			+ "order by "
			+ "    u.nom desc")
	public List<UsuariEntity> findByText(String text);

}
