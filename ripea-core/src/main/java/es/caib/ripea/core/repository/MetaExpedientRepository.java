/**
 * 
 */
package es.caib.ripea.core.repository;

import es.caib.ripea.core.api.dto.MetaExpedientRevisioEstatEnumDto;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.OrganGestorEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus meta-expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface MetaExpedientRepository extends JpaRepository<MetaExpedientEntity, Long> {

	MetaExpedientEntity findByEntitatAndCodi(EntitatEntity entitat, String codi);

	List<MetaExpedientEntity> findByEntitat(EntitatEntity entitat);
	
	@Query( "select " +
			"	me.id " +
			"from " +
			"    MetaExpedientEntity me " +
			"where " +
			"  me.organGestor in (:organGestors)")
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
	
	@Query(	"select me from " +
			"    MetaExpedientEntity me left join me.organGestor org " +
			"where " +
			"    me.entitat = :entitat " +
			"and (:esNullCodi = true or lower(me.codi) like lower('%'||:codi||'%')) " +
			"and (:esNullNom = true or lower(me.nom) like lower('%'||:nom||'%')) " +
			"and (:esNullClassificacio = true or lower(me.classificacio) like lower('%'||:classificacio||'%')) " +
			"and (:esNullActiu = true or me.actiu = :actiu) " +
			"and (:esNullAmbit = true or ((:comuns = true and me.organGestor = null) or (:comuns = false  and me.organGestor != null)) ) " +
			"and (:esNullOrganGestor = true or me.organGestor = :organGestor)" + 
			"and (:esNullRevisioEstat = true or me.revisioEstat IN (:revisioEstats)) ")
	Page<MetaExpedientEntity> findByEntitat(
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
			Pageable pageable);

	@Query( "from " +
			"    MetaExpedientEntity me " +
			"where " +
			"    me.entitat = :entitat " +
			"and (:esNullCodi = true or lower(me.codi) like lower('%'||:codi||'%')) " +
			"and (:esNullNom = true or lower(me.nom) like lower('%'||:nom||'%')) " +
			"and (:esNullClassificacio = true or lower(me.classificacio) like lower('%'||:classificacio||'%')) " +
			"and (:esNullActiu = true or me.actiu = :actiu) " +
			"and (:esNullOrganGestor = true or me.organGestor = :organGestor) " +
			"and me.id in (:ids)" + 
			"and (:esNullRevisioEstat = true or me.revisioEstat = :revisioEstat) ")
	Page<MetaExpedientEntity> findByOrganGestor(
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
			@Param("ids") List<Long> ids,
			@Param("esNullRevisioEstat") boolean esNullRevisioEstat,
			@Param("revisioEstat") MetaExpedientRevisioEstatEnumDto revisioEstat,
			Pageable pageable);

	@Query(	"select" +
			"    distinct e.metaExpedient.id " +
			"from" +
			"    ExpedientEntity e " +
			"where " +
			"    e.id in (:ids) ")
	List<Long> findDistinctMetaExpedientIdsByExpedients(
			@Param("ids") Collection<Long> ids);

//	Sembla que necessita com a mínim spring 4.0 --> Ho deixam preparat per quan passem a jboss7
//	===========================================
//	@Query( "select " +
//			"    distinct me " +
//			"from " +
//			"    MetaExpedientEntity me " +
//			"	left join me.metaExpedientOrganGestors meog " +
//			"	left join me.organGestor og " +
//			"where " +
//			"    me.entitat = :#{#filtre.entitat} " +
//			"and (:#{#filtre.esNullActiu} = true or me.actiu = :#{#filtre.actiu}) " +
//			"and (:#{#filtre.revisioActiva} = false or me.revisioEstat = 'REVISAT') " +
//			"and (:#{#filtre.esNullFiltre} = true or lower(me.nom) like lower('%'||:#{#filtre.filtre}||'%') or lower(me.classificacio) like lower('%'||:#{#filtre.filtre}||'%')) " +
//			"and (:#{#filtre.organGestorIComu} = false or (og = :#{#filtre.organ} or og is null)) " +
//			"and (:#{#filtre.esAdminEntitat} = true " +
//			" 	  or (:#{#filtre.esAdminOrgan} = true and :#{#filtre.esNullOrganCodiPermesos} = false " +
//			"			and ( og.codi in (:#{#filtre.organCodiPermesosSplit[0]}) " +
//			"				or og.codi in (:#{#filtre.organCodiPermesosSplit[1]}) " +
//			"				or og.codi in (:#{#filtre.organCodiPermesosSplit[2]}) " +
//			"				or og.codi in (:#{#filtre.organCodiPermesosSplit[3]})) " +
//			"     or (:#{#filtre.esNullMetaExpedientIdPermesos} = false " +
//			"			and ( me.id in (:#{#filtre.metaExpedientIdPermesosSplit[0]}) " +
//			"				or me.id in (:#{#filtre.metaExpedientIdPermesosSplit[1]}) " +
//			"				or me.id in (:#{#filtre.metaExpedientIdPermesosSplit[2]}) " +
//			"				or me.id in (:#{#filtre.metaExpedientIdPermesosSplit[3]}))) " +
//			"     or (og is not null and :#{#filtre.esNullOrganCodiPermesos} = false " +
//			"			and ( og.codi in (:#{#filtre.organCodiPermesosSplit[0]})" +
//			"				or og.codi in (:#{#filtre.organCodiPermesosSplit[1]}) " +
//			"				or og.codi in (:#{#filtre.organCodiPermesosSplit[2]}) " +
//			"				or og.codi in (:#{#filtre.organCodiPermesosSplit[3]}))) " +
//			"     or (og is null and :#{#filtre.esNullMetaExpedientOrganIdPermesos} = false " +
//			"			and ( meog.id in (:#{#filtre.metaExpedientOrganIdPermesosSplit[0]}) " +
//			"				or meog.id in (:#{#filtre.metaExpedientOrganIdPermesosSplit[1]}) " +
//			"				or meog.id in (:#{#filtre.metaExpedientOrganIdPermesosSplit[2]}) " +
//			"				or meog.id in (:#{#filtre.metaExpedientOrganIdPermesosSplit[3]}))) " +
//			"	  or (:#{#filtre.allComuns} = true and og is null)))")
//	List<MetaExpedientEntity> findByEntitatAndActiuAndFiltreAndPermes(MetaExpedientFiltre filtre);

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
			"	  		or (:allComuns = true and og is null)))")
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
			"and (me.revisioEstat = :revisioEstat) ")
	List<MetaExpedientEntity> findByRevisioEstat(
			@Param("entitat") EntitatEntity entitat, 
			@Param("revisioEstat") MetaExpedientRevisioEstatEnumDto revisioEstat);

	List<MetaExpedientEntity> findByEntitatOrderByNomAsc(EntitatEntity entitat);

	List<MetaExpedientEntity> findByEntitatAndActiuTrueOrderByNomAsc(EntitatEntity entitat);

	List<MetaExpedientEntity> findByEntitatAndClassificacio(EntitatEntity entitat, String classificacio);
	
	@Query( "select " +
			"   me.id " +
			"from " +
			"    MetaExpedientEntity me " +
			"where " +
			"    me.entitat = :entitat " +
			"and me.organGestor is null " +
			"and me.actiu = true")
	List<Long> findProcedimentsComunsActiveIds(
			@Param("entitat") EntitatEntity entitat);
	
	@Query( "select " +
			"   me " +
			"from " +
			"    MetaExpedientEntity me " +
			"where " +
			"    me.entitat = :entitat " +
			"and me.organGestor is null " +
			"and me.actiu = true")
	List<MetaExpedientEntity> findProcedimentsComunsActive(
			@Param("entitat") EntitatEntity entitat);

	@Query(	"select " +
			"   me " +
			"from" +
			"    MetaExpedientEntity me " +
			"	left outer join me.organGestor og " +
			"where " +
			"	 me.entitat = :entitat " +
			"and og.codi in (:organGestorCodis) ")
	List<MetaExpedientEntity> findByOrganGestorCodis(
			@Param("entitat") EntitatEntity entitat,
			@Param("organGestorCodis") List<String> organGestorCodis);
	
	
	@Query(	"select" +
			"    me.id " +
			"from" +
			"    MetaExpedientEntity me " +
			" where " +
			"    (me.entitat = :entitat) ")
	public List<Long> findAllIdsByEntitat(@Param("entitat") EntitatEntity entitat);
	
	@Query(	"select" +
			"    me " +
			"from" +
			"    MetaExpedientEntity me " +
			" where " +
			"    me.entitat = :entitat " + 
			"and me.actiu = true " + 
			"and (:isAdmin = true or me.id in (:ids))")
	public List<MetaExpedientEntity> findMetaExpedientsByIds(	
			@Param("entitat") EntitatEntity entitat, 
			@Param("ids") List<Long> ids, 
			@Param("isAdmin") boolean isAdmin);


	@Query(	"select count(me.id) from MetaExpedientEntity me where me.organGestor = :organGestor")
	Integer countByOrganGestor(@Param("organGestor") OrganGestorEntity organGestor);
}
