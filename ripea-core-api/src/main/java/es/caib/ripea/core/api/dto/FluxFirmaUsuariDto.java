/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * Informació d'un flux de firma a crear.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class FluxFirmaUsuariDto extends AuditoriaDto implements Serializable {
	
	private Long id;
	private String nom;
	private String descripcio;
	private String portafirmesFluxId;
	
	private String destinataris;
	
	@Override
	public String toString() {
		return "FluxFirmaUsuariDto [id=" + id + ", nom=" + nom + ", descripcio=" + descripcio + ", portafirmesFluxId="
				+ portafirmesFluxId + ", destinataris=" + destinataris + "]";
	}

	private static final long serialVersionUID = 4317065244907253143L;

}
