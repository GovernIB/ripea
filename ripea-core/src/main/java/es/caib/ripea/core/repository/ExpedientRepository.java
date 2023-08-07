/**
 * 
 */
package es.caib.ripea.core.repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.LockModeType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.ripea.core.aggregation.MetaExpedientCountAggregation;
import es.caib.ripea.core.api.dto.ExpedientEstatEnumDto;
import es.caib.ripea.core.entity.ContingutEntity;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.ExpedientEstatEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.MetaNodeEntity;
import es.caib.ripea.core.entity.OrganGestorEntity;
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
	
	List<ExpedientEntity> findByEntitatOrderByNomAsc(EntitatEntity entitat);
	
	List<ExpedientEntity> findByArxiuUuid(String arxiuUuid);
	
	@Query(	"select " +
			"    e.id " +
			"from " +
			"    ExpedientEntity e " +
			"where " +
			"    e.numero is null")
	List<Long> findAllIdsNumeroNotNull();
	
	@Query(	"from" +
			"    ExpedientEntity e "
			+ "where "
			+ "	 e.entitat = :entitat " 
			+ "	and e.metaNode = :metaNode " +
			"and e.numero = :numero")
	ExpedientEntity findByEntitatAndMetaNodeAndNumero(
			@Param("entitat") EntitatEntity entitat,
			@Param("metaNode") MetaNodeEntity metaNode,
			@Param("numero") String numero);


	@Query(	"select "
			+ "e.relacionatsAmb from" +
			"    ExpedientEntity e "
			+ "where "
			+ " e = :expedient")
	List<ExpedientEntity> findExpedientsRelacionats(
			@Param("expedient") ExpedientEntity expedient);

	@Query(	"select " +
			"    distinct e " +
			"from " +
			"    ExpedientEntity e " +
			"    left join e.metaexpedientOrganGestorPares meogp " +
			"where " +
			"    e.esborrat = 0 " +
			"and e.entitat = :entitat " +
			"and (" +
			"     (:esNullIdsMetaExpedientsPermesos = false and e.metaExpedient.id in (:idsMetaExpedientsPermesos)) " +
			"     or (:esNullIdsOrgansPermesos = false and meogp.organGestor.id in (:idsOrgansPermesos)) " +
			"     or (:esNullIdsMetaExpedientOrganPairsPermesos = false and meogp.id in (:idsMetaExpedientOrganPairsPermesos)) " +
			"     or (:esNullIdsOrgansAmbProcedimentsComunsPermesos = false and meogp.organGestor.id in (:idsOrgansAmbProcedimentsComunsPermesos) and e.metaExpedient.id in (:idsProcedimentsComuns))) " +
		//TODO if organ is in :idsOrgansAmbProcedimentsComunsPermesos it is also already in :idsOrgansPermesos as well so check :idsOrgansAmbProcedimentsComunsPermesos doesn't do anything, probably :idsOrgansPermesos check should be only allowed for procediments no comuns
			"and (:esNullMetaNode = true or e.metaNode = :metaNode) " +
			"and (:esNullMetaExpedientIdDomini = true or e.metaExpedient.id in (:metaExpedientIdDomini)) " +
			"and (:esNullOrganGestor = true or e.organGestor = :organGestor) " +
			"and (:esNullNumero = true or lower(e.numero) like lower('%'||:numero||'%')) " +
			"and (:esNullNom = true or lower(e.nom) like lower('%'||:nom||'%')) " +
			"and (:esNullCreacioInici = true or e.createdDate >= :creacioInici) " +
			"and (:esNullCreacioFi = true or e.createdDate <= :creacioFi) " +
			"and (:esNullTancatInici = true or e.createdDate >= :tancatInici) " +
			"and (:esNullTancatFi = true or e.createdDate <= :tancatFi) " +
			"and (:esNullEstatEnum = true or (e.estat = :estatEnum and (e.estatAdditional is null or :esNullMetaNode = true))) " +
			"and (:esNullEstat = true or e.estatAdditional = :estat) " +
			"and (:esNullAgafatPer = true or e.agafatPer = :agafatPer) " +
			"and (:esNullTipusId = true or e.metaNode.id = :tipusId) " +
			"and (:esNullExpedientsToBeExcluded = true or e not in (:expedientsToBeExluded)) " +
			"and (:esNullInteressat = true " +
			"		or  e.id in (" +
			"			select interessat.expedient.id " +
			"			from InteressatEntity interessat " +	
			"			where interessat.esRepresentant = false " +
			"				and (lower(interessat.documentNum||' '||interessat.nom||' '||interessat.llinatge1||' '||interessat.llinatge2) like lower('%'||:interessat||'%')" +
			"					or lower(interessat.raoSocial) like lower('%'||:interessat||'%')" +
			"					or lower(interessat.organNom) like lower('%'||:interessat||'%')))) " +
			"and (:esNullMetaExpedientDominiValor = true " +
			"		or  (select count(*) from DadaEntity dada where dada.metaDada.codi = :metaExpedientDominiCodi and dada.node = e.id and dada.valor = :metaExpedientDominiValor) != 0) " +
			"and (:isAdmin = true or (e.grup is null or (:esNullRolsCurrentUser = false and e.grup in (select grup from GrupEntity grup where grup.rol in (:rolsCurrentUser))))) " +
			"and (:esFiltrarExpedientsAmbFirmaPendent != true " + 
			"		or e.id in (" + 
			"			select dp.expedient.id " + 
			"			from DocumentPortafirmesEntity dp " + 
			"			where (dp.estat = es.caib.ripea.core.api.dto.DocumentEnviamentEstatEnumDto.PENDENT or " + 
			"				   dp.estat = es.caib.ripea.core.api.dto.DocumentEnviamentEstatEnumDto.ENVIAT) " + 
			"				   and dp.error = false))"
			)
	Page<ExpedientEntity> findByEntitatAndPermesosAndFiltre(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullIdsMetaExpedientsPermesos") boolean esNullIdsMetaExpedientsPermesos, 
			@Param("idsMetaExpedientsPermesos") List<Long> idsMetaExpedientsPermesos,
			@Param("esNullIdsOrgansPermesos") boolean esNullIdsOrgansPermesos, 
			@Param("idsOrgansPermesos") List<Long> idsOrgansPermesos,
			@Param("esNullIdsMetaExpedientOrganPairsPermesos") boolean esNullIdsMetaExpedientOrganPairsPermesos, 
			@Param("idsMetaExpedientOrganPairsPermesos") List<Long> idsMetaExpedientOrganPairsPermesos,
			@Param("esNullIdsOrgansAmbProcedimentsComunsPermesos") boolean esNullIdsOrgansAmbProcedimentsComunsPermesos, 
			@Param("idsOrgansAmbProcedimentsComunsPermesos") List<Long> idsOrgansAmbProcedimentsComunsPermesos,
			@Param("idsProcedimentsComuns") List<Long> idsProcedimentsComuns,
			@Param("esNullMetaNode") boolean esNullMetaNode,
			@Param("metaNode") MetaNodeEntity metaNode,
			@Param("esNullMetaExpedientIdDomini") boolean esNullMetaExpedientIdDomini,
			@Param("metaExpedientIdDomini") List<Long> metaExpedientIdDomini,
			@Param("esNullOrganGestor") boolean esNullOrganGestor,
			@Param("organGestor") OrganGestorEntity organGestor,
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
			@Param("esNullTipusId") boolean esNullTipusId,
			@Param("tipusId") Long tipusId,
			@Param("esNullExpedientsToBeExcluded") boolean esNullExpedientsToBeExcluded, 
			@Param("expedientsToBeExluded") List<ExpedientEntity> expedientsToBeExluded,
			@Param("esNullInteressat") boolean esNullInteressat,
			@Param("interessat") String interessat,
			@Param("metaExpedientDominiCodi") String metaExpedientDominiCodi,
			@Param("esNullMetaExpedientDominiValor") boolean esNullMetaExpedientDominiValor,
			@Param("metaExpedientDominiValor") String metaExpedientDominiValor,
			@Param("esNullRolsCurrentUser") boolean esNullRolsCurrentUser,
			@Param("rolsCurrentUser") List<String> rolsCurrentUser,
			@Param("isAdmin") boolean isAdmin,
			@Param("esFiltrarExpedientsAmbFirmaPendent") boolean esFiltrarExpedientsAmbFirmaPendent,
			Pageable pageable);

	
	
	
	@Query(	"select " +
			"    distinct e.id " +
			"from " +
			"    ExpedientEntity e " +
			"    left join e.organGestorPares eogp " +
			"    left join eogp.metaExpedientOrganGestor eogpmeog " +
			"    left join eogp.metaExpedientOrganGestor.organGestor eogpmeogog " +
			"where " +
			"    e.esborrat = 0 " +
			"and e.entitat = :entitat " +
			"and (" +
			"     (:esNullMetaExpedientIdPermesos = false and e.metaExpedient.id in (:metaExpedientIdPermesos)) " +
			"     or (:esNullOrganIdPermesos = false and e.organGestor.id in (:organIdPermesos)) " +
			"     or (:esNullOrganIdPermesos = false and eogpmeogog.id in (:organIdPermesos)) " +
			"     or (:esNullMetaExpedientOrganIdPermesos = false and eogpmeog.id in (:metaExpedientOrganIdPermesos)) " +
			"     or (:esNullOrganProcedimentsComunsIdsPermesos = false and eogpmeogog.id in (:organProcedimentsComunsIdsPermesos) and e.metaExpedient.id in (:procedimentsComunsIds))) " +
			"and (:esNullMetaNode = true or e.metaNode = :metaNode) " +
			"and (:esNullMetaExpedientIdDomini = true or e.metaExpedient.id in (:metaExpedientIdDomini)) " +
			"and (:esNullOrganGestor = true or e.organGestor = :organGestor) " +
			"and (:esNullNumero = true or lower(e.numero) like lower('%'||:numero||'%')) " +
			"and (:esNullNom = true or lower(e.nom) like lower('%'||:nom||'%')) " +
			"and (:esNullCreacioInici = true or e.createdDate >= :creacioInici) " +
			"and (:esNullCreacioFi = true or e.createdDate <= :creacioFi) " +
			"and (:esNullTancatInici = true or e.createdDate >= :tancatInici) " +
			"and (:esNullTancatFi = true or e.createdDate <= :tancatFi) " +
			"and (:esNullEstatEnum = true or (e.estat = :estatEnum and (e.estatAdditional is null or :esNullMetaNode = true))) " +
			"and (:esNullEstat = true or e.estatAdditional = :estat) " +
			"and (:esNullAgafatPer = true or e.agafatPer = :agafatPer) " +
			"and (:esNullTipusId = true or e.metaNode.id = :tipusId) " +
			"and (:esNullExpedientsToBeExcluded = true or e not in (:expedientsToBeExluded)) " +
			"and (:esNullInteressat = true " +
			"		or  e.id in (" +
			"			select interessat.expedient.id " +
			"			from InteressatEntity interessat " +	
			"			where interessat.esRepresentant = false " +
			"				and (lower(interessat.documentNum||' '||interessat.nom||' '||interessat.llinatge1||' '||interessat.llinatge2) like lower('%'||:interessat||'%')" +
			"					or lower(interessat.raoSocial) like lower('%'||:interessat||'%')" +
			"					or lower(interessat.organNom) like lower('%'||:interessat||'%')))) " +
			"and (:esNullMetaExpedientDominiValor = true " +
			"		or  (select count(*) from DadaEntity dada where dada.metaDada.codi = :metaExpedientDominiCodi and dada.node = e.id and dada.valor = :metaExpedientDominiValor) != 0) " +
			"and (:isAdmin = true or (e.grup is null or (:esNullRolsCurrentUser = false and e.grup in (select grup from GrupEntity grup where grup.rol in (:rolsCurrentUser))))) " + 
			"and (:esFiltrarExpedientsAmbFirmaPendent != true " + 
			"		or e.id in (" + 
			"			select dp.expedient.id " + 
			"			from DocumentPortafirmesEntity dp " + 
			"			where (dp.estat = es.caib.ripea.core.api.dto.DocumentEnviamentEstatEnumDto.PENDENT or " + 
			"				   dp.estat = es.caib.ripea.core.api.dto.DocumentEnviamentEstatEnumDto.ENVIAT)"
			+ "				   and dp.error = false))"
			)
	List<Long> findIdsByEntitatAndFiltre(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullMetaExpedientIdPermesos") boolean esNullMetaExpedientIdPermesos, 
			@Param("metaExpedientIdPermesos") List<Long> metaExpedientIdPermesos,
			@Param("esNullOrganIdPermesos") boolean esNullOrganIdPermesos, 
			@Param("organIdPermesos") List<Long> organIdPermesos,
			@Param("esNullMetaExpedientOrganIdPermesos") boolean esNullMetaExpedientOrganIdPermesos, 
			@Param("metaExpedientOrganIdPermesos") List<Long> metaExpedientOrganIdPermesos,
			@Param("esNullOrganProcedimentsComunsIdsPermesos") boolean esNullOrganProcedimentsComunsIdsPermesos, 
			@Param("organProcedimentsComunsIdsPermesos") List<Long> organProcedimentsComunsIdsPermesos,
			@Param("procedimentsComunsIds") List<Long> procedimentsComunsIds,
			@Param("esNullMetaNode") boolean esNullMetaNode,
			@Param("metaNode") MetaNodeEntity metaNode,
			@Param("esNullMetaExpedientIdDomini") boolean esNullMetaExpedientIdDomini,
			@Param("metaExpedientIdDomini") List<Long> metaExpedientIdDomini,
			@Param("esNullOrganGestor") boolean esNullOrganGestor,
			@Param("organGestor") OrganGestorEntity organGestor,
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
			@Param("esNullTipusId") boolean esNullTipusId,
			@Param("tipusId") Long tipusId,
			@Param("esNullExpedientsToBeExcluded") boolean esNullExpedientsToBeExcluded, 
			@Param("expedientsToBeExluded") List<ExpedientEntity> expedientsToBeExluded,
			@Param("esNullInteressat") boolean esNullInteressat,
			@Param("interessat") String interessat,
			@Param("metaExpedientDominiCodi") String metaExpedientDominiCodi,
			@Param("esNullMetaExpedientDominiValor") boolean esNullMetaExpedientDominiValor,
			@Param("metaExpedientDominiValor") String metaExpedientDominiValor,
			@Param("esNullRolsCurrentUser") boolean esNullRolsCurrentUser,
			@Param("rolsCurrentUser") List<String> rolsCurrentUser,
			@Param("isAdmin") boolean isAdmin,
			@Param("esFiltrarExpedientsAmbFirmaPendent") boolean esFiltrarExpedientsAmbFirmaPendent);
	
	
	@Query(	"select " +
			"    distinct e " +
			"from " +
			"    ExpedientEntity e " +
			"where " +
			"    e.esborrat = 0 " +
			"and e.entitat = :entitat " +
			"and (:esNullMetaNode = true or e.metaNode = :metaNode) " +
			"and (:esNullNumero = true or lower(e.numero) like lower('%'||:numero||'%')) " +
			"and (:esNullNom = true or lower(e.nom) like lower('%'||:nom||'%')) " +
			"and (:esNullEstatEnum = true or e.estat = :estatEnum) " +
			"and (:esNullEstat = true or e.estatAdditional = :estat) " +
			"and (e.id in (:expedientsRelacionatsIdx)) ")
	Page<ExpedientEntity> findExpedientsRelacionatsByIdIn(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullMetaNode") boolean esNullMetaNode,
			@Param("metaNode") MetaNodeEntity metaNode,
			@Param("esNullNumero") boolean esNullNumero,
			@Param("numero") String numero,
			@Param("esNullNom") boolean esNullNom,
			@Param("nom") String nom,
			@Param("esNullEstatEnum") boolean esNullEstatEnum,
			@Param("estatEnum") ExpedientEstatEnumDto estatEnum,
			@Param("esNullEstat") boolean esNullEstat,
			@Param("estat") ExpedientEstatEntity estat,
			@Param("expedientsRelacionatsIdx") Collection<Long> ids,
			Pageable pageable);
	
	
	
	
	List<ExpedientEntity> findByEntitatAndIdInOrderByIdAsc(
			EntitatEntity entitat,
			Collection<Long> id);
	
	List<ExpedientEntity> findByOrganGestor(
			OrganGestorEntity organGestor);
	
	List<ExpedientEntity> findByOrganGestorAndEstat(
			OrganGestorEntity organGestor,
			ExpedientEstatEnumDto estat);

	@Query(	"select" +
			"    e " +
			"from" +
			"    ExpedientEntity e " +
			"where " +
			"    e.entitat = :entitat " +
			"and e.esborrat = 0 " +
			"and (e.metaNode is null or e.metaNode in (:metaNodesPermesos)) " +
			"and (:esNullMetaNode = true or e.metaNode = :metaNode)")
	List<ExpedientEntity> findByEntitatAndMetaExpedientOrderByNomAsc(
			@Param("entitat") EntitatEntity entitat,
			@Param("metaNodesPermesos") List<? extends MetaNodeEntity> metaNodesPermesos,
			@Param("esNullMetaNode") boolean esNullMetaNode,
			@Param("metaNode") MetaNodeEntity metaNode);

	Page<ExpedientEntity> findByMetaExpedientAndEsborrat(MetaExpedientEntity metaExpedient, int esborrat, Pageable pageable);
	
	@Query(	"select " +
			"    e " +
			"from " +
			"    ExpedientEntity e " +
			"where " +
			"    e.entitat = :entitat " +
			"and e.esborrat = 0 " +
			"and e.estat = 0 " +
			"and (:nomesAgafats = false or e.agafatPer = :usuariActual) " +
			"and (e.metaNode in (:metaExpedientsPermesos)) " +
			"and (:esNullMetaExpedient = true or e.metaNode = :metaExpedient) " +
			"and (:esNullNom = true or lower(e.nom) like lower('%'||:nom||'%')) " +
			"and (:esNullDataInici = true or e.createdDate >= :dataInici) " +
			"and (:esNullDataFi = true or e.createdDate <= :dataFi) ")
	public Page<ExpedientEntity> findExpedientsPerCanviEstatMassiu(
			@Param("entitat") EntitatEntity entitat,
			@Param("nomesAgafats") boolean nomesAgafats,
			@Param("usuariActual") UsuariEntity usuariActual,
			@Param("metaExpedientsPermesos") List<? extends MetaNodeEntity> metaExpedientsPermesos,
			@Param("esNullMetaExpedient") boolean esNullMetaExpedient,
			@Param("metaExpedient") MetaNodeEntity metaExpedient,	
			@Param("esNullNom") boolean esNullNom,
			@Param("nom") String nom,
			@Param("esNullDataInici") boolean esNullDataInici,
			@Param("dataInici") Date dataInici,
			@Param("esNullDataFi") boolean esNullDataFi,
			@Param("dataFi") Date dataFi,
			Pageable pageable);

	@Query(	"select " +
			"    e.id " +
			"from " +
			"    ExpedientEntity e " +
			"where " +
			"    e.entitat = :entitat " +
			"and e.esborrat = 0 " +
			"and e.estat = 0 " +
			"and (:nomesAgafats = false or e.agafatPer = :usuariActual) " +
			"and (e.metaNode in (:metaExpedientsPermesos)) " +
			"and (:esNullMetaExpedient = true or e.metaNode = :metaExpedient) " +
			"and (:esNullNom = true or lower(e.nom) like lower('%'||:nom||'%')) " +
			"and (:esNullDataInici = true or e.createdDate >= :dataInici) " +
			"and (:esNullDataFi = true or e.createdDate <= :dataFi) ")
	public List<Long> findIdsExpedientsPerCanviEstatMassiu(
			@Param("entitat") EntitatEntity entitat,
			@Param("nomesAgafats") boolean nomesAgafats,
			@Param("usuariActual") UsuariEntity usuariActual,
			@Param("metaExpedientsPermesos") List<? extends MetaNodeEntity> metaExpedientsPermesos,
			@Param("esNullMetaExpedient") boolean esNullMetaExpedient,
			@Param("metaExpedient") MetaNodeEntity metaExpedient,	
			@Param("esNullNom") boolean esNullNom,
			@Param("nom") String nom,
			@Param("esNullDataInici") boolean esNullDataInici,
			@Param("dataInici") Date dataInici,
			@Param("esNullDataFi") boolean esNullDataFi,
			@Param("dataFi") Date dataFi);

//	@Query(	"select" +
//			"    count(e) " +
//			"from" +
//			"    ExpedientEntity e " +
//			"where " +
//			"   e.alertes IS NOT EMPTY " +
//			"and e.metaNode = :metaNode ")
//	int findByMetaExpedientAndAlertesNotEmpty(
//			@Param("metaNode") MetaNodeEntity metaNode);

	@Query(	"select" +
			"    new es.caib.ripea.core.aggregation.MetaExpedientCountAggregation( " +
			"	     e.metaExpedient, " +
			"        count(e) " +
			"    ) " +
			"from" +
			"    ExpedientEntity e " +
			"where " +
			"   e.alertes IS NOT EMPTY " +
			"group by" +
			"  e.metaExpedient ")
	List<MetaExpedientCountAggregation> countByAlertesNotEmptyGroupByMetaExpedient();

	@Query(	"select " +
			"    e " +
			"from " +
			"    ExpedientEntity e " +
			"where " +
			"    e.entitat = :entitat " +
			"and e.esborrat = 0 " +
			"and e.estat = 0 " +
			"and (:nomesAgafats = false or e.agafatPer = :usuariActual) " +
			"and (e.metaNode in (:metaExpedientsPermesos)) " +
			"and (:esNullMetaExpedient = true or e.metaNode = :metaExpedient) " +
			"and (:esNullNom = true or lower(e.nom) like lower('%'||:nom||'%')) " +
			"and (:esNullDataInici = true or e.createdDate >= :dataInici) " +
			"and (:esNullDataFi = true or e.createdDate <= :dataFi) " +
			"and (select count(document) from DocumentEntity document where " + // no documents en process de firma
			"	document.expedient = e " +
			"	and (document.estat = es.caib.ripea.core.api.dto.DocumentEstatEnumDto.FIRMA_PENDENT " +
			"		or document.estat = es.caib.ripea.core.api.dto.DocumentEstatEnumDto.FIRMA_PENDENT_VIAFIRMA " +
			"		or document.estat = es.caib.ripea.core.api.dto.DocumentEstatEnumDto.FIRMA_PARCIAL)) = 0 " +   
			"and (select count(document) from DocumentEntity document where document.expedient = e and document.esborrat = 0) > 0 " +   // at least one document no esborrat
			"and (select " +																										// all dades obligatoris created
			"	     	count(metaDada) " +
			"	  from " +
			"			MetaDadaEntity metaDada" +
			"	  where " +
			"			metaDada.activa = true " +
			"			and metaDada.metaNode = e.metaNode " +
			"			and (metaDada.multiplicitat = 0 or metaDada.multiplicitat = 3) " +
			"			and metaDada not in (select dada.metaDada from DadaEntity dada where e.id = dada.node.id)) = 0 " +
			"and (select " +																									// all documents obligatoris created
			"	     	count(metaDocument) " +
			"	  from " +
			"			MetaDocumentEntity metaDocument" +
			"	  where " +
			"			metaDocument.metaExpedient = e.metaExpedient " +
			"			and (metaDocument.multiplicitat = 0 or metaDocument.multiplicitat = 3) " +
			"			and metaDocument.id not in (select doc.metaNode.id from DocumentEntity doc where e.id = doc.expedient.id)) = 0"			
			)
	public Page<ExpedientEntity> findExpedientsPerTancamentMassiu(
			@Param("entitat") EntitatEntity entitat,
			@Param("nomesAgafats") boolean nomesAgafats,
			@Param("usuariActual") UsuariEntity usuariActual,
			@Param("metaExpedientsPermesos") List<? extends MetaNodeEntity> metaExpedientsPermesos,
			@Param("esNullMetaExpedient") boolean esNullMetaExpedient,
			@Param("metaExpedient") MetaNodeEntity metaExpedient,	
			@Param("esNullNom") boolean esNullNom,
			@Param("nom") String nom,
			@Param("esNullDataInici") boolean esNullDataInici,
			@Param("dataInici") Date dataInici,
			@Param("esNullDataFi") boolean esNullDataFi,
			@Param("dataFi") Date dataFi,
			Pageable pageable);

	@Query(	"select " +
			"    e.id " +
			"from " +
			"    ExpedientEntity e " +
			"where " +
			"    e.entitat = :entitat " + 
			"and e.esborrat = 0 " +
			"and e.estat = 0 " +
			"and (:nomesAgafats = false or e.agafatPer = :usuariActual) " +
			"and (e.metaNode in (:metaExpedientsPermesos)) " +
			"and (:esNullMetaExpedient = true or e.metaNode = :metaExpedient) " +
			"and (:esNullNom = true or lower(e.nom) like lower('%'||:nom||'%')) " +
			"and (:esNullDataInici = true or e.createdDate >= :dataInici) " +
			"and (:esNullDataFi = true or e.createdDate <= :dataFi) " +
			"and (select count(document) from DocumentEntity document where document.expedient = e and document.estat = 3) > 0 " + 
			"and (select " +
			"	     	count(metaDada) " +
			"	  from " +
			"			MetaDadaEntity metaDada" +
			"	  where " +
			"			metaDada.activa = true " +
			"			and metaDada.metaNode = e.metaNode " +
			"			and (metaDada.multiplicitat = 0 or metaDada.multiplicitat = 3) " +
			"			and metaDada not in (select dada.metaDada from DadaEntity dada where e.id = dada.node.id)) = 0 " +
			"and (select " +
			"	     	count(metaDocument) " +
			"	  from " +
			"			MetaDocumentEntity metaDocument" +
			"	  where " +
			"			metaDocument.metaExpedient = e.metaExpedient " +
			"			and (metaDocument.multiplicitat = 0 or metaDocument.multiplicitat = 3) " +
			"			and metaDocument.id not in (select doc.metaNode.id from DocumentEntity doc where e.id = doc.expedient.id)) = 0"			
			)
	public List<Long> findIdsExpedientsPerTancamentMassiu(
			@Param("entitat") EntitatEntity entitat,
			@Param("nomesAgafats") boolean nomesAgafats,
			@Param("usuariActual") UsuariEntity usuariActual,
			@Param("metaExpedientsPermesos") List<? extends MetaNodeEntity> metaExpedientsPermesos,
			@Param("esNullMetaExpedient") boolean esNullMetaExpedient,
			@Param("metaExpedient") MetaNodeEntity metaExpedient,	
			@Param("esNullNom") boolean esNullNom,
			@Param("nom") String nom,
			@Param("esNullDataInici") boolean esNullDataInici,
			@Param("dataInici") Date dataInici,
			@Param("esNullDataFi") boolean esNullDataFi,
			@Param("dataFi") Date dataFi);
	
	
	
//	@Query(	"select " +
//			"    distinct e " +
//			"from " +
//			"    ExpedientEntity e " +
//			"    join MetaExpedientOrganGestorEntity mo on e.organGestor = mo.organGestor and e.metaExpedient = mo.metaExpedient " +
//			"where " +
//			"    e.esborrat = 0 " +
//			"and ((:esNullMetaExpedientIdPermesos = false and e.metaExpedient.id in (:metaExpedientIdPermesos)) " +
//			"     or (:esNullOrganIdPermesos = false and e.organGestor.id in (:organIdPermesos)) " +
//			"     or (:esNullMetaExpedientOrganIdPermesos = false and mo in (:metaExpedientOrganIdPermesos)))"
//			)
//	Page<ExpedientEntity> findExpedientsPermittedOneWay(
//			@Param("esNullMetaExpedientIdPermesos") boolean esNullMetaExpedientIdPermesos, 
//			@Param("metaExpedientIdPermesos") List<Long> metaExpedientIdPermesos,
//			@Param("esNullOrganIdPermesos") boolean esNullOrganIdPermesos, 
//			@Param("organIdPermesos") List<Long> organIdPermesos,
//			@Param("esNullMetaExpedientOrganIdPermesos") boolean esNullMetaExpedientOrganIdPermesos, 
//			@Param("metaExpedientOrganIdPermesos") List<Long> metaExpedientOrganIdPermesos,
//			Pageable pageable);
//	


	

	
	
	@Query(	"select " +
			"    distinct e " +
			"from " +
			"    ExpedientEntity e " +
			"    left join e.metaexpedientOrganGestorPares meogp " +
			"where " +
			"	 e.arxiuUuid = null " +
			"and e.esborrat = 0 " +
			"and e.entitat = :entitat " +
			"and (" +
			"     (:esNullIdsMetaExpedientsPermesos = false and e.metaExpedient.id in (:idsMetaExpedientsPermesos)) " +
			"     or (:esNullIdsOrgansPermesos = false and meogp.organGestor.id in (:idsOrgansPermesos)) " +
			"     or (:esNullIdsMetaExpedientOrganPairsPermesos = false and meogp.id in (:idsMetaExpedientOrganPairsPermesos)) " +
			"     or (:esNullIdsOrgansAmbProcedimentsComunsPermesos = false and meogp.organGestor.id in (:idsOrgansAmbProcedimentsComunsPermesos) and e.metaExpedient.id in (:idsProcedimentsComuns))) " +
		//TODO if organ is in :idsOrgansAmbProcedimentsComunsPermesos it is also already in :idsOrgansPermesos as well so check :idsOrgansAmbProcedimentsComunsPermesos doesn't do anything, probably :idsOrgansPermesos check should be only allowed for procediments no comuns
			"and (:nomesAgafats = false or e.agafatPer.codi = :usuariActual) " +			
			"and (:esNullNom = true or lower(e.nom) like lower('%'||:nom||'%')) " +
			"and (:esNullMetaExpedient = true or e.metaExpedient = :metaExpedient) ")
	public Page<ExpedientEntity> findArxiuPendents(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullIdsMetaExpedientsPermesos") boolean esNullIdsMetaExpedientsPermesos, 
			@Param("idsMetaExpedientsPermesos") List<Long> idsMetaExpedientsPermesos,
			@Param("esNullIdsOrgansPermesos") boolean esNullIdsOrgansPermesos, 
			@Param("idsOrgansPermesos") List<Long> idsOrgansPermesos,
			@Param("esNullIdsMetaExpedientOrganPairsPermesos") boolean esNullIdsMetaExpedientOrganPairsPermesos, 
			@Param("idsMetaExpedientOrganPairsPermesos") List<Long> idsMetaExpedientOrganPairsPermesos,
			@Param("esNullIdsOrgansAmbProcedimentsComunsPermesos") boolean esNullIdsOrgansAmbProcedimentsComunsPermesos, 
			@Param("idsOrgansAmbProcedimentsComunsPermesos") List<Long> idsOrgansAmbProcedimentsComunsPermesos,
			@Param("idsProcedimentsComuns") List<Long> idsProcedimentsComuns,
			@Param("nomesAgafats") boolean nomesAgafats,
			@Param("usuariActual") String usuariActual,
			@Param("esNullNom") boolean esNullNom,
			@Param("nom") String nom,
			@Param("esNullMetaExpedient") boolean esNullMetaExpedient,
			@Param("metaExpedient") MetaExpedientEntity metaExpedient,
			Pageable pageable);
	
	
	
	@Query(	"select " +
			"    e " +
			"from " +
			"    ExpedientEntity e " +
			"where " +
			"	 e.arxiuUuid = null " +
			"and e.esborrat = 0 " +
			"and e.entitat = :entitat " +
			"and (e.metaNode in (:metaExpedientsPermesos)) " +
			"and (:nomesAgafats = false or e.agafatPer.codi = :usuariActual) " +			
			"and (:esNullNom = true or lower(e.nom) like lower('%'||:nom||'%')) " +
			"and (:esNullMetaExpedient = true or e.metaExpedient = :metaExpedient) ")
	public Page<ExpedientEntity> findArxiuPendents(
			@Param("entitat") EntitatEntity entitat,
			@Param("metaExpedientsPermesos") List<? extends MetaNodeEntity> metaExpedientsPermesos,
			@Param("nomesAgafats") boolean nomesAgafats,
			@Param("usuariActual") String usuariActual,
			@Param("esNullNom") boolean esNullNom,
			@Param("nom") String nom,
			@Param("esNullMetaExpedient") boolean esNullMetaExpedient,
			@Param("metaExpedient") MetaExpedientEntity metaExpedient,
			Pageable pageable);	
	
	
	@Query(	"select " +
			"    e.id " +
			"from " +
			"    ExpedientEntity e " +
			"where " +
			"	 e.arxiuUuid = null " +
			"and e.esborrat = 0 " +
			"and e.entitat = :entitat " +
			"and (e.metaNode in (:metaExpedientsPermesos)) " +
			"and (:nomesAgafats = false or e.agafatPer.codi = :usuariActual) " +			
			"and (:esNullNom = true or lower(e.nom) like lower('%'||:nom||'%')) " +
			"and (:esNullMetaExpedient = true or e.metaExpedient = :metaExpedient) ")
	public List<Long> findIdsArxiuPendents(
			@Param("entitat") EntitatEntity entitat,
			@Param("metaExpedientsPermesos") List<? extends MetaNodeEntity> metaExpedientsPermesos,
			@Param("nomesAgafats") boolean nomesAgafats,
			@Param("usuariActual") String usuariActual,
			@Param("esNullNom") boolean esNullNom,
			@Param("nom") String nom,
			@Param("esNullMetaExpedient") boolean esNullMetaExpedient,
			@Param("metaExpedient") MetaExpedientEntity metaExpedient);	



	@Query(	"select " +
			"    e " +
			"from " +
			"    ExpedientEntity e " +
			"where " +
			"    e.entitat = :entitat " +
			"and (lower(e.nom) like lower('%'||:text||'%') or lower(e.numero) like lower('%'||:text||'%')) ")
	public List<ExpedientEntity> findByText(
			@Param("entitat") EntitatEntity entitat,
			@Param("text") String text);
	
	
	@Query(	"select" +
			"    e " +
			"from" +
			"    ExpedientEntity e " +
			"where " +
			"e.esborrat = 0 " +
			"and e.entitat = :entitat " +
			"and e.metaNode = :metaNode ORDER BY e.nom DESC")
	List<ExpedientEntity> findByEntitatAndMetaExpedient(
			@Param("entitat") EntitatEntity entitat,
			@Param("metaNode") MetaNodeEntity metaNode);
	
	@Query(	"select" +
			"    e " +
			"from" +
			"    ExpedientEntity e " +
			"where " +
			"e.esborrat = 0 " +
			"and e.entitat = :entitat " +
			"and e.organGestor.id in (:organsIdsPermitted) " +
			"and e.metaNode = :metaNode ORDER BY e.nom DESC")
	List<ExpedientEntity> findByEntitatAndMetaExpedientAndOrgans(
			@Param("entitat") EntitatEntity entitat,
			@Param("organsIdsPermitted") List<Long> organsIdsPermitted,
			@Param("metaNode") MetaNodeEntity metaNode);
	
	
	@Query(	"select" +
			"    max(e.sequencia) " +
			"from" +
			"    ExpedientEntity e " +
			"where " +
			"e.metaNode = :metaNode " +
			"and e.any = :any")
	Long findMaxSequencia(
			@Param("metaNode") MetaNodeEntity metaNode,
			@Param("any") int any);
	

	@Query(	"select count(e.id) from ExpedientEntity e where e.organGestor = :organGestor")
	Integer countByOrganGestor(@Param("organGestor") OrganGestorEntity organGestor);

	@Query(	"from" +
			"    ExpedientEntity e "
			+ "where " + 
			"e.esborrat = 0 " +
			"and e.estat = :estat " +
			"and e.entitat = :entitat " +
			"and e.tancatData is null " +
			"and e.tancatProgramat = :tancatProgramat ORDER BY e.tancatProgramat DESC")
	List<ExpedientEntity> findByEstatAndTancatLogicOrderByTancatProgramat(
			@Param("estat") ExpedientEstatEnumDto estat,
			@Param("entitat") EntitatEntity entitat,
			@Param("tancatProgramat") Date tancatProgramat);
	
	@Lock(LockModeType.PESSIMISTIC_READ)
	@Query(	"select " +
			"    e " +
			"from " +
			"    ExpedientEntity e " +
			"where " +
			"    e.id = :id")
	public ExpedientEntity findWithLock(@Param("id") Long id);
	
}
