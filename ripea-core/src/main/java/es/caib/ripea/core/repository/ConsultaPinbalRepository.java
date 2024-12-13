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

import es.caib.ripea.core.api.dto.ConsultaPinbalEstatEnumDto;
import es.caib.ripea.core.entity.ConsultaPinbalEntity;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.UsuariEntity;


public interface ConsultaPinbalRepository extends JpaRepository<ConsultaPinbalEntity, Long> {


	
	@Query(	"select " +
			"    cp " +
			"from " +
			"    ConsultaPinbalEntity cp " +
			"    left join cp.expedient exp " +			
			"    left join cp.metaExpedient metaexp " +	
			"where " +
			"    (cp.entitat = :entitat) " +
			"and (:esNullExpedientId = true or exp.id = :expedientId) " +
			"and (:esNullMetaExpedientId = true or metaexp.id = :metaExpedientId) " +
			"and (:esNullServei = true or cp.servei = :servei) " +
			"and (:esNullCreatPer = true or cp.createdBy = :creatPer) " +
			"and (:esNullDataInici = true or cp.createdDate >= :dataInici) " +
			"and (:esNullDataFinal = true or cp.createdDate <= :dataFinal) " +
			"and (:esNullEstat = true or cp.estat = :estat) ")
	public Page<ConsultaPinbalEntity> findAmbFiltrePaginat(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullExpedientId") boolean esNullExpedientId,
			@Param("expedientId") Long expedientId,
			@Param("esNullMetaExpedientId") boolean esNullMetaExpedientId,
			@Param("metaExpedientId") Long metaExpedientId,
			@Param("esNullServei") boolean esNullServei,
			@Param("servei") String servei,
			@Param("esNullCreatPer") boolean esNullCreatPer,
			@Param("creatPer") UsuariEntity creatPer,
			@Param("esNullDataInici") boolean esNullDataInici,
			@Param("dataInici") Date dataInici,
			@Param("esNullDataFinal") boolean esNullDataFinal,
			@Param("dataFinal") Date dataFinal,
			@Param("esNullEstat") boolean esNullEstat,
			@Param("estat") ConsultaPinbalEstatEnumDto estat,
			Pageable paginacio);

	
}
