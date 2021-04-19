/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;


/**
 * Informació d'una dada.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class ViaFirmaEnviarDto implements Serializable {

	private String titol;
	private String descripcio;
	private String codiUsuariViaFirma;
	private String contrasenyaUsuariViaFirma;
	private ViaFirmaDispositiuDto viaFirmaDispositiu;
	private String signantNif;
	private String signantNom;
	private String observacions;
	private boolean isFirmaParcial; 
	private boolean validateCodeEnabled;
	private String validateCode;
	private boolean rebreCorreu;
	
	public Boolean isValidateCodeEnabled() {
		return validateCodeEnabled;
	}
	public Boolean isRebreCorreu() {
		return rebreCorreu;
	}
	
	private static final long serialVersionUID = 6451235953471494106L;
}
