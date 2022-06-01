/**
 * 
 */
package es.caib.ripea.core.repository;

import es.caib.ripea.core.entity.ExecucioMassivaContingutEntity;
import es.caib.ripea.core.entity.ExecucioMassivaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus contingut.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExecucioMassivaContingutRepository extends JpaRepository<ExecucioMassivaContingutEntity, Long> {
	
	public List<ExecucioMassivaContingutEntity> findByExecucioMassivaOrderByOrdreAsc (ExecucioMassivaEntity execucioMassiva);
	
	@Query("select min(e.id) " +
			"from	ExecucioMassivaContingutEntity e " +
			"where	e.execucioMassiva.id = :nextMassiu " +
			"   and	e.dataFi is null ")
	public Long findNextExecucioMassivaContingut(@Param("nextMassiu") Long nextMassiu);
	
	@Query("select min(e.id) " +
			"from	ExecucioMassivaContingutEntity e " +
			"where	e.execucioMassiva.id =	" +
			"			(select min(id) " +
			"			 from 	ExecucioMassivaEntity " +
			"			 where 	dataInici <= :ara " +
			"					and dataFi is null) " +
			"	and	e.dataFi is null ")
	public Long findExecucioMassivaContingutId(@Param("ara") Date ara);



	// Mètodes per evitar errors al tenir continguts orfes en base de dades
	// ////////////////////////////////////////////////////////////////////

	@Modifying
	@Query(value = "delete from ipa_massiva_contingut where contingut_id = :contingutId ", nativeQuery = true)
	int deleteExecucioMassivaFromContingutsOrfes(@Param("contingutId") Long contingutId);

}
