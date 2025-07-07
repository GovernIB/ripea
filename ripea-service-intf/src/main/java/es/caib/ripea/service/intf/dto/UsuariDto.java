/**
 * 
 */
package es.caib.ripea.service.intf.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

import es.caib.ripea.service.intf.utils.Utils;


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
	private boolean rebreEmailsCanviEstatRevisio;
	private Long numElementsPagina;
	private boolean expedientListDataDarrerEnviament;
	private boolean expedientListAgafatPer;
	private boolean expedientListInteressats;
	private boolean expedientListComentaris;
	private boolean expedientListGrup;
	private Long procedimentId;
	private Long entitatPerDefecteId;
	private ContingutVistaEnumDto vistaActual;
	private boolean expedientExpandit;
	private MoureDestiVistaEnumDto vistaMoureActual;
	
	
	public String getCodiAndNom() {
		if (codi!=null && codi.equals(nom)) {
			return nom;
		} else {
			return nom + " (" + codi + ")";
		}
	}
	
	public String getNifOfuscat() {
		return Utils.nifMask(nif);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
