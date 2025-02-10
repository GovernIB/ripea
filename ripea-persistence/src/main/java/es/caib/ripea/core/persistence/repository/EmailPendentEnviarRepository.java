/**
 * 
 */
package es.caib.ripea.core.persistence.repository;

import es.caib.ripea.core.persistence.entity.EmailPendentEnviarEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmailPendentEnviarRepository extends JpaRepository<EmailPendentEnviarEntity, Long> {

	public List<EmailPendentEnviarEntity> findByOrderByDestinatariAscEventTipusEnumAsc();

}
