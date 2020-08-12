/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Informació d'una dada.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class OrganGestorDto extends AuditoriaDto {
    private Long id;
    private String codi;
    private String codiDir3;
    private String nom;
    private String entitatId;
    private String entitatNom;
    private List<PermisDto> permisos;

    public int getPermisosCount() {
	if (permisos == null)
	    return 0;
	else
	    return permisos.size();
    }

    private static final long serialVersionUID = -2393511650074099319L;
}
