/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class UsuariTascaFiltreDto implements Serializable {

	private TascaEstatEnumDto estat;

	private static final long serialVersionUID = 1L;
}
