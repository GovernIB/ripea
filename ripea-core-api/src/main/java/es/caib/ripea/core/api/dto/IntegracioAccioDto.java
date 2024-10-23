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


/**
 * Acció realitzada sobre una integració.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@ToString
@Getter
@Setter
public class IntegracioAccioDto implements Serializable {

	private Long index;
	private Long timestamp;
	private Date data;
	private String descripcio;
	private String endpoint;
	private Map<String, String> parametres;
	private IntegracioDto integracio;
	private IntegracioAccioTipusEnumDto tipus;
	private long tempsInici;
	private long tempsResposta;
	private IntegracioAccioEstatEnumDto estat;
	private EntitatDto entitat;
	private String entitatCodi;
	private String errorDescripcio;
	private String excepcioMessage;
	private String excepcioStacktrace;

	public int getParametresCount() {
		if (parametres == null) {
			return 0;
		} else {
			return parametres.size();
		}
	}

	public IntegracioAccioDto(){
		super();
	}

	public IntegracioAccioDto(
			String descripcio,
			Map<String, String> parametres,
			long tempsInici) {
		super();
		this.descripcio = descripcio;
		this.parametres = parametres;
		this.tempsInici = tempsInici;
	}

	private static final long serialVersionUID = -139254994389509932L;

}
