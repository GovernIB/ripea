/**
 * 
 */
package es.caib.ripea.service.intf.dto;

import java.util.Date;


/**
 * Informació d'una versió de document.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DocumentVersioDto extends AuditoriaDto {

	private String arxiuUuid;
	private String id;
	private Date data;

	public String getArxiuUuid() {
		return arxiuUuid;
	}
	public void setArxiuUuid(String arxiuUuid) {
		this.arxiuUuid = arxiuUuid;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}

}
