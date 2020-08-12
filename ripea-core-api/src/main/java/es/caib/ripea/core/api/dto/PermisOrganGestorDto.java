/**
 * 
 */
package es.caib.ripea.core.api.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Informació del permís d'un organ gestor.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class PermisOrganGestorDto extends PermisDto {

    private OrganGestorDto organGestor;

    private static final long serialVersionUID = -139254994389509932L;

}
