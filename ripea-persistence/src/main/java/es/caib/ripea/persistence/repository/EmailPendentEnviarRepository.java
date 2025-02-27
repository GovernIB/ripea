package es.caib.ripea.persistence.repository;

import es.caib.ripea.persistence.entity.EmailPendentEnviarEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface EmailPendentEnviarRepository extends JpaRepository<EmailPendentEnviarEntity, Long> {

	public List<EmailPendentEnviarEntity> findByOrderByDestinatariAscEventTipusEnumAsc();

}
