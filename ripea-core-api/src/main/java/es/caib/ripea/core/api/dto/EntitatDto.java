/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

import lombok.Getter;
import lombok.Setter;

/**
 * Informació d'una entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class EntitatDto extends AuditoriaDto {

	private Long id;
	private String codi;
	private String nom;
	private String descripcio;
	private String cif;
	private String unitatArrel;
	private boolean activa;

	private List<PermisDto> permisos;
	private List<OrganGestorDto> organsGestors;
	
	private boolean usuariActualRead;
	private boolean usuariActualAdministration;
	
	private byte[] logoImgBytes;
	private String capsaleraColorFons;
	private String capsaleraColorLletra;
	
	public boolean isUsuariActualTeOrgans() {
		return organsGestors != null && !organsGestors.isEmpty();
	}
	
	public int getPermisosCount() {
		if  (permisos == null)
			return 0;
		else
			return permisos.size();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
