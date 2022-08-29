/**
 * 
 */
package es.caib.ripea.core.repository;

import java.util.List;

import es.caib.ripea.core.api.dto.OrganEstatEnumDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.MetaExpedientOrganGestorEntity;
import es.caib.ripea.core.entity.OrganGestorEntity;

/**
 * Definició dels mètodes necessaris per a gestionar un organ gestor
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface OrganGestorRepository extends JpaRepository<OrganGestorEntity, Long> {

	public List<OrganGestorEntity> findByEntitat(EntitatEntity entitat);
	public Page<OrganGestorEntity> findByEntitat(EntitatEntity entitat, Pageable paginacio);
	public OrganGestorEntity findByEntitatAndCodi(EntitatEntity entitat, String codi);
	
	@Query(	"from " +
			"    OrganGestorEntity og " +
			"where (og.entitat = :entitat)" +
			" and (og.id in (select distinct pare.id from OrganGestorEntity))")
	public List<OrganGestorEntity> findByEntitatAndHasPare(
			@Param("entitat") EntitatEntity entitat);

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
			"where (og.entitat = :entitat)" +
			" and (:esNullFiltre = true or lower(og.codi) like lower('%'||:filtre||'%') or lower(og.nom) like lower('%'||:filtre||'%')) " +
			" and og.id in (:ids)")
	public List<OrganGestorEntity> findByEntitatAndFiltreAndIds(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,
			@Param("ids") List<Long> ids);

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
			"where " +
			"    (og.entitat = :entitat) " +
			"and (:esNullCodi = true or lower(og.codi) like lower('%'||:codi||'%')) " +
			"and (:esNullNom = true or lower(og.nom) like lower('%'||:nom||'%')) " +
			"and (:esNullOrganSuperior = true or og.pare.id = :organSuperiorId) " + 
			"and (:esNullEstat = true or og.estat = :estat) ")
	public Page<OrganGestorEntity> findAmbFiltrePaginat(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullCodi") boolean esNullCodi,
			@Param("codi") String codi,
			@Param("esNullNom") boolean esNullNom,
			@Param("nom") String nom,
			@Param("esNullOrganSuperior") boolean esNullOrganSuperior,
			@Param("organSuperiorId") Long organSuperiorId,
			@Param("esNullEstat") boolean esNullEstat,
			@Param("estat") OrganEstatEnumDto estat,
			Pageable paginacio);

	@Query(	"select " +
			"    og " + 
			"from " +
			"    OrganGestorEntity og " +
			"    left join og.pare pare1 " +
			"    left join pare1.pare pare2 " + 
			"	 left join pare2.pare pare3 " +
			"	 left join pare3.pare pare4 " +
			"where " +
			"    og.entitat = :entitat " +
			"and (:esNullFiltre = true or lower(og.codi) like lower('%'||:filtre||'%') or lower(og.nom) like lower('%'||:filtre||'%')) " +
			"and (og.id in (:pareIds) " +
			"     or pare1.id in (:pareIds) " +
			"     or pare2.id in (:pareIds) " +
			"     or pare3.id in (:pareIds) " +
			"     or pare4.id in (:pareIds)) " +
			"order by og.nom asc")
	public List<OrganGestorEntity> findByEntitatAndFiltreAndPareIdIn(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,
			@Param("pareIds") List<Long> pareIds);

	@Query("select " +
			"    og.id " +
			"from " +
			"    OrganGestorEntity og " +
			"    left join og.pare pare1 " +
			"    left join pare1.pare pare2 " + 
			"	 left join pare2.pare pare3 " +
			"	 left join pare3.pare pare4 " +
			"where " +
			"    og.entitat = :entitat " +
			"and (pare1.id in (:pareIds) " +
			"     or pare2.id in (:pareIds) " +
			"     or pare3.id in (:pareIds) " +
			"     or pare4.id in (:pareIds))")
	public List<Long> findFillsIds(
			@Param("entitat") EntitatEntity entitat,
			@Param("pareIds") List<Long> pareIds);
	
	
	@Query("select " +
			"    og.codi " +
			"from " +
			"    OrganGestorEntity og " +
			"    left join og.pare pare1 " +
			"    left join pare1.pare pare2 " + 
			"	 left join pare2.pare pare3 " +
			"	 left join pare3.pare pare4 " +
			"where " +
			"    og.entitat = :entitat " +
			"and (pare1.id in (:pareIds) " +
			"     or pare2.id in (:pareIds) " +
			"     or pare3.id in (:pareIds) " +
			"     or pare4.id in (:pareIds))")
	public List<String> findFillsCodis(
			@Param("entitat") EntitatEntity entitat,
			@Param("pareIds") List<Long> pareIds);
	
	
	@Query("select " +
			"    og " +
			"from " +
			"    OrganGestorEntity og " +
			"    left join og.pare pare1 " +
			"    left join pare1.pare pare2 " + 
			"	 left join pare2.pare pare3 " +
			"	 left join pare3.pare pare4 " +
			"where " +
			"    og.entitat = :entitat " +
			"and (pare1.id in (:pareIds) " +
			"     or pare2.id in (:pareIds) " +
			"     or pare3.id in (:pareIds) " +
			"     or pare4.id in (:pareIds))")
	public List<OrganGestorEntity> findFills(
			@Param("entitat") EntitatEntity entitat,
			@Param("pareIds") List<Long> pareIds);

	@Query("from " +
			"    OrganGestorEntity og " +
			" where " +
			"    (og.entitat = :entitat) " + 
			"and og.id in (:ids)")
	public List<OrganGestorEntity> findByEntitatAndIds(@Param("entitat") EntitatEntity entitat, @Param("ids") List<Long> ids);

	@Query(	"select og.id " + 
			"from " +
			"    OrganGestorEntity og " +
			"where" +
			"    og.codi in (:codi)")
	public List<Long> findIdsByCodiDir3List(List<String> codi);
	
	
	
	@Query(	"select " +
			"    meog " + 
			"from " +
			"    MetaExpedientOrganGestorEntity meog " +
			"where " +
			"    meog.metaExpedient.id = :metaExpedientId")
	List<MetaExpedientOrganGestorEntity> findMetaExpedientOrganGestorsByMetaExpedientId(
			@Param("metaExpedientId") Long metaExpedientId);

	public List<OrganGestorEntity> findByEntitatIdAndEstat(Long entitatId, OrganEstatEnumDto estat);

	public OrganGestorEntity findByCodi(String codi);

	@Query(	"from " +
			"    OrganGestorEntity og " +
			"where " +
			"    og.entitat = :entitat " +
			"and og.estat != es.caib.ripea.core.api.dto.OrganEstatEnumDto.V ")
    List<OrganGestorEntity> findByEntitatNoVigent(@Param("entitat") EntitatEntity entitat);
}
