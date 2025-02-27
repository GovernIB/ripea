package es.caib.ripea.persistence.repository;

import es.caib.ripea.persistence.entity.DispositiuEnviamentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface DispositiuEnviamentRepository extends JpaRepository<DispositiuEnviamentEntity, Long> {}