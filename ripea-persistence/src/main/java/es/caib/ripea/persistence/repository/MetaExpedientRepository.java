package es.caib.ripea.persistence.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.persistence.entity.OrganGestorEntity;
import es.caib.ripea.service.intf.dto.MetaExpedientRevisioEstatEnumDto;
import es.caib.ripea.service.intf.dto.TipusProcedimentServeiEnum;

@Component
public interface MetaExpedientRepository extends JpaRepository<MetaExpedientEntity, Long> {

	MetaExpedientEntity findByEntitatAndCodi(EntitatEntity entitat, String codi);

	@Query("SELECT e.codi FROM MetaExpedientEntity e ORDER BY e.codi")
	List<String> findCodisOrdenats();
	
	@Query( "select " +
			"	me.id " +
			"from " +
			"    MetaExpedientEntity me " +
			"where " +
			"  me.organGestor in (:organGestors) order by me.nom ASC")
	List<Long> findByOrgansGestors(@Param("organGestors") List<OrganGestorEntity> organGestors);

	@Query(	"from " +
			"    MetaExpedientEntity me " +
			"where " +
			"    me.entitat = :entitat " +
			"and (:esNullCodi = true or lower(me.codi) like lower('%'||:codi||'%')) " +
			"and (:esNullNom = true or lower(me.nom) like lower('%'||:nom||'%')) " +
			"and (:esNullClassificacio = true or lower(me.classificacio) like lower('%'||:classificacio||'%')) " +
			"and (:esNullActiu = true or me.actiu = :actiu) " +
			"and (:esNullAmbit = true or ((:comuns = true and me.organGestor = null) or (:comuns = false  and me.organGestor != null)) ) " +
			"and (:esNullOrganGestor = true or me.organGestor = :organGestor)" + 
			"and (:esNullRevisioEstat = true or me.revisioEstat IN (:revisioEstats)) ")
	List<MetaExpedientEntity> findByEntitat(
			@Param("entitat") EntitatEntity entitat, 
			@Param("esNullCodi") boolean esNullCodi,
			@Param("codi") String codi,
			@Param("esNullNom") boolean esNullNom,
			@Param("nom") String nom,
			@Param("esNullClassificacio") boolean esNullClassificacio,
			@Param("classificacio") String classificacio,
			@Param("esNullActiu") boolean esNullActiu,
			@Param("actiu") Boolean actiu,
			@Param("esNullOrganGestor") boolean esNullOrganGestor,
			@Param("organGestor") OrganGestorEntity organGestor,
			@Param("esNullAmbit") boolean esNullAmbit,
			@Param("comuns") boolean comuns,
			@Param("esNullRevisioEstat") boolean esNullRevisioEstat,
			@Param("revisioEstats") MetaExpedientRevisioEstatEnumDto[] revisioEstats,
			Sort sort);
	
	@Query(	"select me.id from " +
			"    MetaExpedientEntity me left join me.metaExpedientOrganGestors meogp " +
			"where " +
			"    me.entitat = :entitat " +
			"and (" +
			"     (:esNullIdsMetaExpedientsPermesos = false and (me.id in (:idsMetaExpedientsPermesos0)" +
			"			or me.id in (:idsMetaExpedientsPermesos1)" +
			"			or me.id in (:idsMetaExpedientsPermesos2)" +
			"			or me.id in (:idsMetaExpedientsPermesos3))) " +
			"     or (:esNullIdsOrgansPermesos = false and (meogp.organGestor.id in (:idsOrgansPermesos0)" +
			"			or meogp.organGestor.id in (:idsOrgansPermesos1)" +
			"			or meogp.organGestor.id in (:idsOrgansPermesos2)" +
			"			or meogp.organGestor.id in (:idsOrgansPermesos3))) " +
			"     or (:esNullIdsMetaExpedientOrganPairsPermesos = false and meogp.id in (:idsMetaExpedientOrganPairsPermesos)) " +
			"     or (:esNullIdsOrgansAmbProcedimentsComunsPermesos = false and meogp.organGestor.id in (:idsOrgansAmbProcedimentsComunsPermesos) and me.id in (:idsProcedimentsComuns))) " +
			// Un cop superada la select anterior, es procedeix a afinar per permisos per procediment:
			// - Per admin i superadmin: es compleix la primera condicio = No filtra
			// - Per la resta: el procediment no ha de requerir permis directe o en cas contrari, s'ha de tenir el permis directe de lectura.
			"and (:isAdmin = true or :esNullIdsMetaExpedientsPermesos = true or me.permisDirecte = false or ("+ 
					"				me.id in (:idsMetaExpedientsPermesos0)" +
					"			or	me.id in (:idsMetaExpedientsPermesos1)" +
					"			or	me.id in (:idsMetaExpedientsPermesos2)" +
					"			or	me.id in (:idsMetaExpedientsPermesos3)))")
	List<Long> findMetaExpedientsPermesos(
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
			@Param("isAdmin") boolean isAdmin);
	
	@Query(	"select me from " +
			"    MetaExpedientEntity me left join me.organGestor org " +
			"where " +
			"    me.entitat = :entitat " +
			"and (:esNullTipus = true or me.tipusProcedimentServei = :tipusProcedimentServei) " +
			"and (:esNullCodi = true or lower(me.codi) like lower('%'||:codi||'%')) " +
			"and (:esNullNom = true or lower(me.nom) like lower('%'||:nom||'%')) " +
			"and (:esNullClassificacio = true or lower(me.classificacio) like lower('%'||:classificacio||'%')) " +
			"and (:esNullActiu = true or me.actiu = :actiu) " +
			"and (:permisDirecte = false or me.permisDirecte=true)" +
			"and (:esNullAmbit = true or ((:comuns = true and me.organGestor = null) or (:comuns = false  and me.organGestor != null)) ) " +
			"and (:esNullOrganGestor = true or me.organGestor = :organGestor)" + 
			"and (:esNullRevisioEstat = true or me.revisioEstat IN (:revisioEstats)) ")
	Page<MetaExpedientEntity> findByEntitat(
			@Param("entitat") EntitatEntity entitat, 
			@Param("esNullTipus") boolean esNullTipus,
			@Param("tipusProcedimentServei") TipusProcedimentServeiEnum tipusProcedimentServei,				
			@Param("esNullCodi") boolean esNullCodi,
			@Param("codi") String codi,
			@Param("esNullNom") boolean esNullNom,
			@Param("nom") String nom,
			@Param("esNullClassificacio") boolean esNullClassificacio,
			@Param("classificacio") String classificacio,
			@Param("esNullActiu") boolean esNullActiu,
			@Param("actiu") Boolean actiu,
			@Param("esNullOrganGestor") boolean esNullOrganGestor,
			@Param("organGestor") OrganGestorEntity organGestor,	
			@Param("esNullAmbit") boolean esNullAmbit,
			@Param("comuns") boolean comuns,
			@Param("esNullRevisioEstat") boolean esNullRevisioEstat,
			@Param("revisioEstats") MetaExpedientRevisioEstatEnumDto[] revisioEstats,
			@Param("permisDirecte") boolean permisDirecte,
			Pageable pageable);

	@Query( "from " +
			"    MetaExpedientEntity me " +
			"where " +
			"    me.entitat = :entitat " +
			"and (:esNullCodi = true or lower(me.codi) like lower('%'||:codi||'%')) " +
			"and (:esNullNom = true or lower(me.nom) like lower('%'||:nom||'%')) " +
			"and (:esNullClassificacio = true or lower(me.classificacio) like lower('%'||:classificacio||'%')) " +
			"and (:esNullActiu = true or me.actiu = :actiu) " +
			"and (:esNullTipus = true or me.tipusProcedimentServei = :tipusProcedimentServei) " +
			"and (:esNullOrganGestor = true or me.organGestor = :organGestor) " +
			"and (:permisDirecte = false or me.permisDirecte=true)" +
			"and me.id in (:ids)" + 
			"and (:esNullRevisioEstat = true or me.revisioEstat = :revisioEstat) ")
	Page<MetaExpedientEntity> findByOrganGestor(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullTipus") boolean esNullTipus,
			@Param("tipusProcedimentServei") TipusProcedimentServeiEnum tipusProcedimentServei,			
			@Param("esNullCodi") boolean esNullCodi,
			@Param("codi") String codi,
			@Param("esNullNom") boolean esNullNom,
			@Param("nom") String nom,
			@Param("esNullClassificacio") boolean esNullClassificacio,
			@Param("classificacio") String classificacio,
			@Param("esNullActiu") boolean esNullActiu,
			@Param("actiu") Boolean actiu,
			@Param("esNullOrganGestor") boolean esNullOrganGestor,
			@Param("organGestor") OrganGestorEntity organGestor,
			@Param("ids") List<Long> ids,
			@Param("esNullRevisioEstat") boolean esNullRevisioEstat,
			@Param("revisioEstat") MetaExpedientRevisioEstatEnumDto revisioEstat,
			@Param("permisDirecte") boolean permisDirecte,
			Pageable pageable);

	@Query( "select distinct me " +
			"from MetaExpedientEntity me " +
			"	left outer join me.metaExpedientOrganGestors meog " +
			"	left outer join me.organGestor og " +
			"where me.entitat = :entitat " +
			"	and (:esNullActiu = true or me.actiu = :actiu) " +
			"	and (:revisioActiva = false or me.revisioEstat = 'REVISAT') " +
			"	and (:esNullFiltre = true or lower(me.nom) like lower('%'||:filtre||'%') or lower(me.classificacio) like lower('%'||:filtre||'%')) " +
			"	and (:organGestorIComu = false or (og is null or og = :organ)) " +
			"	and (:esAdminEntitat = true or ( " +
//			"			(:esAdminOrgan = true and :esNullOrganCodiPermesos = false and og is not null and ( og.codi in (:organCodiPermesos0) )) " +
//			"				or og.codi in (:organCodiPermesos1) " +
//			"				or og.codi in (:organCodiPermesos2) " +
//			"				or og.codi in (:organCodiPermesos3)) " +
			"     		(:esNullMetaExpedientIdPermesos = false and ( me.id in (:metaExpedientIdPermesos0) " +
			"						or me.id in (:metaExpedientIdPermesos1) " +
			"						or me.id in (:metaExpedientIdPermesos2) " +
			"						or me.id in (:metaExpedientIdPermesos3))) " +
			"     		or (og is not null and :esNullOrganCodiPermesos = false and ( og.codi in (:organCodiPermesos0) " +
			"						or og.codi in (:organCodiPermesos1) " +
			"						or og.codi in (:organCodiPermesos2) " +
			"						or og.codi in (:organCodiPermesos3))) " +
			"     		or (og is null and :esNullMetaExpedientOrganIdPermesos = false and ( meog.id in (:metaExpedientOrganIdPermesos0) " +
			"						or meog.id in (:metaExpedientOrganIdPermesos1) " +
			"						or meog.id in (:metaExpedientOrganIdPermesos2) " +
			"						or meog.id in (:metaExpedientOrganIdPermesos3))) " +
			"	  		or (:allComuns = true and og is null))) order by me.nom ASC")
	List<MetaExpedientEntity> findByEntitatAndActiuAndFiltreAndPermes(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullActiu") boolean esNullActiu,
			@Param("actiu") Boolean actiu,
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,
			@Param("esAdminEntitat") boolean esAdminEntitat,
//			@Param("esAdminOrgan") boolean esAdminOrgan,
			@Param("esNullMetaExpedientIdPermesos") boolean esNullMetaExpedientIdPermesos,
			@Param("metaExpedientIdPermesos0") List<Long> metaExpedientIdPermesos0,
			@Param("metaExpedientIdPermesos1") List<Long> metaExpedientIdPermesos1,
			@Param("metaExpedientIdPermesos2") List<Long> metaExpedientIdPermesos2,
			@Param("metaExpedientIdPermesos3") List<Long> metaExpedientIdPermesos3,
			@Param("esNullOrganCodiPermesos") boolean esNullOrganCodiPermesos,
			@Param("organCodiPermesos0") List<String> organCodiPermesos0,
			@Param("organCodiPermesos1") List<String> organCodiPermesos1,
			@Param("organCodiPermesos2") List<String> organCodiPermesos2,
			@Param("organCodiPermesos3") List<String> organCodiPermesos3,
			@Param("esNullMetaExpedientOrganIdPermesos") boolean esNullMetaExpedientOrganIdPermesos,
			@Param("metaExpedientOrganIdPermesos0") List<Long> metaExpedientOrganIdPermesos0,
			@Param("metaExpedientOrganIdPermesos1") List<Long> metaExpedientOrganIdPermesos1,
			@Param("metaExpedientOrganIdPermesos2") List<Long> metaExpedientOrganIdPermesos2,
			@Param("metaExpedientOrganIdPermesos3") List<Long> metaExpedientOrganIdPermesos3,
			@Param("revisioActiva") boolean revisioActiva,
			@Param("organGestorIComu") boolean organGestorIComu,
			@Param("organ") OrganGestorEntity organ,
			@Param("allComuns") boolean allComuns);


	@Query( "from " +
			"    MetaExpedientEntity me " +
			"where " +
			"    me.organGestor = :organGestor " +
			"and (:esNullActiu = true or me.actiu = :actiu) " +
			"and (:esNullFiltre = true or lower(me.nom) like lower('%'||:filtre||'%') or lower(me.classificacio) like lower('%'||:filtre||'%')) " +
			"order by me.nom asc")
	List<MetaExpedientEntity> findByOrganGestorAndActiuAndFiltreTrueOrderByNomAsc(
			@Param("organGestor") OrganGestorEntity organGestor,
			@Param("esNullActiu") boolean esNullActiu,
			@Param("actiu") Boolean actiu,
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre);
	
	
	@Query(	"from " +
			"    MetaExpedientEntity me " +
			"where " +
			"    me.entitat = :entitat " +
			"and (me.revisioEstat = :revisioEstat) order by me.nom ASC")
	List<MetaExpedientEntity> findByRevisioEstat(
			@Param("entitat") EntitatEntity entitat, 
			@Param("revisioEstat") MetaExpedientRevisioEstatEnumDto revisioEstat);

	List<MetaExpedientEntity> findByEntitatOrderByNomAsc(EntitatEntity entitat);

	List<MetaExpedientEntity> findByEntitatAndActiuTrueOrderByNomAsc(EntitatEntity entitat);

	List<MetaExpedientEntity> findByEntitatAndClassificacioOrderByNomAsc(EntitatEntity entitat, String classificacio);
	
	List<MetaExpedientEntity> findByEntitatAndClassificacioAndTipusProcedimentServeiOrderByNomAsc(
			EntitatEntity entitat,
			String classificacio,
			TipusProcedimentServeiEnum tipusProcedimentServei);
	
	@Query( "select " +
			"   me.id " +
			"from " +
			"    MetaExpedientEntity me " +
			"where " +
			"    me.entitat = :entitat " +
			"and me.organGestor is null " +
			"and me.actiu = true order by me.nom ASC")
	List<Long> findProcedimentsComunsActiveIds(
			@Param("entitat") EntitatEntity entitat);
	
	@Query( "select " +
			"   me " +
			"from " +
			"    MetaExpedientEntity me " +
			"where " +
			"    me.entitat = :entitat " +
			"and me.organGestor is null " +
			"and me.actiu = true order by me.nom ASC")
	List<MetaExpedientEntity> findProcedimentsComunsActive(
			@Param("entitat") EntitatEntity entitat);

	@Query(	"select " +
			"   me " +
			"from" +
			"    MetaExpedientEntity me " +
			"	left outer join me.organGestor og " +
			"where " +
			"	 me.entitat = :entitat " +
			"and og.codi in (:organGestorCodis) order by me.nom ASC")
	List<MetaExpedientEntity> findByOrganGestorCodis(
			@Param("entitat") EntitatEntity entitat,
			@Param("organGestorCodis") List<String> organGestorCodis);

	@Query(	"select" +
			"    me.id " +
			"from" +
			"    MetaExpedientEntity me " +
			" where " +
			"    (me.entitat = :entitat) order by me.nom ASC")
	public List<Long> findAllIdsByEntitat(@Param("entitat") EntitatEntity entitat);
	
	@Query(	"select" +
			"    me " +
			"from" +
			"    MetaExpedientEntity me " +
			" where " +
			"    me.entitat = :entitat " + 
			"and me.actiu = true " + 
			"and (:isAdmin = true or me.id in (:ids)) order by me.nom ASC")
	public List<MetaExpedientEntity> findMetaExpedientsByIds(	
			@Param("entitat") EntitatEntity entitat, 
			@Param("ids") List<Long> ids, 
			@Param("isAdmin") boolean isAdmin);

	@Query(	"select count(me.id) from MetaExpedientEntity me where me.organGestor = :organGestor")
	Integer countByOrganGestor(@Param("organGestor") OrganGestorEntity organGestor);
}