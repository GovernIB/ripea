package es.caib.ripea.persistence.repository;

import es.caib.ripea.persistence.entity.ContingutEntity;
import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.MetaNodeEntity;
import es.caib.ripea.persistence.entity.NodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
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

	@Modifying
	@Query(value = "delete from ipa_node " +
			" where id not in (select id from ipa_expedient) " +
			"   and id not in (select id from ipa_document)", nativeQuery = true)
	int deleteNodesOrfes();
}