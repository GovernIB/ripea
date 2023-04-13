/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@ToString
@Getter
@Setter
public class IntegracioAccioBuilderDto implements Serializable {

	private String descripcio;
	private Map<String, String> parametres;

	public IntegracioAccioBuilderDto(
			String descripcio,
			Map<String, String> parametres) {
		super();
		this.descripcio = descripcio;
		this.parametres = parametres;
	}

	private static final long serialVersionUID = -139254994389509932L;

}
