/**
 * 
 */
package es.caib.ripea.core.api.dto;

import lombok.Getter;
import lombok.Setter;

/**
 *  Filtre per a la consulta de organs gestors
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class OrganGestorFiltreDto extends AuditoriaDto {
	private String codi;
	private String nom;
}
