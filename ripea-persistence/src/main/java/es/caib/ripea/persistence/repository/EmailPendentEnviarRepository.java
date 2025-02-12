/**
 * 
 */
package es.caib.ripea.persistence.repository;

import es.caib.ripea.persistence.entity.EmailPendentEnviarEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmailPendentEnviarRepository extends JpaRepository<EmailPendentEnviarEntity, Long> {

	public List<EmailPendentEnviarEntity> findByOrderByDestinatariAscEventTipusEnumAsc();

}
