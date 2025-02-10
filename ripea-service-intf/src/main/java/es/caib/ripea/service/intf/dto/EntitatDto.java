/**
 * 
 */
package es.caib.ripea.service.intf.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.List;

/**
 * Informació d'una entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class EntitatDto extends AuditoriaDto {

	private Long id;
	@EqualsAndHashCode.Include
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
	private boolean logo;
	private String capsaleraColorFons;
	private String capsaleraColorLletra;
    private boolean permetreEnviamentPostal;
	
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
