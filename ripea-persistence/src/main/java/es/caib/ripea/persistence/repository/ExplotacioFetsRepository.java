package es.caib.ripea.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import es.caib.ripea.persistence.entity.ExplotacioFetsEntity;

@Component
public interface ExplotacioFetsRepository extends JpaRepository<ExplotacioFetsEntity, Long> {}