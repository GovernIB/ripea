package es.caib.ripea.persistence.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.domain.Auditable;
import org.springframework.lang.Nullable;

/**
 * Classe basse de on extendre per a activar les auditories.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@MappedSuperclass
public class RipeaAuditable<PK extends Serializable> extends RipeaPersistable<PK> implements Auditable<UsuariEntity, PK, LocalDateTime> {

	@ManyToOne
	private @Nullable UsuariEntity createdBy;

	@Temporal(TemporalType.TIMESTAMP)
	private @Nullable Date createdDate;

	@ManyToOne //
	private @Nullable UsuariEntity lastModifiedBy;

	@Temporal(TemporalType.TIMESTAMP)
	private @Nullable Date lastModifiedDate;

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Auditable#getCreatedBy()
	 */
	@Override
	public Optional<UsuariEntity> getCreatedBy() {
		return Optional.ofNullable(createdBy);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Auditable#setCreatedBy(java.lang.Object)
	 */
	@Override
	public void setCreatedBy(UsuariEntity createdBy) {
		this.createdBy = createdBy;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Auditable#getCreatedDate()
	 */
	@Override
	public Optional<LocalDateTime> getCreatedDate() {
		return null == createdDate ? Optional.empty()
				: Optional.of(LocalDateTime.ofInstant(createdDate.toInstant(), ZoneId.systemDefault()));
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Auditable#setCreatedDate(java.time.temporal.TemporalAccessor)
	 */
	@Override
	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = Date.from(createdDate.atZone(ZoneId.systemDefault()).toInstant());
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Auditable#getLastModifiedBy()
	 */
	@Override
	public Optional<UsuariEntity> getLastModifiedBy() {
		return Optional.ofNullable(lastModifiedBy);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Auditable#setLastModifiedBy(java.lang.Object)
	 */
	@Override
	public void setLastModifiedBy(UsuariEntity lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Auditable#getLastModifiedDate()
	 */
	@Override
	public Optional<LocalDateTime> getLastModifiedDate() {
		return null == lastModifiedDate ? Optional.empty()
				: Optional.of(LocalDateTime.ofInstant(lastModifiedDate.toInstant(), ZoneId.systemDefault()));
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Auditable#setLastModifiedDate(java.time.temporal.TemporalAccessor)
	 */
	@Override
	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = Date.from(lastModifiedDate.atZone(ZoneId.systemDefault()).toInstant());
	}

}
