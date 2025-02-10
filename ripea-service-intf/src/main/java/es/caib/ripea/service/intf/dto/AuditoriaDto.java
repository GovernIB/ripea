/**
 * 
 */
package es.caib.ripea.service.intf.dto;

import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Informació d'auditoria.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
public class AuditoriaDto {

	private static final String DATETIME_FORMAT = "dd/MM/yyyy HH:mm";
	private static final String DATE_FORMAT = "dd/MM/yyyy";

	private UsuariDto createdBy;
	private Date createdDate;
	private UsuariDto lastModifiedBy;
	private Date lastModifiedDate;

	public String getCreatedDateAmbFormat() {
		return formatDateTime(createdDate);
	}
	public String getLastModifiedDateAmbFormat() {
		return formatDateTime(lastModifiedDate);
	}
	
	protected static String formatDateTime(Date date) {
		if (date == null)
			return null;
		SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_FORMAT);
		return sdf.format(date);
	}
	
	protected static String formatDate(Date date) {
		if (date == null)
			return null;
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		return sdf.format(date);
	}
}
