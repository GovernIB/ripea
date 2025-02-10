/**
 * 
 */
package es.caib.ripea.service.intf.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Informació d'un URL de instrucció a generar.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class URLInstruccioDto implements Serializable {
	
	private Long id;
	private String nom;
	private String descripcio;
	private String codi;
	private String url;

	private static final long serialVersionUID = 5315186101841925830L;

}
