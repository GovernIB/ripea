package es.caib.ripea.core.repository;

import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.GrupEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GrupRepository extends JpaRepository<GrupEntity, Long> {
	
	@Query(	"from " +
			"    GrupEntity grup " +
			"where " +
			"    grup.entitat = :entitat " +
			"and (:esNullFiltre = true or lower(grup.rol) like lower('%'||:filtre||'%') or lower(grup.descripcio) like lower('%'||:filtre||'%')) ")
	Page<GrupEntity> findByEntitat(
			@Param("entitat") EntitatEntity entitat, 
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,	
			Pageable pageable);


	static final String FIND_BY_ENTITAT_AND_PROCEDIMENT_AND_FILTRE = "select distinct " +
			"    grup " +
			"from " +
			"    GrupEntity grup " +
			"    left join grup.metaExpedients me " +
			"    left join grup.organGestor og " +
			"where " +
			"    grup.entitat = :entitat " +
			"and (:esNullProcedimentId = true or me.id = :procedimentId) " +
			"and (:esNullFiltre = true or lower(grup.rol) like lower('%'||:filtre||'%') or lower(grup.descripcio) like lower('%'||:filtre||'%')) " +
			"and (:esNullOrgansFills = true or og.codi in (:organsFills))";
	
	@Query(FIND_BY_ENTITAT_AND_PROCEDIMENT_AND_FILTRE)
	Page<GrupEntity> findByEntitatAndProcediment(
			@Param("entitat") EntitatEntity entitat, 
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,	
			@Param("esNullProcedimentId") boolean esNullProcedimentId,
			@Param("procedimentId") Long procedimentId,	
			@Param("esNullOrgansFills") boolean esNullOrgansFills,
			@Param("organsFills") List<String> organsFills,
			Pageable pageable);

	@Query(FIND_BY_ENTITAT_AND_PROCEDIMENT_AND_FILTRE)
	List<GrupEntity> findByEntitatAndProcediment(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,
			@Param("esNullProcedimentId") boolean esNullProcedimentId,
			@Param("procedimentId") Long procedimentId,
			@Param("esNullOrgansFills") boolean esNullOrgansFills,
			@Param("organsFills") List<String> organsFills);
	

	static final String FIND_BY_ENTITAT_AND_PROCEDIMENT = "select distinct " +
			"    grup " +
			"from " +
			"    GrupEntity grup " +
			"    left join grup.metaExpedients me " +
			"    left join grup.organGestor og " +
			"where " +
			"    grup.entitat = :entitat " +
			"and (:esNullProcedimentId = true or me.id = :procedimentId) " +
			"and (:esNullCodi = true or lower(grup.codi) like lower('%'||:codi||'%')) " +
			"and (:esNullDescripcio = true or lower(grup.descripcio) like lower('%'||:descripcio||'%')) " +
			"and (:esNullOrganGestorId = true or og.id = :organGestorId) " +
			"and (:esNullOrgansFills = true or og.codi in (:organsFills))";
	
	@Query(FIND_BY_ENTITAT_AND_PROCEDIMENT)
	Page<GrupEntity> findByEntitatAndProcediment(
			@Param("entitat") EntitatEntity entitat, 
			@Param("esNullCodi") boolean esNullCodi,
			@Param("codi") String codi,
			@Param("esNullDescripcio") boolean esNullDescripcio,
			@Param("descripcio") String descripcio,
			@Param("esNullProcedimentId") boolean esNullProcedimentId,
			@Param("procedimentId") Long procedimentId,	
			@Param("esNullOrganGestorId") boolean esNullOrganGestorId,
			@Param("organGestorId") Long organGestorId,	
			@Param("esNullOrgansFills") boolean esNullOrgansFills,
			@Param("organsFills") List<String> organsFills,
			Pageable pageable);

	@Query(FIND_BY_ENTITAT_AND_PROCEDIMENT)
	List<GrupEntity> findByEntitatAndProcediment(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullCodi") boolean esNullCodi,
			@Param("codi") String codi,
			@Param("esNullDescripcio") boolean esNullDescripcio,
			@Param("descripcio") String descripcio,
			@Param("esNullProcedimentId") boolean esNullProcedimentId,
			@Param("procedimentId") Long procedimentId,
			@Param("esNullOrganGestorId") boolean esNullOrganGestorId,
			@Param("organGestorId") Long organGestorId,
			@Param("esNullOrgansFills") boolean esNullOrgansFills,
			@Param("organsFills") List<String> organsFills);
	
	@Query(	"select distinct " +
			"    grup.id " +
			"from " +
			"    GrupEntity grup " +
			"    left join grup.metaExpedients me " +
			"    left join grup.organGestor og " +
			"where " +
			"    grup.entitat = :entitat " +
			"and (:esNullProcedimentId = true or me.id = :procedimentId) " +
			"and (:esNullCodi = true or lower(grup.codi) like lower('%'||:codi||'%')) " +
			"and (:esNullDescripcio = true or lower(grup.descripcio) like lower('%'||:descripcio||'%')) " +
			"and (:esNullOrganGestorId = true or og.id = :organGestorId) " +
			"and (:esNullOrgansFills = true or og.codi in (:organsFills))")
	List<Long> findIdsByEntitatAndProcediment(
			@Param("entitat") EntitatEntity entitat, 
			@Param("esNullCodi") boolean esNullCodi,
			@Param("codi") String codi,
			@Param("esNullDescripcio") boolean esNullDescripcio,
			@Param("descripcio") String descripcio,
			@Param("esNullProcedimentId") boolean esNullProcedimentId,
			@Param("procedimentId") Long procedimentId,	
			@Param("esNullOrganGestorId") boolean esNullOrganGestorId,
			@Param("organGestorId") Long organGestorId,	
			@Param("esNullOrgansFills") boolean esNullOrgansFills,
			@Param("organsFills") List<String> organsFills);
	
	
	@Query(	"select distinct " +
			"    grup " +
			"from " +
			"    GrupEntity grup " +
			"    left join grup.metaExpedients me " +
			"    left join grup.organGestor og " +
			"where " +
			"    grup.entitat = :entitat " +
			"and (:esNullProcedimentId = true or me.id = :procedimentId) " +
			"and (:esNullOrgansFills = true or og.codi in (:organsFills))")
	List<GrupEntity> findByEntitatAndOrgan(
			@Param("entitat") EntitatEntity entitat, 
			@Param("esNullProcedimentId") boolean esNullProcedimentId,
			@Param("procedimentId") Long procedimentId,	
			@Param("esNullOrgansFills") boolean esNullOrgansFills,
			@Param("organsFills") List<String> organsFills);


	List<GrupEntity> findByEntitatId(
			@Param("entitatId") Long entitatId);
	

	GrupEntity findByEntitatIdAndCodi(
			@Param("entitatId") Long entitatId, 
			@Param("codi") String codi);

	@Modifying
 	@Query(value = "UPDATE IPA_GRUP " +
 			"SET CREATEDBY_CODI = CASE WHEN CREATEDBY_CODI = :codiAntic THEN :codiNou ELSE CREATEDBY_CODI END, " +
 			"    LASTMODIFIEDBY_CODI = CASE WHEN LASTMODIFIEDBY_CODI = :codiAntic THEN :codiNou ELSE LASTMODIFIEDBY_CODI END " +
 			"WHERE CREATEDBY_CODI = :codiAntic OR LASTMODIFIEDBY_CODI = :codiAntic",
 			nativeQuery = true)
	public int updateUsuariAuditoria(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);
}
