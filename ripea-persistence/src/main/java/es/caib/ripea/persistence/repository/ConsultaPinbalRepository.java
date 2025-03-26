package es.caib.ripea.persistence.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import es.caib.ripea.persistence.entity.ConsultaPinbalEntity;
import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.UsuariEntity;
import es.caib.ripea.service.intf.dto.ConsultaPinbalEstatEnumDto;

@Component
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
			@Param("dataInici") LocalDateTime dataInici,
			@Param("esNullDataFinal") boolean esNullDataFinal,
			@Param("dataFinal") LocalDateTime dataFinal,
			@Param("esNullEstat") boolean esNullEstat,
			@Param("estat") ConsultaPinbalEstatEnumDto estat,
			Pageable paginacio);

	
}
