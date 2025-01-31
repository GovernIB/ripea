package es.caib.ripea.core.api.dto;

import java.io.Serializable;
import lombok.Data;

@Data
/**
 * El GenericDto es pot utilitzar als controladors per mostrar o enviar dades senzilles a la vista.
 * Evita haver de crear diferents dtos amb els mateixos tipus de camps.
 */
public class GenericDto implements Serializable {
	
	private static final long serialVersionUID = -4522941921588083446L;
	
	private Long id;
	private String codi;
	private String texte;
	private Object[] arguments;
	
	public GenericDto() {
		super();
	}
	
	public GenericDto(Long id, String codi, String texte) {
		super();
		this.id = id;
		this.codi = codi;
		this.texte = texte;
	}
	
	public GenericDto(String codi, String texte, Object[] arguments) {
		super();
		this.codi = codi;
		this.texte = texte;
		this.arguments = arguments;
	}

	@Override
	public String toString() {
		return "GenericDto [id=" + id + ", codi=" + codi + ", texte=" + texte + "]";
	}
}