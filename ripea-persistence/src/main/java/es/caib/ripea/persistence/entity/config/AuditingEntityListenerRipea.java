package es.caib.ripea.persistence.entity.config;

import java.time.LocalDateTime;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import es.caib.ripea.persistence.entity.RipeaAuditable;
import es.caib.ripea.persistence.entity.UsuariEntity;
import es.caib.ripea.persistence.repository.UsuariRepository;

@Configurable
public class AuditingEntityListenerRipea {

	@Autowired private UsuariRepository usuariRepository;

	public void setAuditingHandler(ObjectFactory<AuditingHandler> auditingHandler) {}

	@PrePersist
	public <T extends RipeaAuditable<Long>> void touchForCreate(T target) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UsuariEntity usuariEntity = usuariRepository.findById(authentication.getName()).get();
//		target.setCreatedBy(usuariEntity);
		target.setCreatedDate(LocalDateTime.now());
	}

	@PreUpdate
	public <T extends RipeaAuditable<Long>> void touchForUpdate(T target) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UsuariEntity usuariEntity = usuariRepository.findById(authentication.getName()).get();
//		target.setLastModifiedBy(usuariEntity);
		target.setLastModifiedDate(LocalDateTime.now());
	}
}
