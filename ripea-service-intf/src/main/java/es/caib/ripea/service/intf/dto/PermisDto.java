package es.caib.ripea.service.intf.dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

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
	private boolean procedimentsComuns;
	private boolean administration;
	private boolean administrationComuns;
	private boolean statistics;
	private boolean disseny;
	private Long organGestorId;
	private String organGestorNom;
	private String organGestorCodi;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		PermisDto permis = (PermisDto) obj;
		return (id.equals(permis.getId()) && principalNom.equals(permis.getPrincipalNom()));
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		return prime + ((id == null) ? 0 : id.hashCode());
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