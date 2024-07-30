package es.caib.ripea.core.api.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArbreJsonDto  implements Serializable {

	private String id;
	private String text;
	private ArbreJsonStateDto state;
	private List<ArbreJsonDto> children;
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		if (text != null) {
			this.text = text.trim();
		}
	}
	
	@Override
	public boolean equals(Object object) {
		ArbreJsonDto arbre = (ArbreJsonDto)object;
		return this.getId().equals(arbre.getId());
	}
	
	private static final long serialVersionUID = -1445293078129901997L;
	
}
