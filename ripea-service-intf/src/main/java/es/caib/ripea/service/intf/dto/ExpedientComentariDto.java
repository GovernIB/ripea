package es.caib.ripea.service.intf.dto;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació del moviment d'un contenidor.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientComentariDto  extends AuditoriaDto {

	
	private Long id;
//	private ExpedientDto expedient;
	private String text;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
//	public ExpedientDto getExpedient() {
//		return expedient;
//	}
//	public void setExpedient(ExpedientDto expedient) {
//		this.expedient = expedient;
//	}

	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}
