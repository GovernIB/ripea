/**
 * 
 */
package es.caib.ripea.core.repository;

import es.caib.ripea.core.api.dto.OrganEstatEnumDto;
import es.caib.ripea.core.persistence.EntitatEntity;
import es.caib.ripea.core.persistence.MetaExpedientOrganGestorEntity;
import es.caib.ripea.core.persistence.OrganGestorEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

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
			"where (:esNullFiltre = true or lower(og.codi) like lower('%'||:filtre||'%') or lower(og.nom) like lower('%'||:filtre||'%')) ")
	public List<OrganGestorEntity> findByFiltre(
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
			"    og.codi " + 
			"from " +
			"    OrganGestorEntity og " +
			"where " +
			"    (og.entitat = :entitat) " +
			"and (:esNullCodi = true or lower(og.codi) like lower('%'||:codi||'%')) " +
			"and (:esNullNom = true or lower(og.nom) like lower('%'||:nom||'%')) " +
			"and (:esNullOrganSuperior = true or og.pare.id = :organSuperiorId) " + 
			"and (:esNullEstat = true or og.estat = :estat) ")
	public Set<String> findAmbFiltre(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullCodi") boolean esNullCodi,
			@Param("codi") String codi,
			@Param("esNullNom") boolean esNullNom,
			@Param("nom") String nom,
			@Param("esNullOrganSuperior") boolean esNullOrganSuperior,
			@Param("organSuperiorId") Long organSuperiorId,
			@Param("esNullEstat") boolean esNullEstat,
			@Param("estat") OrganEstatEnumDto estat);


	@Query("from " +
			"    OrganGestorEntity og " +
			" where " +
			"    (og.entitat = :entitat) " + 
			"and og.id in (:ids)")
	public List<OrganGestorEntity> findByEntitatAndIds(
			@Param("entitat") EntitatEntity entitat,
			@Param("ids") List<Long> ids);


	@Query("from " +
			"    OrganGestorEntity og " +
			" where " +
			"    (og.entitat = :entitat) " +
			"and og.codi in (:codis)")
	public List<OrganGestorEntity> findByEntitatAndCodis(
			@Param("entitat") EntitatEntity entitat,
			@Param("codis") List<String> codis);


	@Query(	"select og.codi " +
			"from " +
			"    OrganGestorEntity og " +
			" where " +
			"    og.entitat = :entitat " + 
			"and og.estat = es.caib.ripea.core.api.dto.OrganEstatEnumDto.V " +
			"and og.id in (:ids)")
	public List<String> findCodisByEntitatAndVigentIds(
			@Param("entitat") EntitatEntity entitat,
			@Param("ids") List<Long> ids);
	

	@Query(	"select distinct og.codi " +
			"from " +
			"    OrganGestorEntity og " +
			"where" +
			"    og.id in (:ids)")
	public List<String> findCodisByIdList(@Param("ids") List<Long> ids);

	
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
