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
			"and (:esNullActiu = true or me.actiu = :actiu) " +
			"and (:hideWithoutOrganGestor = true or me.organGestor != null) " +
			"and (:esNullOrganGestor = true or me.organGestor = :organGestor)")
	List<MetaExpedientEntity> findByEntitat(
			@Param("entitat") EntitatEntity entitat, 
			@Param("esNullCodi") boolean esNullCodi,
			@Param("codi") String codi,
			@Param("esNullNom") boolean esNullNom,
			@Param("nom") String nom,
			@Param("esNullActiu") boolean esNullActiu,
			@Param("actiu") Boolean actiu,
			@Param("esNullOrganGestor") boolean esNullOrganGestor,
			@Param("organGestor") OrganGestorEntity organGestor,
			@Param("hideWithoutOrganGestor") boolean hideWithoutOrganGestor,
			Sort sort);
	  
	@Query( "from " +
	         "    MetaExpedientEntity me " +
	         "where " +
	         "    me.entitat = :entitat " +
			"and (:esNullCodi = true or lower(me.codi) like lower('%'||:codi||'%')) " +
			"and (:esNullNom = true or lower(me.nom) like lower('%'||:nom||'%')) " +
			"and (:esNullActiu = true or me.actiu = :actiu) " +
			"and (:esNullOrganGestor = true or me.organGestor = :organGestor) " +
	        "and me.id in (:ids)")
	List<MetaExpedientEntity> findByEntitat(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullCodi") boolean esNullCodi,
			@Param("codi") String codi,
			@Param("esNullNom") boolean esNullNom,
			@Param("nom") String nom,
			@Param("esNullActiu") boolean esNullActiu,
			@Param("actiu") Boolean actiu,
			@Param("esNullOrganGestor") boolean esNullOrganGestor,
			@Param("organGestor") OrganGestorEntity organGestor,
			@Param("ids") List<Long> ids,
			Sort sort);

	@Query(	"from " +
			"    MetaExpedientEntity me " +
			"where " +
			"    me.entitat = :entitat " +
			"and (:esNullCodi = true or lower(me.codi) like lower('%'||:codi||'%')) " +
			"and (:esNullNom = true or lower(me.nom) like lower('%'||:nom||'%')) " +
			"and (:esNullActiu = true or me.actiu = :actiu) " +
			"and (:hideWithoutOrganGestor = true or me.organGestor != null) " +
			"and (:esNullOrganGestor = true or me.organGestor = :organGestor)")
	Page<MetaExpedientEntity> findByEntitat(
			@Param("entitat") EntitatEntity entitat, 
			@Param("esNullCodi") boolean esNullCodi,
			@Param("codi") String codi,
			@Param("esNullNom") boolean esNullNom,
			@Param("nom") String nom,
			@Param("esNullActiu") boolean esNullActiu,
			@Param("actiu") Boolean actiu,
			@Param("esNullOrganGestor") boolean esNullOrganGestor,
			@Param("organGestor") OrganGestorEntity organGestor,	
			@Param("hideWithoutOrganGestor") boolean hideWithoutOrganGestor,
			Pageable pageable);

	@Query( "from " +
	         "    MetaExpedientEntity me " +
	         "where " +
	         "    me.entitat = :entitat " +
			"and (:esNullCodi = true or lower(me.codi) like lower('%'||:codi||'%')) " +
			"and (:esNullNom = true or lower(me.nom) like lower('%'||:nom||'%')) " +
			"and (:esNullActiu = true or me.actiu = :actiu) " +
			"and (:esNullOrganGestor = true or me.organGestor = :organGestor) " +
	        "and me.id in (:ids)")
	Page<MetaExpedientEntity> findByEntitat(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullCodi") boolean esNullCodi,
			@Param("codi") String codi,
			@Param("esNullNom") boolean esNullNom,
			@Param("nom") String nom,
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
	

	
	@Query( "from " +
	         "    MetaExpedientEntity me " +
	         "where " +
	         "    me.entitat = :entitat " +
			"and me.actiu = true " +
			"and (:esNullFiltre = true or lower(me.nom) like lower('%'||:filtre||'%') or lower(me.classificacioSia) like lower('%'||:filtre||'%')) ")
	List<MetaExpedientEntity> findByEntitatAndActiuTrueAndFiltreOrderByNomAsc(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre);


	@Query( "from " +
	         "    MetaExpedientEntity me " +
	         "where " +
	         "    me.organGestor = :organGestor " +
			"and me.actiu = true " +
			"and (:esNullFiltre = true or lower(me.nom) like lower('%'||:filtre||'%') or lower(me.classificacioSia) like lower('%'||:filtre||'%')) ")
	List<MetaExpedientEntity> findByOrganGestorAndActiuAndFiltreTrueOrderByNomAsc(
			@Param("organGestor") OrganGestorEntity organGestor,
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre);
	
	
	List<MetaExpedientEntity> findByEntitatOrderByNomAsc(EntitatEntity entitat);
	
    
	List<MetaExpedientEntity> findByEntitatAndActiuTrueOrderByNomAsc(EntitatEntity entitat);

	List<MetaExpedientEntity> findByOrganGestorOrderByNomAsc(OrganGestorEntity organGestorEntity);
	
	List<MetaExpedientEntity> findByEntitatAndClassificacioSia(EntitatEntity entitat, String classificacioSia);
	

}
