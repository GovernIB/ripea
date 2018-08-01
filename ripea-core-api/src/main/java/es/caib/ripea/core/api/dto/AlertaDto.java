/**
 * 
 */
package es.caib.ripea.core.api.dto;

/**
 * Informació d'una alerta.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AlertaDto {
	
	private Long id;
	private String text;
	private String error;
	private boolean llegida;
	private Long contingutId;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	
	public boolean isLlegida() {
		return llegida;
	}
	public void setLlegida(boolean llegida) {
		this.llegida = llegida;
	}
	
	public Long getContingutId() {
		return contingutId;
	}
	public void setContingutId(Long contingutId) {
		this.contingutId = contingutId;
	}
	
}
