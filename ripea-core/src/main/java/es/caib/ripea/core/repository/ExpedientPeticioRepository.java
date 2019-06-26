/**
 * 
 */
package es.caib.ripea.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.ripea.core.api.dto.ExpedientPeticioAccioEnumDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioEstatEnumDto;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.ExpedientPeticioEntity;

public interface ExpedientPeticioRepository extends JpaRepository<ExpedientPeticioEntity, Long> {

	ExpedientPeticioEntity findByIdentificador(
			String identificador);

	List<ExpedientPeticioEntity> findByEstatAndConsultaWsErrorIsFalse(
			ExpedientPeticioEstatEnumDto estat);
	
	
	List<ExpedientPeticioEntity> findByExpedient(
			ExpedientEntity expedient);

	@Query("from" +
			"    ExpedientPeticioEntity e " +
			"where " +
			"e.registre.entitat = :entitat " +
			"and (:esNullProcediment = true or lower(e.registre.procedimentCodi) like lower('%'||:procediment||'%')) " +
			"and (:esNullNumero = true or lower(e.registre.identificador) like lower('%'||:numero||'%')) " +
			"and (:esNullExtracte = true or lower(e.registre.extracte) like lower('%'||:extracte||'%')) " +
			"and (:esNullDestinacio = true or lower(e.registre.destiDescripcio) like lower('%'||:destinacio||'%')) " + 
			"and (:esNullDataInicial = true or e.registre.data >= :dataInicial) " +
			"and (:esNullDataFinal = true or e.registre.data <= :dataFinal) " +
			"and (:esNullEstat = true or e.estat = :estat) " +
			"and (:esNullAccio = true or e.expedientPeticioAccioEnumDto = :accio) "
			)
	Page<ExpedientPeticioEntity> findByEntitatAndFiltre(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullProcediment") boolean esNullProcediment,
			@Param("procediment") String procediment,
			@Param("esNullNumero") boolean esNullNumero,
			@Param("numero") String numero,
			@Param("esNullExtracte") boolean esNullExtracte,
			@Param("extracte") String extracte,
			@Param("esNullDestinacio") boolean esNullDestinacio,
			@Param("destinacio") String destinacio,
			@Param("esNullDataInicial") boolean esNullDataInicial,
			@Param("dataInicial") Date dataInicial,
			@Param("esNullDataFinal") boolean esNullDataFinal,
			@Param("dataFinal") Date dataFinal,
			@Param("esNullEstat") boolean esNullEstat,
			@Param("estat") ExpedientPeticioEstatEnumDto estat,
			@Param("esNullAccio") boolean esNullAccio,
			@Param("accio") ExpedientPeticioAccioEnumDto accio,
			Pageable pageable);

	

}
