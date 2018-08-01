/**
 * 
 */
package es.caib.ripea.core.repository;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.ripea.core.api.registre.RegistreProcesEstatEnum;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.RegistreEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface RegistreRepository extends JpaRepository<RegistreEntity, Long> {
	
	@Query(	"select " +
			"    r " +
			"from " +
			"    RegistreEntity r " +
			"where " +
			"    r.entitat = :entitat " +
			"	and (:esNullUnitatOrganitzativa = true or r.unitatAdministrativa = :unitatOrganitzativa) " +
			"	and (:esNullDataInici = true or r.data >= :dataInici) " +
			"	and (:esNullDataFi = true or r.data <= :dataFi) " +
			"	and (:esNullProcesEstat = true or r.procesEstat = :procesEstat) " +
		    "order by r.data desc")
	public Page<RegistreEntity> findByFiltrePaginat(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullUnitatOrganitzativa") boolean esNullUnitatOrganitzativa,
			@Param("unitatOrganitzativa") String unitatOrganitzativa,
			@Param("esNullDataInici") boolean esNullDataInici,
			@Param("dataInici") Date dataInici,
			@Param("esNullDataFi") boolean esNullDataFi,
			@Param("dataFi") Date dataFi,
			@Param("esNullProcesEstat") boolean esNullProcesEstat, 
			@Param("procesEstat") RegistreProcesEstatEnum procesEstat,
			Pageable pageable);

}
