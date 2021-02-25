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
			"and (:esNullOrganGestor = true or me.organGestor = :organGestor)")
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
			Sort sort);

	@Query(	"from " +
			"    MetaExpedientEntity me " +
			"where " +
			"    me.entitat = :entitat " +
			"and (:esNullCodi = true or lower(me.codi) like lower('%'||:codi||'%')) " +
			"and (:esNullNom = true or lower(me.nom) like lower('%'||:nom||'%')) " +
			"and (:esNullClassificacioSia = true or lower(me.classificacioSia) like lower('%'||:classificacioSia||'%')) " +
			"and (:esNullActiu = true or me.actiu = :actiu) " +
			"and (:esNullAmbit = true or ((:comuns = true and me.organGestor = null) or (:comuns = false  and me.organGestor != null)) ) " +
			"and (:esNullOrganGestor = true or me.organGestor = :organGestor)")
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
			"and me.id in (:ids)")
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
			"and me.id in (:ids)")
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
			"and (:esNullActiu = true or me.actiu = :actiu) " +
			"and (:esNullFiltre = true or lower(me.nom) like lower('%'||:filtre||'%') or lower(me.classificacioSia) like lower('%'||:filtre||'%')) " +
			"and (:esAdminEntitat = true " +
			"     or (:esAdminOrgan = true and :esAdminOrgan = false) " + // TODO esborrar
			"     or (:esNullIdPermesos = false and me.id in (:idPermesos)) " +
			"     or (me.organGestor is not null and :esNullOrganIdPermesos = false and me.organGestor.id in (:organIdPermesos)) " +
			"     or (me.organGestor is null and :esNullMetaExpedientOrganIdPermesos = false and meog.id in (:metaExpedientOrganIdPermesos)) )")
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
			@Param("metaExpedientOrganIdPermesos") List<Long> metaExpedientOrganIdPermesos);

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

	List<MetaExpedientEntity> findByEntitatOrderByNomAsc(EntitatEntity entitat);

	List<MetaExpedientEntity> findByEntitatAndActiuTrueOrderByNomAsc(EntitatEntity entitat);

	List<MetaExpedientEntity> findByOrganGestorOrderByNomAsc(OrganGestorEntity organGestorEntity);

	List<MetaExpedientEntity> findByEntitatAndClassificacioSia(EntitatEntity entitat, String classificacioSia);

	@Query(	"from" +
			"    MetaExpedientEntity me " +
			"where " +
			"	 me.entitat = :entitat " +
			"and me.organGestor in (:organGestors) ")
	List<MetaExpedientEntity> findByOrganGestors(
			@Param("entitat") EntitatEntity entitat,
			@Param("organGestors") List<OrganGestorEntity> organGestors);

}
