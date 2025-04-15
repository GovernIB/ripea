package es.caib.ripea.persistence.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.springframework.data.domain.Auditable;

/**
 * Classe basse de on extendre per a activar les auditories.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@MappedSuperclass
public class RipeaAuditable<PK extends Serializable> extends RipeaPersistable<PK> implements Auditable<String, PK, LocalDateTime> {

	@Column(name = "createdby_codi", length = 64, nullable = false)
	private String createdBy;

	@Column(name = "createddate", nullable = false)
	private LocalDateTime createdDate;

	@Column(name = "lastmodifiedby_codi", length = 64)
	private String lastModifiedBy;

	@Column(name = "lastmodifieddate")
	private LocalDateTime lastModifiedDate;

	@Override
	public Optional<String> getCreatedBy() {
		return this.createdBy==null?null:Optional.of(this.createdBy);
	}

	@Override
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	@Override
	public Optional<LocalDateTime> getCreatedDate() {
		return Optional.of(this.createdDate);
	}

	@Override
	public void setCreatedDate(LocalDateTime creationDate) {
		this.createdDate = creationDate;
	}

	@Override
	public Optional<String> getLastModifiedBy() {
		return this.lastModifiedBy==null?null:Optional.of(this.lastModifiedBy);
	}

	@Override
	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	@Override
	public Optional<LocalDateTime> getLastModifiedDate() {
		return Optional.of(this.lastModifiedDate);
	}

	@Override
	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

}