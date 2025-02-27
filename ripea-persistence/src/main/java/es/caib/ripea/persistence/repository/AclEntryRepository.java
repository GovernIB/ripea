package es.caib.ripea.persistence.repository;

import es.caib.ripea.persistence.entity.AclEntryEntity;
import es.caib.ripea.persistence.entity.AclObjectIdentityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface AclEntryRepository extends JpaRepository<AclEntryEntity, Long> {

    List<AclEntryEntity> findByAclObjectIdentity(AclObjectIdentityEntity objectIdentity);
}