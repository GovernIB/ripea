/**
 * 
 */
package es.caib.ripea.core.repository;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.ripea.core.entity.EmailPendentEnviarEntity;


public interface EmailPendentEnviarRepository extends JpaRepository<EmailPendentEnviarEntity, Long> {
	
	public List<EmailPendentEnviarEntity> findByOrderByDestinatariAscEventTipusEnumAsc();


}
