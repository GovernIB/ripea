/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

import lombok.Getter;
import lombok.Setter;

/**
 * Informació d'un permís.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class PermisDto implements Serializable {

	private Serializable id;
	private String principalNom;
	private String principalCodiNom;
	private PrincipalTipusEnumDto principalTipus;
	private boolean read;
	private boolean write;
	private boolean create;
	private boolean delete;
	private boolean administration;

	private Long organGestorId;
	private String organGestorNom;
	private String organGestorCodi;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public boolean getAmbOrganGestor() {
		return organGestorId != null;
	}

	public String getOrganGestorCodiINom() {
		String organGestorComplet = "";
		if (organGestorNom != null) {
			String nomOrganGestor = organGestorNom;
			String codiOrganGestor = organGestorCodi;
			if (codiOrganGestor != null && !codiOrganGestor.isEmpty())
				organGestorComplet += codiOrganGestor + " - ";
			if (nomOrganGestor != null && !nomOrganGestor.isEmpty())
				organGestorComplet += nomOrganGestor;
		}
		return organGestorComplet;
	}
	
	private static final long serialVersionUID = -139254994389509932L;

}
