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
    private String nom;
    private String entitatId;
    private String pareCodi;
    private String entitatNom;
    private List<PermisDto> permisos;

    public int getPermisosCount() {
        if (permisos == null)
            return 0;
        else
            return permisos.size();
    }
}
