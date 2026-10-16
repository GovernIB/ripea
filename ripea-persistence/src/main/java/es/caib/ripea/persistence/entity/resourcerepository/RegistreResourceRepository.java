package es.caib.ripea.persistence.entity.resourcerepository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.ripea.persistence.base.repository.BaseRepository;
import es.caib.ripea.persistence.entity.resourceentity.RegistreResourceEntity;

public interface RegistreResourceRepository extends BaseRepository<RegistreResourceEntity, Long> {

	//Carrega d'una vegada els registres indicats amb els seus interessats (perspectiva REGISTRE dels llistats
	//d'anotacions), en lloc d'una consulta per anotació. Màxim 1000 ids per crida (límit IN d'Oracle).
	@Query("select r from RegistreResourceEntity r left join fetch r.interessats where r.id in (:registreIds)")
	List<RegistreResourceEntity> fetchAmbInteressats(@Param("registreIds") List<Long> registreIds);
}
