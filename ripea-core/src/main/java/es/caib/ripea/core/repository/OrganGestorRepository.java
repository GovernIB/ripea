/**
 * 
 */
package es.caib.ripea.core.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.OrganGestorEntity;

/**
 * Definició dels mètodes necessaris per a gestionar un organ gestor
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface OrganGestorRepository extends JpaRepository<OrganGestorEntity, Long> {

	public List<OrganGestorEntity> findByEntitat(EntitatEntity entitat);
	public Page<OrganGestorEntity> findByEntitat(EntitatEntity entitat, Pageable paginacio);
//	public OrganGestorEntity findByCodi(String codi);
	public OrganGestorEntity findByCodiAndEntitat(String codi, EntitatEntity entitat);
	
	@Query(	"from " +
			"    OrganGestorEntity og " +
			"where (og.entitat = :entitat)" +
			" and (:esNullFiltre = true or lower(og.codi) like lower('%'||:filtre||'%') or lower(og.nom) like lower('%'||:filtre||'%')) ")
	public List<OrganGestorEntity> findByEntitatAndFiltre(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre);
	
	@Query(	"from " +
			"    OrganGestorEntity og " +
			"where " +
			"     og in (:canditats) " +
			" and (:esNullFiltre = true or lower(og.codi) like lower('%'||:filtre||'%') or lower(og.nom) like lower('%'||:filtre||'%')) ")
	public List<OrganGestorEntity> findByCanditatsAndFiltre(
			@Param("canditats")List<OrganGestorEntity> canditats,
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre);
	
	@Query(	"from " +
			"    OrganGestorEntity og " +
			"where (og.entitat = :entitat)" +
      " and (:esNullFiltre = true or lower(og.codi) like lower('%'||:filtre||'%') or lower(og.nom) like lower('%'||:filtre||'%')) ")
	public Page<OrganGestorEntity> findByEntitatAndFiltre(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,
			Pageable paginacio);
	
	@Query("from " +
    		 "    OrganGestorEntity og " +
    		 "where og.codi in (:codis)")
	public List<OrganGestorEntity> findByCodiDir3List(@Param("codis") List<String> codis);
	
	@Query("from " +
   		 "    OrganGestorEntity og " +
   		 " where " +
   		 "     (og.entitat = :entitat)" + 
   		 " 	and og.id in (:ids)")
	public List<OrganGestorEntity> findByEntitatAndIds(@Param("entitat") EntitatEntity entitat, @Param("ids") List<Long> ids);

  @Query( "select og.id " + 
      "from " +
      "    OrganGestorEntity og " +
      "where og.codi in (:codi)")
  public List<Long> findIdsByCodiDir3List(List<String> codi);
}

