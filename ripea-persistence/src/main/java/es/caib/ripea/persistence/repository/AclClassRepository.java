package es.caib.ripea.persistence.repository;

import es.caib.ripea.persistence.entity.AclClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface AclClassRepository extends JpaRepository<AclClassEntity, Long> {

	AclClassEntity findByClassname(String classname);

}