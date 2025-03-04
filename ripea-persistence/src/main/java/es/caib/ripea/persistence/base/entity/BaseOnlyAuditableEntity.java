package es.caib.ripea.persistence.base.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Base per a definir les demés entitats de l'aplicació.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseOnlyAuditableEntity implements AuditableEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private @Nullable Long id;

	@CreatedBy
	@Column(name = "createdby_codi", length = 64, nullable = false)
	private String createdBy;
	@CreatedDate
	@Column(name = "createddate", nullable = false)
	private LocalDateTime createdDate;
	@LastModifiedBy
	@Column(name = "lastmodifiedby_codi", length = 64)
	private String lastModifiedBy;
	@LastModifiedDate
	@Column(name = "lastmodifieddate")
	private LocalDateTime lastModifiedDate;

	public Long getId() {
		return id;
	}

	public boolean isNew() {
		return null == getId();
	}

	@Override
	public void updateCreated(
			String createdBy,
			LocalDateTime createdDate) {
		this.createdBy = createdBy;
		this.createdDate = (createdDate != null) ? createdDate : LocalDateTime.now();
	}

	@Override
	public void updateLastModified(
			String lastModifiedBy,
			LocalDateTime lastModifiedDate) {
		this.lastModifiedBy = lastModifiedBy;
		this.lastModifiedDate = (lastModifiedDate != null) ? lastModifiedDate : LocalDateTime.now();
	}

}