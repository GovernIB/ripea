/**
 * 
 */
package es.caib.ripea.core.repository;

import es.caib.ripea.core.entity.DadaEntity;
import es.caib.ripea.core.entity.MetaDadaEntity;
import es.caib.ripea.core.entity.NodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

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
			"    distinct md " +
			"from" +
			"    DadaEntity d inner join d.metaDada md " +
			"where " +
			"    d.node.id in (:nodeIds) " +
			"order by " +
			"    md.codi asc ")
	List<MetaDadaEntity> findDistinctMetaDadaByNodeIdInOrderByMetaDadaCodiAsc(
			@Param("nodeIds") Collection<Long> nodeIds);



	// Mètodes per evitar errors al tenir continguts orfes en base de dades
	// ////////////////////////////////////////////////////////////////////

	@Modifying
	@Query(value = "delete from ipa_dada " +
			" where node_id in (" +
			"	select n.id from ipa_node n " +
			"	 where n.id not in (select id from ipa_expedient) " +
			"	   and n.id not in (select id from ipa_document))", nativeQuery = true)
	int deleteDadesFromNodesOrfes();
}
