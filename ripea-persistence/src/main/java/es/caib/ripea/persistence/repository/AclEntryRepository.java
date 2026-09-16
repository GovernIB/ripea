package es.caib.ripea.persistence.repository;

import es.caib.ripea.persistence.entity.AclEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface AclEntryRepository extends JpaRepository<AclEntryEntity, Long> {

}