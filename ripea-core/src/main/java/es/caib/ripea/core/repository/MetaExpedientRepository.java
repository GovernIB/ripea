/**
 * 
 */
package es.caib.ripea.core.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.ripea.core.api.dto.MetaExpedientRevisioEstatEnumDto;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.OrganGestorEntity;

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
			"and (:esNullClassificacioSia = true or lower(me.classificacioSia) like lower('%'||:classificacioSia||'%')) " +
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
			@Param("esNullClassificacioSia") boolean esNullClassificacioSia,
			@Param("classificacioSia") String classificacioSia,
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
			"and (:esNullClassificacioSia = true or lower(me.classificacioSia) like lower('%'||:classificacioSia||'%')) " +
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
			@Param("esNullClassificacioSia") boolean esNullClassificacioSia,
			@Param("classificacioSia") String classificacioSia,
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
			"and (:esNullClassificacioSia = true or lower(me.classificacioSia) like lower('%'||:classificacioSia||'%')) " +
			"and (:esNullActiu = true or me.actiu = :actiu) " +
			"and (:esNullOrganGestor = true or me.organGestor = :organGestor) " +
			"and me.id in (:ids)" + 
			"and (:esNullRevisioEstat = true or me.revisioEstat = :revisioEstat) ")
	List<MetaExpedientEntity> findByOrganGestor(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullCodi") boolean esNullCodi,
			@Param("codi") String codi,
			@Param("esNullNom") boolean esNullNom,
			@Param("nom") String nom,
			@Param("esNullClassificacioSia") boolean esNullClassificacioSia,
			@Param("classificacioSia") String classificacioSia,
			@Param("esNullActiu") boolean esNullActiu,
			@Param("actiu") Boolean actiu,
			@Param("esNullOrganGestor") boolean esNullOrganGestor,
			@Param("organGestor") OrganGestorEntity organGestor,
			@Param("ids") List<Long> ids,
			@Param("esNullRevisioEstat") boolean esNullRevisioEstat,
			@Param("revisioEstat") MetaExpedientRevisioEstatEnumDto revisioEstat,
			Sort sort);

	@Query( "from " +
			"    MetaExpedientEntity me " +
			"where " +
			"    me.entitat = :entitat " +
			"and (:esNullCodi = true or lower(me.codi) like lower('%'||:codi||'%')) " +
			"and (:esNullNom = true or lower(me.nom) like lower('%'||:nom||'%')) " +
			"and (:esNullClassificacioSia = true or lower(me.classificacioSia) like lower('%'||:classificacioSia||'%')) " +
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
			@Param("esNullClassificacioSia") boolean esNullClassificacioSia,
			@Param("classificacioSia") String classificacioSia,
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

	@Query( "select " +
			"    distinct me " +
			"from " +
			"    MetaExpedientEntity me left join me.metaExpedientOrganGestors meog " +
			"where " +
			"    me.entitat = :entitat " +
			"and (:esNullActiu = true or me.actiu = :actiu and (:revisioActiva = false or me.revisioEstat = 'REVISAT')) " +
			"and (:esNullFiltre = true or lower(me.nom) like lower('%'||:filtre||'%') or lower(me.classificacioSia) like lower('%'||:filtre||'%')) " +
			"and (:organGestorIComu = false or (me.organGestor = :organ or me.organGestor is null)) " +
			"and ((:esAdminEntitat = true or :esAdminOrgan = true) " +
			"     or (:esNullIdPermesos = false and me.id in (:idPermesos)) " +
			"     or (me.organGestor is not null and :esNullOrganIdPermesos = false and me.organGestor.id in (:organIdPermesos)) " +
			"     or (me.organGestor is null and :esNullMetaExpedientOrganIdPermesos = false and meog.id in (:metaExpedientOrganIdPermesos)) " +
			"	  or (:allComuns = true and me.organGestor is null))")
	List<MetaExpedientEntity> findByEntitatAndActiuAndFiltreAndPermes(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullActiu") boolean esNullActiu,
			@Param("actiu") Boolean actiu,
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,
			@Param("esAdminEntitat") boolean esAdminEntitat,
			@Param("esAdminOrgan") boolean esAdminOrgan,
			@Param("esNullIdPermesos") boolean hiHaIdPermesos,
			@Param("idPermesos") List<Long> idPermesos,
			@Param("esNullOrganIdPermesos") boolean hiHaOrganIdPermesos,
			@Param("organIdPermesos") List<Long> organIdPermesos,
			@Param("esNullMetaExpedientOrganIdPermesos") boolean hiHaMetaExpedientOrganIdPermesos,
			@Param("metaExpedientOrganIdPermesos") List<Long> metaExpedientOrganIdPermesos, 
			@Param("revisioActiva") boolean revisioActiva,
			@Param("organGestorIComu") boolean organGestorIComu,
			@Param("organ") OrganGestorEntity organ,
			@Param("allComuns") boolean allComuns);
	
	
	

	@Query( "from " +
			"    MetaExpedientEntity me " +
			"where " +
			"    me.organGestor = :organGestor " +
			"and (:esNullActiu = true or me.actiu = :actiu) " +
			"and (:esNullFiltre = true or lower(me.nom) like lower('%'||:filtre||'%') or lower(me.classificacioSia) like lower('%'||:filtre||'%')) " +
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

	List<MetaExpedientEntity> findByOrganGestorOrderByNomAsc(OrganGestorEntity organGestorEntity);

	List<MetaExpedientEntity> findByEntitatAndClassificacioSia(EntitatEntity entitat, String classificacioSia);
	
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

	@Query(	"from" +
			"    MetaExpedientEntity me " +
			"where " +
			"	 me.entitat = :entitat " +
			"and me.organGestor in (:organGestors) ")
	List<MetaExpedientEntity> findByOrganGestors(
			@Param("entitat") EntitatEntity entitat,
			@Param("organGestors") List<OrganGestorEntity> organGestors);
	
	
	@Query(	"select" +
			"    me.id " +
			"from" +
			"    MetaExpedientEntity me " +
			" where " +
			"    (me.entitat = :entitat) ")
	public List<Long> findAllIdsByEntitat(@Param("entitat") EntitatEntity entitat);
	
	@Query(	"select" +
			"    me.id " +
			"from" +
			"    MetaExpedientEntity me " +
			" where " +
			"    (me.entitat = :entitat) " + 
			"and me.id in (:ids)")
	public List<Long> findIdsByEntitat(@Param("entitat") EntitatEntity entitat, @Param("ids") List<Long> ids);
	
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

}
