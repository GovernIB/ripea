/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * Informació d'un URL de instrucció a generar.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class URLInstruccionDto implements Serializable {
	
	private Long id;
	private String nom;
	private String descripcio;
	private String codi;
	private String url;

	private static final long serialVersionUID = 5315186101841925830L;

}
