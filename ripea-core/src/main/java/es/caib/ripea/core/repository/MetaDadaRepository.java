/**
 * 
 */
package es.caib.ripea.core.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.ripea.core.api.dto.MetaDadaTipusEnumDto;
import es.caib.ripea.core.api.dto.MultiplicitatEnumDto;
import es.caib.ripea.core.entity.MetaDadaEntity;
import es.caib.ripea.core.entity.MetaNodeEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus meta-dada.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
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

	@Modifying
 	@Query(value = "UPDATE IPA_METADADA " +
 			"SET CREATEDBY_CODI = CASE WHEN CREATEDBY_CODI = :codiAntic THEN :codiNou ELSE CREATEDBY_CODI END, " +
 			"    LASTMODIFIEDBY_CODI = CASE WHEN LASTMODIFIEDBY_CODI = :codiAntic THEN :codiNou ELSE LASTMODIFIEDBY_CODI END " +
 			"WHERE CREATEDBY_CODI = :codiAntic OR LASTMODIFIEDBY_CODI = :codiAntic",
 			nativeQuery = true)
	public int updateUsuariAuditoria(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);
}