/**
 * 
 */
package es.caib.ripea.core.repository;

import es.caib.ripea.core.api.dto.ExpedientEstatEnumDto;
import es.caib.ripea.core.entity.ContingutEntity;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.ExpedientEstatEntity;
import es.caib.ripea.core.entity.GrupEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.MetaNodeEntity;
import es.caib.ripea.core.entity.OrganGestorEntity;
import es.caib.ripea.core.entity.UsuariEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Date;
import java.util.List;

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

	List<ExpedientEntity> findByEntitatOrderByNomAsc(EntitatEntity entitat);


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


	@Query(	"select " +
			"    distinct e " +
			"from " +
			"    ExpedientEntity e " +
			"    left join e.metaexpedientOrganGestorPares meogp " +
			"where " +
			"    e.esborrat = 0 " +
			"and e.entitat = :entitat " +
			"and (" +
			"     (:esNullIdsMetaExpedientsPermesos = false and (e.metaExpedient.id in (:idsMetaExpedientsPermesos0)" +
			"			or e.metaExpedient.id in (:idsMetaExpedientsPermesos1)" +
			"			or e.metaExpedient.id in (:idsMetaExpedientsPermesos2)" +
			"			or e.metaExpedient.id in (:idsMetaExpedientsPermesos3))) " +
			"     or (:esNullIdsOrgansPermesos = false and (meogp.organGestor.id in (:idsOrgansPermesos0)" +
			"			or meogp.organGestor.id in (:idsOrgansPermesos1)" +
			"			or meogp.organGestor.id in (:idsOrgansPermesos2)" +
			"			or meogp.organGestor.id in (:idsOrgansPermesos3))) " +
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
			"and (:isAdmin = true or (e.grup is null or (:esNullIdsGrupsPermesos = false and e.grup.id in (:idsGrupsPermesos)))) " +
			"and (:esFiltrarExpedientsAmbFirmaPendent != true " + 
			"		or e.id in (" + 
			"			select dp.expedient.id " + 
			"			from DocumentPortafirmesEntity dp " + 
			"			where (dp.estat = es.caib.ripea.core.api.dto.DocumentEnviamentEstatEnumDto.PENDENT or " + 
			"				   dp.estat = es.caib.ripea.core.api.dto.DocumentEnviamentEstatEnumDto.ENVIAT) " + 
			"				   and dp.error = false)) " +
			"and (:esNullNumeroRegistre = true " +
			"		or lower(e.registresImportats) like lower('%'||:numeroRegistre||'%'))" +
			"and (:esNullGrup = true or e.grup = :grup) "
			)
	Page<ExpedientEntity> findByEntitatAndPermesosAndFiltre(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullIdsMetaExpedientsPermesos") boolean esNullIdsMetaExpedientsPermesos, 
			@Param("idsMetaExpedientsPermesos0") List<Long> idsMetaExpedientsPermesos0,
			@Param("idsMetaExpedientsPermesos1") List<Long> idsMetaExpedientsPermesos1,
			@Param("idsMetaExpedientsPermesos2") List<Long> idsMetaExpedientsPermesos2,
			@Param("idsMetaExpedientsPermesos3") List<Long> idsMetaExpedientsPermesos3,
			@Param("esNullIdsOrgansPermesos") boolean esNullIdsOrgansPermesos,
			@Param("idsOrgansPermesos0") List<Long> idsOrgansPermesos0,
			@Param("idsOrgansPermesos1") List<Long> idsOrgansPermesos1,
			@Param("idsOrgansPermesos2") List<Long> idsOrgansPermesos2,
			@Param("idsOrgansPermesos3") List<Long> idsOrgansPermesos3,
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
			@Param("esNullIdsGrupsPermesos") boolean esNullIdsGrupsPermesos,
			@Param("idsGrupsPermesos") List<Long> idsGrupsPermesos,
			@Param("isAdmin") boolean isAdmin,
			@Param("esFiltrarExpedientsAmbFirmaPendent") boolean esFiltrarExpedientsAmbFirmaPendent,
			@Param("esNullNumeroRegistre") boolean esNullNumeroRegistre,
			@Param("numeroRegistre") String numeroRegistre,
			@Param("esNullGrup") boolean esNullGrup,
			@Param("grup") GrupEntity grup,
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
			"     (:esNullMetaExpedientIdPermesos = false and (e.metaExpedient.id in (:metaExpedientIdPermesos0) " +
			"			or e.metaExpedient.id in (:metaExpedientIdPermesos1) " +
			"			or e.metaExpedient.id in (:metaExpedientIdPermesos2) " +
			"			or e.metaExpedient.id in (:metaExpedientIdPermesos3))) " +
			"     or (:esNullOrganIdPermesos = false and (e.organGestor.id in (:organIdPermesos0) " +
			"			or e.organGestor.id in (:organIdPermesos1) " +
			"			or e.organGestor.id in (:organIdPermesos2) " +
			"			or e.organGestor.id in (:organIdPermesos3))) " +
			"     or (:esNullOrganIdPermesos = false and (eogpmeogog.id in (:organIdPermesos0) " +
			"			or eogpmeogog.id in (:organIdPermesos1) " +
			"			or eogpmeogog.id in (:organIdPermesos2) " +
			"			or eogpmeogog.id in (:organIdPermesos3))) " +
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
			"and (:isAdmin = true or (e.grup is null or (:esNullIdsGrupsPermesos = false and e.grup.id in (:idsGrupsPermesos)))) " +
			"and (:esFiltrarExpedientsAmbFirmaPendent != true " + 
			"		or e.id in (" + 
			"			select dp.expedient.id " + 
			"			from DocumentPortafirmesEntity dp " + 
			"			where (dp.estat = es.caib.ripea.core.api.dto.DocumentEnviamentEstatEnumDto.PENDENT or " + 
			"				   dp.estat = es.caib.ripea.core.api.dto.DocumentEnviamentEstatEnumDto.ENVIAT)" + 
			"				   and dp.error = false)) " +
			"and (:esNullNumeroRegistre = true " +
			"		or lower(e.registresImportats) like lower('%'||:numeroRegistre||'%')) " +
			"and (:esNullGrup = true or e.grup = :grup) "
			)
	List<Long> findIdsByEntitatAndFiltre(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullMetaExpedientIdPermesos") boolean esNullMetaExpedientIdPermesos, 
			@Param("metaExpedientIdPermesos0") List<Long> metaExpedientIdPermesos0,
			@Param("metaExpedientIdPermesos1") List<Long> metaExpedientIdPermesos1,
			@Param("metaExpedientIdPermesos2") List<Long> metaExpedientIdPermesos2,
			@Param("metaExpedientIdPermesos3") List<Long> metaExpedientIdPermesos3,
			@Param("esNullOrganIdPermesos") boolean esNullOrganIdPermesos,
			@Param("organIdPermesos0") List<Long> organIdPermesos0,
			@Param("organIdPermesos1") List<Long> organIdPermesos1,
			@Param("organIdPermesos2") List<Long> organIdPermesos2,
			@Param("organIdPermesos3") List<Long> organIdPermesos3,
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
			@Param("esNullIdsGrupsPermesos") boolean esNullIdsGrupsPermesos,
			@Param("idsGrupsPermesos") List<Long> idsGrupsPermesos,
			@Param("isAdmin") boolean isAdmin,
			@Param("esFiltrarExpedientsAmbFirmaPendent") boolean esFiltrarExpedientsAmbFirmaPendent,
			@Param("esNullNumeroRegistre") boolean esNullNumeroRegistre,
			@Param("numeroRegistre") String numeroRegistre,
			@Param("esNullGrup") boolean esNullGrup,
			@Param("grup") GrupEntity grup);
	

	static final String FIND_BY_RELACIONATS = "select " +
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
			"and (e.id in (:expedientsRelacionatsIdx)) ";

	@Query(FIND_BY_RELACIONATS)
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

	@Query(FIND_BY_RELACIONATS)
	List<ExpedientEntity> findExpedientsRelacionatsByIdIn(
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
			@Param("expedientsRelacionatsIdx") Collection<Long> ids);


	@Query(	"select e " +
			"from ExpedientEntity e " +
			"where e.entitat = :entitat " +
			"and e.id in (:ids)")
	List<ExpedientEntity> findByEntitatAndIdInOrderByIdAsc(
			@Param("entitat") EntitatEntity entitat,
			@Param("ids") Collection<Long> id);
	

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

	static final String FIND_BY_CANVI_ESTAT_MASSIU = "select " +
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
			"and (:esNullExpedient = true or e = :expedient) " +
			"and (:esNullDataInici = true or e.createdDate >= :dataInici) " +
			"and (:esNullDataFi = true or e.createdDate <= :dataFi) " +
			"and (:esNullEstatEnum = true or (e.estat = :estatEnum and (e.estatAdditional is null or :esNullMetaExpedient = true))) " +
			"and (:esNullEstat = true or e.estatAdditional = :estat) ";

	@Query(FIND_BY_CANVI_ESTAT_MASSIU)
	public Page<ExpedientEntity> findExpedientsPerCanviEstatMassiu(
			@Param("entitat") EntitatEntity entitat,
			@Param("nomesAgafats") boolean nomesAgafats,
			@Param("usuariActual") UsuariEntity usuariActual,
			@Param("metaExpedientsPermesos") List<? extends MetaNodeEntity> metaExpedientsPermesos,
			@Param("esNullMetaExpedient") boolean esNullMetaExpedient,
			@Param("metaExpedient") MetaNodeEntity metaExpedient,	
			@Param("esNullExpedient") boolean esNullExpedient,
			@Param("expedient") ExpedientEntity expedient,
			@Param("esNullDataInici") boolean esNullDataInici,
			@Param("dataInici") Date dataInici,
			@Param("esNullDataFi") boolean esNullDataFi,
			@Param("dataFi") Date dataFi,
			@Param("esNullEstatEnum") boolean esNullEstatEnum,
			@Param("estatEnum") ExpedientEstatEnumDto estatEnum,
			@Param("esNullEstat") boolean esNullEstat,
			@Param("estat") ExpedientEstatEntity estat,
			Pageable pageable);

	@Query(FIND_BY_CANVI_ESTAT_MASSIU)
	public List<ExpedientEntity> findExpedientsPerCanviEstatMassiu(
			@Param("entitat") EntitatEntity entitat,
			@Param("nomesAgafats") boolean nomesAgafats,
			@Param("usuariActual") UsuariEntity usuariActual,
			@Param("metaExpedientsPermesos") List<? extends MetaNodeEntity> metaExpedientsPermesos,
			@Param("esNullMetaExpedient") boolean esNullMetaExpedient,
			@Param("metaExpedient") MetaNodeEntity metaExpedient,
			@Param("esNullExpedient") boolean esNullExpedient,
			@Param("expedient") ExpedientEntity expedient,
			@Param("esNullDataInici") boolean esNullDataInici,
			@Param("dataInici") Date dataInici,
			@Param("esNullDataFi") boolean esNullDataFi,
			@Param("dataFi") Date dataFi,
			@Param("esNullEstatEnum") boolean esNullEstatEnum,
			@Param("estatEnum") ExpedientEstatEnumDto estatEnum,
			@Param("esNullEstat") boolean esNullEstat,
			@Param("estat") ExpedientEstatEntity estat);

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
			"and (:esNullExpedient = true or e = :expedient) " +
			"and (:esNullDataInici = true or e.createdDate >= :dataInici) " +
			"and (:esNullDataFi = true or e.createdDate <= :dataFi) " +
			"and (:esNullEstatEnum = true or (e.estat = :estatEnum and (e.estatAdditional is null or :esNullMetaExpedient = true))) " +
			"and (:esNullEstat = true or e.estatAdditional = :estat) ")
	public List<Long> findIdsExpedientsPerCanviEstatMassiu(
			@Param("entitat") EntitatEntity entitat,
			@Param("nomesAgafats") boolean nomesAgafats,
			@Param("usuariActual") UsuariEntity usuariActual,
			@Param("metaExpedientsPermesos") List<? extends MetaNodeEntity> metaExpedientsPermesos,
			@Param("esNullMetaExpedient") boolean esNullMetaExpedient,
			@Param("metaExpedient") MetaNodeEntity metaExpedient,	
			@Param("esNullExpedient") boolean esNullExpedient,
			@Param("expedient") ExpedientEntity expedient,
			@Param("esNullDataInici") boolean esNullDataInici,
			@Param("dataInici") Date dataInici,
			@Param("esNullDataFi") boolean esNullDataFi,
			@Param("dataFi") Date dataFi,
			@Param("esNullEstatEnum") boolean esNullEstatEnum,
			@Param("estatEnum") ExpedientEstatEnumDto estatEnum,
			@Param("esNullEstat") boolean esNullEstat,
			@Param("estat") ExpedientEstatEntity estat);


	static final String FIND_BY_TANCAMENT_MASSIU = "select " +
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
			"			and metaDocument.id not in (select doc.metaNode.id from DocumentEntity doc where e.id = doc.expedient.id)) = 0";

	@Query(FIND_BY_TANCAMENT_MASSIU)
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

	@Query(FIND_BY_TANCAMENT_MASSIU)
	public List<ExpedientEntity> findExpedientsPerTancamentMassiu(
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
			"and (select count(document) from DocumentEntity document where " + // no documents en process de firma
			"	document.expedient = e " +
			"	and (document.estat = es.caib.ripea.core.api.dto.DocumentEstatEnumDto.FIRMA_PENDENT " +
			"		or document.estat = es.caib.ripea.core.api.dto.DocumentEstatEnumDto.FIRMA_PENDENT_VIAFIRMA " +
			"		or document.estat = es.caib.ripea.core.api.dto.DocumentEstatEnumDto.FIRMA_PARCIAL)) = 0 " +
			"and (select count(document) from DocumentEntity document where document.expedient = e and document.esborrat = 0) > 0 " +   // at least one document no esborrat
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


	static final String FIND_BY_ARXIU_PENDENT =	"select " +
			"    e " +
			"from " +
			"    ExpedientEntity e " +
			"where " +
			"	 e.arxiuUuid = null " +
			"and e.esborrat = 0 " +
			"and e.estat = es.caib.ripea.core.api.dto.ExpedientEstatEnumDto.OBERT " +
			"and e.entitat = :entitat " +
			"and (e.metaNode in (:metaExpedientsPermesos)) " +
			"and (:nomesAgafats = false or e.agafatPer.codi = :usuariActual) " +
			"and (:esNullNom = true or lower(e.nom) like lower('%'||:nom||'%')) " +
			"and (:esNullMetaExpedient = true or e.metaExpedient = :metaExpedient) " +
			"and (:esNullCreacioInici = true or e.createdDate >= :creacioInici) " +
			"and (:esNullCreacioFi = true or e.createdDate <= :creacioFi)";

	@Query(FIND_BY_ARXIU_PENDENT)
	public Page<ExpedientEntity> findArxiuPendents(
			@Param("entitat") EntitatEntity entitat,
			@Param("metaExpedientsPermesos") List<? extends MetaNodeEntity> metaExpedientsPermesos,
			@Param("nomesAgafats") boolean nomesAgafats,
			@Param("usuariActual") String usuariActual,
			@Param("esNullNom") boolean esNullNom,
			@Param("nom") String nom,
			@Param("esNullMetaExpedient") boolean esNullMetaExpedient,
			@Param("metaExpedient") MetaExpedientEntity metaExpedient,
			@Param("esNullCreacioInici") boolean esNullCreacioInici,
			@Param("creacioInici") Date creacioInici,
			@Param("esNullCreacioFi") boolean esNullCreacioFi,
			@Param("creacioFi") Date creacioFi,
			Pageable pageable);

	@Query(FIND_BY_ARXIU_PENDENT)
	public List<ExpedientEntity> findArxiuPendents(
			@Param("entitat") EntitatEntity entitat,
			@Param("metaExpedientsPermesos") List<? extends MetaNodeEntity> metaExpedientsPermesos,
			@Param("nomesAgafats") boolean nomesAgafats,
			@Param("usuariActual") String usuariActual,
			@Param("esNullNom") boolean esNullNom,
			@Param("nom") String nom,
			@Param("esNullMetaExpedient") boolean esNullMetaExpedient,
			@Param("metaExpedient") MetaExpedientEntity metaExpedient,
			@Param("esNullCreacioInici") boolean esNullCreacioInici,
			@Param("creacioInici") Date creacioInici,
			@Param("esNullCreacioFi") boolean esNullCreacioFi,
			@Param("creacioFi") Date creacioFi);
	
	
	@Query(	"select " +
			"    e.id " +
			"from " +
			"    ExpedientEntity e " +
			"where " +
			"	 e.arxiuUuid = null " +
			"and e.estat = es.caib.ripea.core.api.dto.ExpedientEstatEnumDto.OBERT " +
			"and e.esborrat = 0 " +
			"and e.entitat = :entitat " +
			"and (e.metaNode in (:metaExpedientsPermesos)) " +
			"and (:nomesAgafats = false or e.agafatPer.codi = :usuariActual) " +			
			"and (:esNullNom = true or lower(e.nom) like lower('%'||:nom||'%')) " +
			"and (:esNullMetaExpedient = true or e.metaExpedient = :metaExpedient) " + 
			"and (:esNullCreacioInici = true or e.createdDate >= :creacioInici) " +
			"and (:esNullCreacioFi = true or e.createdDate <= :creacioFi) ")
	public List<Long> findIdsArxiuPendents(
			@Param("entitat") EntitatEntity entitat,
			@Param("metaExpedientsPermesos") List<? extends MetaNodeEntity> metaExpedientsPermesos,
			@Param("nomesAgafats") boolean nomesAgafats,
			@Param("usuariActual") String usuariActual,
			@Param("esNullNom") boolean esNullNom,
			@Param("nom") String nom,
			@Param("esNullMetaExpedient") boolean esNullMetaExpedient,
			@Param("metaExpedient") MetaExpedientEntity metaExpedient,
			@Param("esNullCreacioInici") boolean esNullCreacioInici,
			@Param("creacioInici") Date creacioInici,
			@Param("esNullCreacioFi") boolean esNullCreacioFi,
			@Param("creacioFi") Date creacioFi);	


	@Query(	"select " +
			"    distinct e " +
			"from " +
			"    ExpedientEntity e " +
			"    left join e.metaexpedientOrganGestorPares meogp " +
			"where " +
			"    e.esborrat = 0 " +
			"and e.entitat = :entitat " +
			"and (" +
			"     (:esNullIdsMetaExpedientsPermesos = false and (e.metaExpedient.id in (:idsMetaExpedientsPermesos0) " +
			"			or e.metaExpedient.id in (:idsMetaExpedientsPermesos1) " +
			"			or e.metaExpedient.id in (:idsMetaExpedientsPermesos2) " +
			"			or e.metaExpedient.id in (:idsMetaExpedientsPermesos3))) " +
			"     or (:esNullIdsOrgansPermesos = false and (meogp.organGestor.id in (:idsOrgansPermesos0) " +
			"			or meogp.organGestor.id in (:idsOrgansPermesos1) " +
			"			or meogp.organGestor.id in (:idsOrgansPermesos2) " +
			"			or meogp.organGestor.id in (:idsOrgansPermesos3)))" +
			"     or (:esNullIdsMetaExpedientOrganPairsPermesos = false and meogp.id in (:idsMetaExpedientOrganPairsPermesos)) " +
			"     or (:esNullIdsOrgansAmbProcedimentsComunsPermesos = false and meogp.organGestor.id in (:idsOrgansAmbProcedimentsComunsPermesos) and e.metaExpedient.id in (:idsProcedimentsComuns))) " +
		//TODO if organ is in :idsOrgansAmbProcedimentsComunsPermesos it is also already in :idsOrgansPermesos as well so check :idsOrgansAmbProcedimentsComunsPermesos doesn't do anything, probably :idsOrgansPermesos check should be only allowed for procediments no comuns
			"and (lower(e.nom) like lower('%'||:text||'%') or lower(e.numero) like lower('%'||:text||'%')) " +
			"and (:isAdmin = true or (e.grup is null or (:esNullRolsCurrentUser = false and e.grup in (select grup from GrupEntity grup where grup.rol in (:rolsCurrentUser))))) " +
			"and (:esNullMetaExpedient = true or e.metaExpedient = :metaExpedient) "
			)
	List<ExpedientEntity> findByTextAndFiltre(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullIdsMetaExpedientsPermesos") boolean esNullIdsMetaExpedientsPermesos, 
			@Param("idsMetaExpedientsPermesos0") List<Long> idsMetaExpedientsPermesos0,
			@Param("idsMetaExpedientsPermesos1") List<Long> idsMetaExpedientsPermesos1,
			@Param("idsMetaExpedientsPermesos2") List<Long> idsMetaExpedientsPermesos2,
			@Param("idsMetaExpedientsPermesos3") List<Long> idsMetaExpedientsPermesos3,
			@Param("esNullIdsOrgansPermesos") boolean esNullIdsOrgansPermesos,
			@Param("idsOrgansPermesos0") List<Long> idsOrgansPermesos0,
			@Param("idsOrgansPermesos1") List<Long> idsOrgansPermesos1,
			@Param("idsOrgansPermesos2") List<Long> idsOrgansPermesos2,
			@Param("idsOrgansPermesos3") List<Long> idsOrgansPermesos3,
			@Param("esNullIdsMetaExpedientOrganPairsPermesos") boolean esNullIdsMetaExpedientOrganPairsPermesos,
			@Param("idsMetaExpedientOrganPairsPermesos") List<Long> idsMetaExpedientOrganPairsPermesos,
			@Param("esNullIdsOrgansAmbProcedimentsComunsPermesos") boolean esNullIdsOrgansAmbProcedimentsComunsPermesos, 
			@Param("idsOrgansAmbProcedimentsComunsPermesos") List<Long> idsOrgansAmbProcedimentsComunsPermesos,
			@Param("idsProcedimentsComuns") List<Long> idsProcedimentsComuns,
			@Param("text") String text,
			@Param("esNullRolsCurrentUser") boolean esNullRolsCurrentUser,
			@Param("rolsCurrentUser") List<String> rolsCurrentUser,
			@Param("isAdmin") boolean isAdmin,
			@Param("esNullMetaExpedient") boolean esNullMetaExpedient,
			@Param("metaExpedient") MetaExpedientEntity metaExpedient);
	

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
			"	left outer join e.organGestor og " +
			"where " +
			"e.esborrat = 0 " +
			"and e.entitat = :entitat " +
			"and og.codi in (:organsCodisPermitted) " +
			"and e.metaNode = :metaNode ORDER BY e.nom DESC")
	List<ExpedientEntity> findByEntitatAndMetaExpedientAndOrgans(
			@Param("entitat") EntitatEntity entitat,
			@Param("organsCodisPermitted") List<String> organsCodisPermitted,
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

	@Modifying
	@Query(value = "update ipa_expedient set prioritat = :prioritat where id in (:expedientsId)", nativeQuery = true)
    void updatePrioritats(
			@Param("expedientsId") Collection<Long> expedientsId,
			@Param("prioritat") String  prioritat);
}
