package es.caib.ripea.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import es.caib.ripea.persistence.entity.ExplotacioDimensioEntity;

@Component
public interface ExplotacioDimensioRepository extends JpaRepository<ExplotacioDimensioEntity, Long> {
	public ExplotacioDimensioEntity findByEntitatIdAndProcedimentIdAndOrganGestorIdAndUsuariCodi(
			@Param("entitatId") Long entitatId,
			@Param("procedimentId") Long procedimentId,
			@Param("organGestorId") Long organGestorId,
			@Param("usuariCodi") String usuariCodi);
}