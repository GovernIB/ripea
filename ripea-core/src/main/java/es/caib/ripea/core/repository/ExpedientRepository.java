/**
 * 
 */
package es.caib.ripea.core.repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.ripea.core.api.dto.ExpedientEstatEnumDto;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.MetaNodeEntity;
import es.caib.ripea.core.entity.UsuariEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientRepository extends JpaRepository<ExpedientEntity, Long> {

	ExpedientEntity findByEntitatAndMetaNodeAndAnyAndSequencia(
			EntitatEntity entitat,
			MetaNodeEntity metaNode,
			int any,
			long sequencia);

	@Query(	"from" +
			"    ExpedientEntity e " +
			"where " +
			"    e.esborrat = 0 " +
			"and e.entitat = :entitat " +
			"and (e.metaNode is null or e.metaNode in (:metaNodesPermesos)) " +
			"and (:esNullNumero = true or lower(e.codi||'/'||e.sequencia||'/'||e.any) like lower('%'||:numero||'%')) " +
			"and (:esNullNom = true or lower(e.nom) like lower('%'||:nom||'%')) " +
			"and (:esNullMetaNode = true or e.metaNode = :metaNode) " +
			"and (:esNullCreacioInici = true or e.createdDate >= :creacioInici) " +
			"and (:esNullCreacioFi = true or e.createdDate <= :creacioFi) " +
			"and (:esNullTancatInici = true or e.createdDate >= :tancatInici) " +
			"and (:esNullTancatFi = true or e.createdDate <= :tancatFi) " +
			"and (:esNullEstat = true or e.estat = :estat) " +
			"and (:esNullAgafatPer = true or e.agafatPer = :agafatPer) " +
			"and (:esNullSearch = true or lower(e.nom) like lower('%'||:search||'%') or lower(e.codi||'/'||e.sequencia||'/'||e.any) like lower('%'||:search||'%'))" +
			"and (:esNullTipusId = true or e.metaNode.id = :tipusId) ")
	Page<ExpedientEntity> findByEntitatAndFiltre(
			@Param("entitat") EntitatEntity entitat,
			@Param("metaNodesPermesos") List<? extends MetaNodeEntity> metaNodesPermesos,
			@Param("esNullMetaNode") boolean esNullMetaNode,
			@Param("metaNode") MetaNodeEntity metaNode,			
			@Param("esNullNumero") boolean esNullNumero,
			@Param("numero") String numero,
			@Param("esNullNom") boolean esNullNom,
			@Param("nom") String nom,
			@Param("esNullCreacioInici") boolean esNullCreacioInici,
			@Param("creacioInici") Date creacioInici,
			@Param("esNullCreacioFi") boolean esNullCreacioFi,
			@Param("creacioFi") Date creacioFi,
			@Param("esNullTancatInici") boolean esNullTancatInici,
			@Param("tancatInici") Date tancatInici,
			@Param("esNullTancatFi") boolean esNullTancatFi,
			@Param("tancatFi") Date tancatFi,
			@Param("esNullEstat") boolean esNullEstat,
			@Param("estat") ExpedientEstatEnumDto estat,
			@Param("esNullAgafatPer") boolean esNullAgafatPer,
			@Param("agafatPer") UsuariEntity agafatPer,
			@Param("esNullSearch") boolean esNullSearch,
			@Param("search") String search,
			@Param("esNullTipusId") boolean esNullTipusId,
			@Param("tipusId") Long tipusId,
			Pageable pageable);

	@Query(	"select" +
			"    e.id " +
			"from" +
			"    ExpedientEntity e " +
			"where " +
			"    e.esborrat = 0 " +
			"and e.entitat = :entitat " +
			"and (e.metaNode is null or e.metaNode in (:metaNodesPermesos)) " +
			"and (:esNullNumero = true or lower(e.codi||'/'||e.sequencia||'/'||e.any) like lower('%'||:numero||'%')) " +
			"and (:esNullNom = true or lower(e.nom) like lower('%'||:nom||'%')) " +
			"and (:esNullMetaNode = true or e.metaNode = :metaNode) " +
			"and (:esNullCreacioInici = true or e.createdDate >= :creacioInici) " +
			"and (:esNullCreacioFi = true or e.createdDate <= :creacioFi) " +
			"and (:esNullTancatInici = true or e.createdDate >= :tancatInici) " +
			"and (:esNullTancatFi = true or e.createdDate <= :tancatFi) " +
			"and (:esNullEstat = true or e.estat = :estat)")
	List<Long> findIdByEntitatAndFiltre(
			@Param("entitat") EntitatEntity entitat,
			@Param("metaNodesPermesos") List<? extends MetaNodeEntity> metaNodesPermesos,
			@Param("esNullMetaNode") boolean esNullMetaNode,
			@Param("metaNode") MetaNodeEntity metaNode,			
			@Param("esNullNumero") boolean esNullNumero,
			@Param("numero") String numero,
			@Param("esNullNom") boolean esNullNom,
			@Param("nom") String nom,
			@Param("esNullCreacioInici") boolean esNullCreacioInici,
			@Param("creacioInici") Date creacioInici,
			@Param("esNullCreacioFi") boolean esNullCreacioFi,
			@Param("creacioFi") Date creacioFi,
			@Param("esNullTancatInici") boolean esNullTancatInici,
			@Param("tancatInici") Date tancatInici,
			@Param("esNullTancatFi") boolean esNullTancatFi,
			@Param("tancatFi") Date tancatFi,
			@Param("esNullEstat") boolean esNullEstat,
			@Param("estat") ExpedientEstatEnumDto estat);

	List<ExpedientEntity> findByEntitatAndAndMetaNodeAndIdInOrderByIdAsc(
			EntitatEntity entitat,
			MetaNodeEntity metaNode,
			Collection<Long> id);

	@Query(	"select" +
			"    e " +
			"from" +
			"    ExpedientEntity e " +
			"where " +
			"    e.entitat = :entitat " +
			"and (e.metaNode is null or e.metaNode in (:metaNodesPermesos)) " +
			"and (:esNullMetaNode = true or e.metaNode = :metaNode)")
	List<ExpedientEntity> findByEntitatAndMetaExpedientOrderByNomAsc(
			@Param("entitat") EntitatEntity entitat,
			@Param("metaNodesPermesos") List<? extends MetaNodeEntity> metaNodesPermesos,
			@Param("esNullMetaNode") boolean esNullMetaNode,
			@Param("metaNode") MetaNodeEntity metaNode);
}
