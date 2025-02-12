/**
 * 
 */
package es.caib.ripea.persistence.repository;

import es.caib.ripea.persistence.entity.CarpetaEntity;
import es.caib.ripea.persistence.entity.ContingutEntity;
import es.caib.ripea.persistence.entity.ExpedientEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus carpeta.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
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
