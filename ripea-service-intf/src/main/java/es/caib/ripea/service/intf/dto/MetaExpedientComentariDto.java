package es.caib.ripea.service.intf.dto;

import org.apache.commons.lang.builder.ToStringBuilder;


public class MetaExpedientComentariDto extends AuditoriaDto {

	
	private Long id;
	private String text;

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
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}
