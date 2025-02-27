package es.caib.ripea.persistence.repository;

import es.caib.ripea.persistence.entity.MetaDadaEntity;
import es.caib.ripea.persistence.entity.MetaNodeEntity;
import es.caib.ripea.service.intf.dto.MetaDadaTipusEnumDto;
import es.caib.ripea.service.intf.dto.MultiplicitatEnumDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface MetaDadaRepository extends JpaRepository<MetaDadaEntity, Long> {

	MetaDadaEntity findByMetaNodeAndCodi(
			MetaNodeEntity metaNode,
			String codi);
		
	@Query(	"from " +
			"    MetaDadaEntity md " +
			"where " +
			"    md.metaNode = :metaNode " +
			"and (:esNullFiltre = true or lower(md.codi) like lower('%'||:filtre||'%') or lower(md.nom) like lower('%'||:filtre||'%')) order by md.ordre asc")
	Page<MetaDadaEntity> findByMetaNode(
			@Param("metaNode") MetaNodeEntity metaNode,
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,
			Pageable pageable);
	
	
	@Query(	"from " +
			"    MetaDadaEntity md " +
			"where " +
			"    md.metaNode = :metaNode " +
			"and (:esNullFiltre = true or lower(md.codi) like lower('%'||:filtre||'%') or lower(md.nom) like lower('%'||:filtre||'%')) order by md.ordre asc")
	List<MetaDadaEntity> findByMetaNode(
			@Param("metaNode") MetaNodeEntity metaNode,
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,
			Sort sort);
	
	
	int countByMetaNode(MetaNodeEntity metaNode);
	
	
	
	List<MetaDadaEntity> findByMetaNodeOrderByOrdreAsc(
			MetaNodeEntity metaNode);
	List<MetaDadaEntity> findByMetaNodeIdOrderByOrdreAsc(
			Long metaNodeId);
	List<MetaDadaEntity> findByMetaNodeIdInOrderByMetaNodeIdAscOrdreAsc(
			List<Long> metaNodeIds);
	List<MetaDadaEntity> findByMetaNodeIdAndTipusOrderByOrdreAsc(
			Long metaNodeId,
			MetaDadaTipusEnumDto tipus);
	List<MetaDadaEntity> findByMetaNodeIdAndTipusAndActivaTrueOrderByOrdreAsc(
			Long metaNodeId,
			MetaDadaTipusEnumDto tipus);
	List<MetaDadaEntity> findByMetaNodeAndActivaTrueOrderByOrdreAsc(
			MetaNodeEntity metaNode);
	List<MetaDadaEntity> findByMetaNodeAndActivaTrueAndMultiplicitatIn(
			MetaNodeEntity metaExpedient,
			MultiplicitatEnumDto[] multiplicitats);
	
	List<MetaDadaEntity> findByCodi(String codi);

}