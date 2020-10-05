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
import es.caib.ripea.core.entity.ContingutEntity;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.ExpedientEstatEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.MetaNodeEntity;
import es.caib.ripea.core.entity.UsuariEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientRepository extends JpaRepository<ExpedientEntity, Long> {

	ExpedientEntity findByMetaExpedientAndPareAndNomAndEsborrat(
			MetaExpedientEntity metaExpedient,
			ContingutEntity pare,
			String nom,
			int esborrat);
	
	List<ExpedientEntity> findByMetaExpedient(
			MetaExpedientEntity metaExpedient);

	ExpedientEntity findByEntitatAndMetaNodeAndAnyAndSequencia(
			EntitatEntity entitat,
			MetaNodeEntity metaNode,
			int any,
			long sequencia);
	
	@Query(	"from" +
			"    ExpedientEntity e "
			+ "where "
			+ "	 e.entitat = :entitat " 
			+ "	and e.metaNode = :metaNode " +
			"and e.codi||'/'||e.sequencia||'/'||e.any = :numero")
	ExpedientEntity findByEntitatAndMetaNodeAndNumero(
			@Param("entitat") EntitatEntity entitat,
			@Param("metaNode") MetaNodeEntity metaNode,
			@Param("numero") String numero);
	
	
	@Query(	"from" +
			"    ExpedientEntity e "
			+ "where "
			+ "	 e.entitat = :entitat " +
			"and e.codi||'/'||e.sequencia||'/'||e.any = :numero")
	ExpedientEntity findByEntitatAndNumero(
			@Param("entitat") EntitatEntity entitat,
			@Param("numero") String numero);
	
	
	@Query(	"select "
			+ "e.relacionatsAmb from" +
			"    ExpedientEntity e "
			+ "where "
			+ " e = :expedient")
	List<ExpedientEntity> findExpedientsRelacionats(
			@Param("expedient") ExpedientEntity expedient);
	
	
	
	
	

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
			"and (:esNullEstatEnum = true or e.estat = :estatEnum) " +
			"and (:esNullEstat = true or e.expedientEstat = :estat) " +
			"and (:esNullAgafatPer = true or e.agafatPer = :agafatPer) " +
			"and (:esNullSearch = true or lower(e.nom) like lower('%'||:search||'%') or lower(e.codi||'/'||e.sequencia||'/'||e.any) like lower('%'||:search||'%'))" +
			"and (:esNullTipusId = true or e.metaNode.id = :tipusId) " +
			"and (:esNullExpedientsToBeExcluded = true or e not in :expedientsToBeExluded) " + 
			"and (:esNullInteressat = true " +
			"		or  e.id in (" +
			"			select interessat.expedient.id " +
			"			from InteressatEntity interessat " +	
			"			where interessat.esRepresentant = false " +
			"				and (lower(interessat.documentNum||' '||interessat.nom||' '||interessat.llinatge1||' '||interessat.llinatge2) like lower('%'||:interessat||'%')" +
			"					or lower(interessat.raoSocial) like lower('%'||:interessat||'%')" +
			"					or lower(interessat.organNom) like lower('%'||:interessat||'%')))) " +
			"and (:esNullMetaExpedientDominiValor = true " +
			"		or  (select count(*) from DadaEntity dada where dada.node = e.id and dada.valor = :metaExpedientDominiValor) != 0) " +
			"and (e.grup is null or (:esNullRolsCurrentUser = false and e.grup in (select grup from GrupEntity grup where grup.rol in (:rolsCurrentUser)))) "
			)
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
			@Param("esNullEstatEnum") boolean esNullEstatEnum,
			@Param("estatEnum") ExpedientEstatEnumDto estatEnum,
			@Param("esNullEstat") boolean esNullEstat,
			@Param("estat") ExpedientEstatEntity estat,
			@Param("esNullAgafatPer") boolean esNullAgafatPer,
			@Param("agafatPer") UsuariEntity agafatPer,
			@Param("esNullSearch") boolean esNullSearch,
			@Param("search") String search,
			@Param("esNullTipusId") boolean esNullTipusId,
			@Param("tipusId") Long tipusId,
			@Param("esNullExpedientsToBeExcluded") boolean esNullExpedientsToBeExcluded, 
			@Param("expedientsToBeExluded") List<ExpedientEntity> expedientsToBeExluded,
			@Param("esNullInteressat") boolean esNullInteressat,
			@Param("interessat") String interessat,
			@Param("esNullMetaExpedientDominiValor") boolean esNullMetaExpedientDominiValor,
			@Param("metaExpedientDominiValor") String metaExpedientDominiValor,
			@Param("esNullRolsCurrentUser") boolean esNullRolsCurrentUser,
			@Param("rolsCurrentUser") List<String> rolsCurrentUser,
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
			"and (:esNullEstat = true or e.estat = :estat) " + 
			"and (:esNullInteressat = true " +
			"		or  e.id in (" +
			"			select interessat.expedient.id " +
			"			from InteressatEntity interessat " +	
			"			where interessat.esRepresentant = false " +
			"				and lower(interessat.documentNum||' '||interessat.nom||' '||interessat.llinatge1||' '||interessat.llinatge2) like lower('%'||:interessat||'%'))) ")
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
			@Param("estat") ExpedientEstatEnumDto estat,
			@Param("esNullInteressat") boolean esNullInteressat,
			@Param("interessat") String interessat);

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
	
	
//	@Query(	"select" +
//			"    e " +
//			"from" +
//			"    ExpedientEntity e " +
//			"where " +
//			"    e.entitat = :entitat " +
//			"and e.metaNode = :metaNode "
//			+ "and e != :expedient)")
//	List<ExpedientEntity> findByEntitatAndMetaExpedientWithoutGivenExp(
//			@Param("entitat") EntitatEntity entitat,
//			@Param("metaNode") MetaNodeEntity metaNode,
//			@Param("expedient") ExpedientEntity expedient);
	
	
	@Query(	"select" +
			"    count(e) " +
			"from" +
			"    ExpedientEntity e " +
			"where " +
			"   e.alertes IS EMPTY " +
			"and e.metaNode = :metaNode ")
	int findByMetaExpedientAndAlertesEmpty(
			@Param("metaNode") MetaNodeEntity metaNode);
	
	@Query(	"select" +
			"    count(e) " +
			"from" +
			"    ExpedientEntity e " +
			"where " +
			"   e.alertes IS NOT EMPTY " +
			"and e.metaNode = :metaNode ")
	int findByMetaExpedientAndAlertesNotEmpty(
			@Param("metaNode") MetaNodeEntity metaNode);
	
//	@Query(	"select" +
//			"    count(e) " +
//			"from" +
//			"    ExpedientEntity e " +
//			"where " +
//			"   e.alertes IS EMPTY " +
//			"and e.metaNode = :metaNode ")
//	int findByMetaExpedientAndValid(
//			@Param("metaNode") MetaNodeEntity metaNode);
}
