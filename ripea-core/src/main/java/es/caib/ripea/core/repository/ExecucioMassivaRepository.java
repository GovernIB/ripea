/**
 * 
 */
package es.caib.ripea.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.ripea.core.entity.ExecucioMassivaEntity;
import es.caib.ripea.core.entity.UsuariEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus contingut.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExecucioMassivaRepository extends JpaRepository<ExecucioMassivaEntity, Long> {
	
	List<ExecucioMassivaEntity> findByCreatedByAndEntitatId(UsuariEntity createdBy, Long entitatId, Pageable pageable);
	
	List<ExecucioMassivaEntity> findByEntitatId(Long entitatId, Pageable pageable);
	
	@Query("select min(id) " +
			"from 	ExecucioMassivaEntity " +
			"where 	dataInici <= :ara " +
			"	and dataFi is null ")
	Long getNextMassiu(@Param("ara") Date ara);
	
	@Query("select e " +
			"from 	ExecucioMassivaEntity e " +
			"where 	dataInici <= :ara " +
			"	and dataFi is null " +
			"	order by e.id asc")
	List<ExecucioMassivaEntity> getMassivesPerProcessar(@Param("ara") Date ara);
	
	@Query("select min(id) " +
			"	from 	ExecucioMassivaEntity " +
			"	where 	dataInici <= :ara " +
			"	and dataFi is null")
	Long getMinExecucioMassiva(@Param("ara") Date ara);
}
