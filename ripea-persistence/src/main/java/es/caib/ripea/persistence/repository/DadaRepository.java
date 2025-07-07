package es.caib.ripea.persistence.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import es.caib.ripea.persistence.entity.DadaEntity;
import es.caib.ripea.persistence.entity.MetaDadaEntity;
import es.caib.ripea.persistence.entity.NodeEntity;
import es.caib.ripea.service.intf.dto.MetaDadaTipusEnumDto;

@Component
public interface DadaRepository extends JpaRepository<DadaEntity, Long> {

	int countByNodeId(Long nodeId);
	
	List<DadaEntity> findByNode(NodeEntity node);
	
	DadaEntity findByNodeAndMetaDadaAndOrdre(NodeEntity node, MetaDadaEntity metaDada, int ordre);
	
	List<DadaEntity> findByNodeAndMetaDadaOrderByOrdreAsc(NodeEntity node, MetaDadaEntity metaDada);
	
	List<DadaEntity> findByMetaDadaTipus(MetaDadaTipusEnumDto tipus);
	
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

	@Modifying
	@Query(value = "delete from ipa_dada " +
			" where node_id in (" +
			"	select n.id from ipa_node n " +
			"	 where n.id not in (select id from ipa_expedient) " +
			"	   and n.id not in (select id from ipa_document))", nativeQuery = true)
	int deleteDadesFromNodesOrfes();
	
	@Modifying
 	@Query(value = "UPDATE IPA_DADA " +
 			"SET CREATEDBY_CODI = CASE WHEN CREATEDBY_CODI = :codiAntic THEN :codiNou ELSE CREATEDBY_CODI END, " +
 			"    LASTMODIFIEDBY_CODI = CASE WHEN LASTMODIFIEDBY_CODI = :codiAntic THEN :codiNou ELSE LASTMODIFIEDBY_CODI END " +
 			"WHERE CREATEDBY_CODI = :codiAntic OR LASTMODIFIEDBY_CODI = :codiAntic",
 			nativeQuery = true)
	public int updateUsuariAuditoria(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);
}
