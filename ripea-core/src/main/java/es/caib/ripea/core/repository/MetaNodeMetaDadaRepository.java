/**
 * 
 */
package es.caib.ripea.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import es.caib.ripea.core.entity.MetaDadaEntity;
import es.caib.ripea.core.entity.MetaNodeEntity;
import es.caib.ripea.core.entity.MetaNodeMetaDadaEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus meta-node-meta-dada.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface MetaNodeMetaDadaRepository extends JpaRepository<MetaNodeMetaDadaEntity, Long> {

	@Query("from MetaNodeMetaDadaEntity where metaNode.id = ?1 order by ordre asc")
	List<MetaNodeMetaDadaEntity> findByMetaNodeId(Long metaNodeId);

	@Query("from MetaNodeMetaDadaEntity where metaNode.id in ?1 order by metaNode.id asc, ordre asc")
	List<MetaNodeMetaDadaEntity> findByMetaNodeIdIn(List<Long> metaNodeIds);

	@Query("from MetaNodeMetaDadaEntity where metaNode.id = ?1 and metaDada = ?2")
	MetaNodeMetaDadaEntity findByMetaNodeIdAndMetaDada(Long metaNodeId, MetaDadaEntity metaDada);

	@Query("from MetaNodeMetaDadaEntity where metaNode = ?1 and metaDada.activa = true order by ordre asc and id asc")
	List<MetaNodeMetaDadaEntity> findByMetaNodeAndActivaTrue(MetaNodeEntity metaNode);

}
