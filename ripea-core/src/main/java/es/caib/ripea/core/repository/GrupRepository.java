package es.caib.ripea.core.repository;

import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.GrupEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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
	
	
	
	
	@Query(	"select distinct " +
			"    grup " +
			"from " +
			"    GrupEntity grup " +
			"    left join grup.metaExpedients me " +
			"    left join grup.organGestor og " +
			"    left join og.pare pare1 " +
			"    left join pare1.pare pare2 " + 
			"	 left join pare2.pare pare3 " +
			"	 left join pare3.pare pare4 " +
			"where " +
			"    grup.entitat = :entitat " +
			"and (:esNullProcedimentId = true or me.id = :procedimentId) " +
			"and (:esNullFiltre = true or lower(grup.rol) like lower('%'||:filtre||'%') or lower(grup.descripcio) like lower('%'||:filtre||'%')) " +
			"and (:esNullOrganGestorId = true " +
			"	  or og.id = :organGestorId " +
			"     or pare1.id = :organGestorId " +
			"     or pare2.id = :organGestorId " +
			"     or pare3.id = :organGestorId " +
			"     or pare4.id = :organGestorId)")
	Page<GrupEntity> findByEntitatAndProcediment(
			@Param("entitat") EntitatEntity entitat, 
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,	
			@Param("esNullProcedimentId") boolean esNullProcedimentId,
			@Param("procedimentId") Long procedimentId,	
			@Param("esNullOrganGestorId") boolean esNullOrganGestorId,
			@Param("organGestorId") Long organGestorId,	
			Pageable pageable);
	
	
	
	@Query(	"select distinct " +
			"    grup " +
			"from " +
			"    GrupEntity grup " +
			"    left join grup.metaExpedients me " +
			"    left join grup.organGestor og " +
			"    left join og.pare pare1 " +
			"    left join pare1.pare pare2 " + 
			"	 left join pare2.pare pare3 " +
			"	 left join pare3.pare pare4 " +
			"where " +
			"    grup.entitat = :entitat " +
			"and (:esNullProcedimentId = true or me.id = :procedimentId) " +
			"and (:esNullCodi = true or lower(grup.codi) like lower('%'||:codi||'%')) " +
			"and (:esNullDescripcio = true or lower(grup.descripcio) like lower('%'||:descripcio||'%')) " +
			"and (:esNullOrganGestorId = true or og.id = :organGestorId) " +
			"and (:esNullOrganGestorAscendentId = true " +
			"	  or og.id = :organGestorAscendentId " +
			"     or pare1.id = :organGestorAscendentId " +
			"     or pare2.id = :organGestorAscendentId " +
			"     or pare3.id = :organGestorAscendentId " +
			"     or pare4.id = :organGestorAscendentId)")
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
			@Param("esNullOrganGestorAscendentId") boolean esNullOrganGestorAscendentId,
			@Param("organGestorAscendentId") Long organGestorAscendentId,	
			Pageable pageable);
	
	@Query(	"select distinct " +
			"    grup.id " +
			"from " +
			"    GrupEntity grup " +
			"    left join grup.metaExpedients me " +
			"    left join grup.organGestor og " +
			"    left join og.pare pare1 " +
			"    left join pare1.pare pare2 " + 
			"	 left join pare2.pare pare3 " +
			"	 left join pare3.pare pare4 " +
			"where " +
			"    grup.entitat = :entitat " +
			"and (:esNullProcedimentId = true or me.id = :procedimentId) " +
			"and (:esNullCodi = true or lower(grup.codi) like lower('%'||:codi||'%')) " +
			"and (:esNullDescripcio = true or lower(grup.descripcio) like lower('%'||:descripcio||'%')) " +
			"and (:esNullOrganGestorId = true or og.id = :organGestorId) " +
			"and (:esNullOrganGestorAscendentId = true " +
			"	  or og.id = :organGestorAscendentId " +
			"     or pare1.id = :organGestorAscendentId " +
			"     or pare2.id = :organGestorAscendentId " +
			"     or pare3.id = :organGestorAscendentId " +
			"     or pare4.id = :organGestorAscendentId)")
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
			@Param("esNullOrganGestorAscendentId") boolean esNullOrganGestorAscendentId,
			@Param("organGestorAscendentId") Long organGestorAscendentId);
	
	
	@Query(	"select distinct " +
			"    grup " +
			"from " +
			"    GrupEntity grup " +
			"    left join grup.metaExpedients me " +
			"    left join grup.organGestor og " +
			"    left join og.pare pare1 " +
			"    left join pare1.pare pare2 " + 
			"	 left join pare2.pare pare3 " +
			"	 left join pare3.pare pare4 " +
			"where " +
			"    grup.entitat = :entitat " +
			"and (:esNullProcedimentId = true or me.id = :procedimentId) " +
			"and (:esNullOrganGestorId = true " +
			"	  or og.id = :organGestorId " +
			"     or pare1.id = :organGestorId " +
			"     or pare2.id = :organGestorId " +
			"     or pare3.id = :organGestorId " +
			"     or pare4.id = :organGestorId)")
	List<GrupEntity> findByEntitatAndOrgan(
			@Param("entitat") EntitatEntity entitat, 
			@Param("esNullProcedimentId") boolean esNullProcedimentId,
			@Param("procedimentId") Long procedimentId,	
			@Param("esNullOrganGestorId") boolean esNullOrganGestorId,
			@Param("organGestorId") Long organGestorId);


	List<GrupEntity> findByEntitatId(
			@Param("entitatId") Long entitatId);
	

	GrupEntity findByEntitatIdAndCodi(
			@Param("entitatId") Long entitatId, 
			@Param("codi") String codi);

	
}
