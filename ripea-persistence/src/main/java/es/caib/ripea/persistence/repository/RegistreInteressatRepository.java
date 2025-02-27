package es.caib.ripea.persistence.repository;

import es.caib.ripea.persistence.entity.RegistreInteressatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface RegistreInteressatRepository extends JpaRepository<RegistreInteressatEntity, Long> {
	RegistreInteressatEntity findByRepresentant(RegistreInteressatEntity representant);
}