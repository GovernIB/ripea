/**
 * 
 */
package es.caib.ripea.core.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.ripea.core.entity.CarpetaEntity;
import es.caib.ripea.core.entity.ExpedientEntity;

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
}
