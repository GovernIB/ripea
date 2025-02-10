/**
 * 
 */
package es.caib.ripea.service.intf.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Map;


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
