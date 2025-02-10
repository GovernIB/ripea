/**
 * 
 */
package es.caib.ripea.service.intf.dto;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació del moviment d'un contenidor.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ContingutComentariDto  extends AuditoriaDto {

	
	private Long id;
	private ContingutDto contingut;
	private String text;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public ContingutDto getContingut() {
		return contingut;
	}
	public void setContingut(ContingutDto contingut) {
		this.contingut = contingut;
	}

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
