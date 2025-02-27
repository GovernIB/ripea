package es.caib.ripea.persistence.repository;

import es.caib.ripea.persistence.entity.ViaFirmaUsuariEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface ViaFirmaUsuariRepository extends JpaRepository<ViaFirmaUsuariEntity, String> {}