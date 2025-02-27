package es.caib.ripea.persistence.repository;

import es.caib.ripea.persistence.entity.CarpetaEntity;
import es.caib.ripea.persistence.entity.ContingutEntity;
import es.caib.ripea.persistence.entity.ExpedientEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface CarpetaRepository extends JpaRepository<CarpetaEntity, Long> {
	List<CarpetaEntity> findByPare(ExpedientEntity expedient);
	
	List<CarpetaEntity> findByPareAndEsborrat(
			CarpetaEntity expedient, 
			int esborrat, 
			Sort sort); 
	CarpetaEntity findByPareAndExpedientRelacionatAndEsborrat(
			ContingutEntity pare,
			ExpedientEntity expedient, 
			int esborrat);

    List<CarpetaEntity> findByExpedientAndEsborrat(ExpedientEntity expedient, int esborrat);
}
