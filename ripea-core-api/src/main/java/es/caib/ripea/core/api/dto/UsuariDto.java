/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;


/**
 * Informació d'un usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class UsuariDto implements Serializable {

	private String codi;
	private String nom;
	private String nif;
	private String email;
	private String emailAlternatiu;
	private String idioma;
	private String[] rols;
	private boolean rebreEmailsAgrupats;
	private String rolActual;
	private boolean rebreAvisosNovesAnotacions;
	
	public String getCodiAndNom() {
		return nom + " (" + codi + ")";
	}

	private static final long serialVersionUID = -139254994389509932L;

}
