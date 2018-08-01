/**
 * 
 */
package es.caib.ripea.core.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.ripea.core.entity.DadaEntity;
import es.caib.ripea.core.entity.MetaDadaEntity;
import es.caib.ripea.core.entity.NodeEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus dada.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DadaRepository extends JpaRepository<DadaEntity, Long> {

	List<DadaEntity> findByNode(NodeEntity node);
	List<DadaEntity> findByNodeAndMetaDadaOrderByOrdreAsc(NodeEntity node, MetaDadaEntity metaDada);
	List<DadaEntity> findByNodeIdInOrderByNodeIdAscMetaDadaCodiAsc(Collection<Long> nodeIds);
	@Query(	"select" +
			"    distinct d.metaDada " +
			"from" +
			"    DadaEntity d " +
			"where " +
			"    d.node.id in (:nodeIds) " +
			"order by " +
			"    d.metaDada.codi asc ")
	List<MetaDadaEntity> findDistinctMetaDadaByNodeIdInOrderByMetaDadaCodiAsc(
			@Param("nodeIds") Collection<Long> nodeIds); 

}
