package es.caib.ripea.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import es.caib.ripea.persistence.entity.ExplotacioTempsEntity;

@Component
public interface ExplotacioTempsRepository extends JpaRepository<ExplotacioTempsEntity, Long> {}