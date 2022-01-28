package es.caib.ripea.core.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.GrupEntity;

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
	
	@Query(	"from " +
			"    GrupEntity grup " +
			"where " +
			"    grup.entitat = :entitat " +
			"and grup.rol = :rol " +
			"and grup.descripcio = :descripcio ")
	List<GrupEntity> findByRolDescricpio(
			@Param("entitat") EntitatEntity entitat, 
			@Param("rol") String rol,
			@Param("descripcio") String descripcio);

	
}
