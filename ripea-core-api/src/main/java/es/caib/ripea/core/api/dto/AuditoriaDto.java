/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Informació d'auditoria.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AuditoriaDto {

	private static final String DATE_FORMAT = "dd/MM/yyyy HH:mm";

	private UsuariDto createdBy;
	private Date createdDate;
	private UsuariDto lastModifiedBy;
	private Date lastModifiedDate;

	public UsuariDto getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(UsuariDto createdBy) {
		this.createdBy = createdBy;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public UsuariDto getLastModifiedBy() {
		return lastModifiedBy;
	}
	public void setLastModifiedBy(UsuariDto lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}
	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public String getCreatedDateAmbFormat() {
		if (createdDate == null)
			return null;
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		return sdf.format(createdDate);
	}
	public String getLastModifiedDateAmbFormat() {
		if (lastModifiedDate == null)
			return null;
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		return sdf.format(lastModifiedDate);
	}

}
