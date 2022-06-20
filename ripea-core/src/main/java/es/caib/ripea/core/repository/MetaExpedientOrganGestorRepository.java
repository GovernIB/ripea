/**
 * 
 */
package es.caib.ripea.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.MetaExpedientOrganGestorEntity;
import es.caib.ripea.core.entity.OrganGestorEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus relació meta-expedient - òrgan.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface MetaExpedientOrganGestorRepository extends JpaRepository<MetaExpedientOrganGestorEntity, Long> {

	MetaExpedientOrganGestorEntity findByMetaExpedientAndOrganGestor(
			MetaExpedientEntity metaExpedient,
			OrganGestorEntity organGestor);

	List<MetaExpedientOrganGestorEntity> findByMetaExpedient(MetaExpedientEntity metaExpedient);
	
	List<MetaExpedientOrganGestorEntity> findByOrganGestor(
			OrganGestorEntity organGestor);

	@Query(	"select " +
			"    meog.organGestor.id " + 
			"from " +
			"    MetaExpedientOrganGestorEntity meog " +
			"where " +
			"    meog.metaExpedient = :metaExpedient ")
	List<Long> findOrganGestorIdByMetaExpedient(
			@Param("metaExpedient") MetaExpedientEntity metaExpedient);

	@Query(	"select " +
			"    meog.organGestor.id " + 
			"from " +
			"    MetaExpedientOrganGestorEntity meog " +
			"where " +
			"    meog.id in (:metaExpedientOrganGestorIds) ")
	public List<Long> findOrganGestorIdsByMetaExpedientOrganGestorIds(
			@Param("metaExpedientOrganGestorIds") List<Long> metaExpedientOrganGestorIds);
	
	@Query(	"select " +
			"    meog.organGestor.id " + 
			"from " +
			"    MetaExpedientOrganGestorEntity meog " +
			"where " +
			"    meog in (:metaExpedientOrganGestors) ")
	public List<Long> findOrganGestorIdsByMetaExpedientOrganGestors(
			@Param("metaExpedientOrganGestors") List<MetaExpedientOrganGestorEntity> metaExpedientOrganGestors);
	

	@Query("select " +
			"    meog.organGestor " +
			"from " +
			"    MetaExpedientOrganGestorEntity meog " +
			"    left join meog.organGestor.pare pare1 " +
			"    left join pare1.pare pare2 " + 
			"	 left join pare2.pare pare3 " +
			"	 left join pare3.pare pare4 " +
			"where " +
			"    meog.metaExpedient = :metaExpedient " +
			"and (:esNullFiltre = true or lower(meog.organGestor.codi) like lower('%'||:filtre||'%') or lower(meog.organGestor.nom) like lower('%'||:filtre||'%')) " +
			"and (meog.organGestor.id in (:pareIds) " +
			"     or pare1.id in (:pareIds) " + 
			"	  or pare2.id in (:pareIds)) " +
			"	  or pare3.id in (:pareIds)) " +
			"	  or pare4.id in (:pareIds)) " +
			"order by meog.organGestor.nom asc")
	public List<OrganGestorEntity> findOrganGestorByMetaExpedientAndFiltreAndOrganGestorPareIdIn(
			@Param("metaExpedient") MetaExpedientEntity metaExpedient,
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,
			@Param("pareIds") List<Long> pareIds);

	@Query("select " +
			"    meog.id " +
			"from " +
			"    MetaExpedientOrganGestorEntity meog " +
			"    left join meog.organGestor.pare pare1 " +
			"    left join pare1.pare pare2 " + 
			"	 left join pare2.pare pare3 " +
			"	 left join pare3.pare pare4 " +
			"where " +
			"    meog.metaExpedient.entitat = :entitat " +
			"and (pare1.id in (:pareIds) " +
			"     or pare2.id in (:pareIds) " +
			"     or pare3.id in (:pareIds) " +
			"     or pare4.id in (:pareIds))")
	public List<Long> findFillsIds(
			@Param("entitat") EntitatEntity entitat,
			@Param("pareIds") List<Long> pareIds);


	@Query(	"select count(meog.id) from MetaExpedientOrganGestorEntity meog where meog.organGestor = :organGestor")
	Integer countByOrganGestor(@Param("organGestor") OrganGestorEntity organGestor);
}
