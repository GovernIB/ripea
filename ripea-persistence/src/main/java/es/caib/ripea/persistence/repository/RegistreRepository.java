package es.caib.ripea.persistence.repository;

import es.caib.ripea.persistence.entity.RegistreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface RegistreRepository extends JpaRepository<RegistreEntity, Long> {}