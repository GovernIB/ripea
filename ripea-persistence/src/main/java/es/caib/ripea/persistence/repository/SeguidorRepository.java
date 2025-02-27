package es.caib.ripea.persistence.repository;

import es.caib.ripea.persistence.entity.InteressatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface SeguidorRepository extends JpaRepository<InteressatEntity, Long> {}
