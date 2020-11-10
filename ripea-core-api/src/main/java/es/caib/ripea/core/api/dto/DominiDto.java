/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.io.Serializable;

import lombok.EqualsAndHashCode;
import lombok.EqualsAndHashCode.Include;
import lombok.Getter;
import lombok.Setter;

/**
 * Informació d'un Domini.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter @EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class DominiDto implements Serializable {
	
	private Long id;
	@Include
	private String codi;
	private String nom;
	private String descripcio;
	private Long entitatId;
	private String consulta;
	private String cadena;
	private String contrasenya;

	private static final long serialVersionUID = 4550970703735934708L;

}
