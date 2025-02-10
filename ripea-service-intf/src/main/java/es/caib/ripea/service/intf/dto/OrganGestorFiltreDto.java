/**
 * 
 */
package es.caib.ripea.service.intf.dto;

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
	private Long pareId;
    private OrganEstatEnumDto estat;
}
