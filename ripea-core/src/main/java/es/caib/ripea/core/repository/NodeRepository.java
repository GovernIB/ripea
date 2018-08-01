/**
 * 
 */
package es.caib.ripea.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.ripea.core.entity.ContingutEntity;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.MetaNodeEntity;
import es.caib.ripea.core.entity.NodeEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus node.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface NodeRepository extends JpaRepository<NodeEntity, Long> {

	@Query(	"select " +
			"    pare.id, " +
			"    count(*) " +
			"from " +
			"    NodeEntity " +
			"where " +
			"    entitat = :entitat " +
			"and pare in (:pares) " +
			"and esborrat = 0 " +
			"group by " +
			"    pare")
	List<Object[]> countByPares(
			@Param("entitat") EntitatEntity entitat,
			@Param("pares") List<? extends ContingutEntity> pares);

	@Query(	"select " +
			"    pare.id, " +
			"    count(*) " +
			"from " +
			"    NodeEntity " +
			"where " +
			"    entitat = :entitat " +
			"and pare in (:pares) " +
			"and (metaNode is null or metaNode in (:metaNodesPermesos)) " +
			"and esborrat = 0 " +
			"group by " +
			"    pare")
	List<Object[]> countAmbPermisReadByPares(
			@Param("entitat") EntitatEntity entitat,
			@Param("pares") List<? extends ContingutEntity> pares,
			@Param("metaNodesPermesos") List<MetaNodeEntity> metaNodesPermesos);

}
