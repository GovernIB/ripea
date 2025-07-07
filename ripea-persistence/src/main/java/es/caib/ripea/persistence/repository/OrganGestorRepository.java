package es.caib.ripea.persistence.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.MetaExpedientOrganGestorEntity;
import es.caib.ripea.persistence.entity.OrganGestorEntity;
import es.caib.ripea.service.intf.dto.OrganEstatEnumDto;

@Component
public interface OrganGestorRepository extends JpaRepository<OrganGestorEntity, Long> {

	public List<OrganGestorEntity> findByEntitat(EntitatEntity entitat);

	public OrganGestorEntity findByEntitatAndCodi(EntitatEntity entitat, String codi);
	
	@Query(	"from " +
			"    OrganGestorEntity og " +
			"where (og.entitat.id = :entitatId)" +
			" and (og.id in (select distinct pare.id from OrganGestorEntity))")
	public List<OrganGestorEntity> findByEntitatAndHasPare(@Param("entitatId") Long entitatId);

	@Query(	"from " +
			"    OrganGestorEntity og " +
			"where (og.entitat.id = :entitatId)" +
			" and (:esNullFiltre = true or lower(og.codi) like lower('%'||:filtre||'%') or lower(og.nom) like lower('%'||:filtre||'%')) ")
	public List<OrganGestorEntity> findByEntitatAndFiltre(
			@Param("entitatId") Long entitatId,
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre);

	@Query(	"from " +
			"    OrganGestorEntity og " +
			"where (og.entitat.id = :entitatId)" +
			" and og in (:canditats) " +
			" and (:esNullFiltre = true or lower(og.codi) like lower('%'||:filtre||'%') or lower(og.nom) like lower('%'||:filtre||'%')) ")
	public List<OrganGestorEntity> findByCanditatsAndFiltre(
			@Param("entitatId") Long entitatId,
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

	@Query("from OrganGestorEntity og " +
			" where og.entitat = :entitat " + 
			"and og.id in (:ids)")
	public List<OrganGestorEntity> findByEntitatAndIds(
			@Param("entitat") EntitatEntity entitat,
			@Param("ids") List<Long> ids);

	@Query("from OrganGestorEntity og " +
			" where og.entitat = :entitat " +
			"and og.codi in (:codis)")
	public List<OrganGestorEntity> findByEntitatAndCodis(
			@Param("entitat") EntitatEntity entitat,
			@Param("codis") List<String> codis);

	@Query(	"select og.codi " +
			"from OrganGestorEntity og " +
			" where og.entitat = :entitat " + 
			"and og.estat = es.caib.ripea.service.intf.dto.OrganEstatEnumDto.V " +
			"and og.id in (:ids)")
	public List<String> findCodisByEntitatAndVigentIds(
			@Param("entitat") EntitatEntity entitat,
			@Param("ids") List<Long> ids);

	@Query(	"select distinct og.codi " +
			"from OrganGestorEntity og " +
			"where og.entitat.id = :entitatId" +
			" and og.id in (:ids)")
	public List<String> findCodisByIdList(@Param("entitatId")Long entitatId, @Param("ids") List<Long> ids);
	
	@Query(	"select meog " + 
			"from MetaExpedientOrganGestorEntity meog " +
			"where meog.metaExpedient.id = :metaExpedientId")
	List<MetaExpedientOrganGestorEntity> findMetaExpedientOrganGestorsByMetaExpedientId(@Param("metaExpedientId") Long metaExpedientId);

	public List<OrganGestorEntity> findByEntitatIdAndEstat(Long entitatId, OrganEstatEnumDto estat);

	public OrganGestorEntity findByEntitatIdAndCodi(Long entitatId, String codi);

	@Query(	"from OrganGestorEntity og " +
			"where og.entitat = :entitat " +
			"and og.estat != es.caib.ripea.service.intf.dto.OrganEstatEnumDto.V ")
	public List<OrganGestorEntity> findByEntitatNoVigent(@Param("entitat") EntitatEntity entitat);
	
	@Modifying
 	@Query(value = "UPDATE IPA_ORGAN_GESTOR " +
 			"SET CREATEDBY_CODI = CASE WHEN CREATEDBY_CODI = :codiAntic THEN :codiNou ELSE CREATEDBY_CODI END, " +
 			"    LASTMODIFIEDBY_CODI = CASE WHEN LASTMODIFIEDBY_CODI = :codiAntic THEN :codiNou ELSE LASTMODIFIEDBY_CODI END " +
 			"WHERE CREATEDBY_CODI = :codiAntic OR LASTMODIFIEDBY_CODI = :codiAntic",
 			nativeQuery = true)
	public int updateUsuariAuditoria(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);
}