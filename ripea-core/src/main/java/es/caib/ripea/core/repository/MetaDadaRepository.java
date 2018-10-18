/**
 * 
 */
package es.caib.ripea.core.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

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
	Page<MetaDadaEntity> findByMetaNode(
			MetaNodeEntity metaNode,
			Pageable pageable);
	List<MetaDadaEntity> findByMetaNode(
			MetaNodeEntity metaNode,
			Sort sort);
	List<MetaDadaEntity> findByMetaNodeOrderByOrdreAsc(
			MetaNodeEntity metaNode);
	List<MetaDadaEntity> findByMetaNodeIdOrderByOrdreAsc(
			Long metaNodeId);
	List<MetaDadaEntity> findByMetaNodeIdInOrderByMetaNodeIdAscOrdreAsc(
			List<Long> metaNodeIds);
	List<MetaDadaEntity> findByMetaNodeAndActivaTrue(
			MetaNodeEntity metaNode);

}