package es.caib.ripea.service.intf.base.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Transient;

import com.fasterxml.jackson.annotation.JsonProperty;

import es.caib.ripea.service.intf.utils.Utils;

/**
 * Implementació base de la interfície de recurs.
 *
 * @param <ID> el tipus de la clau primària del recurs.
 */
@SuppressWarnings("serial")
public class BaseAuditableResource<ID extends Serializable> implements Resource<ID> {

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected ID id;
	
	private String createdBy;
	private LocalDateTime createdDate;
	private String lastModifiedBy;
	private LocalDateTime lastModifiedDate;
	
    @Transient private String createdByFullName;
    @Transient private String lastModifiedByFullName;
	
	@Override
	public ID getId() {
		return id;
	}

	@Override
	public void setId(ID id) {
		this.id = id;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public String getCreatedByFullName() {
		if (Utils.hasValue(this.createdByFullName))
			return createdByFullName;
		else
			return createdBy;
	}

	public void setCreatedByFullName(String createdByFullName) {
		this.createdByFullName = createdByFullName;
	}

	public String getLastModifiedByFullName() {
		if (Utils.hasValue(this.lastModifiedByFullName))
			return lastModifiedByFullName;
		else
			return lastModifiedBy;
	}

	public void setLastModifiedByFullName(String lastModifiedByFullName) {
		this.lastModifiedByFullName = lastModifiedByFullName;
	}

}
