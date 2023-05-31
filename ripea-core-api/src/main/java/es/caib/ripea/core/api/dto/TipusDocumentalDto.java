/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;



@Getter @Setter
public class TipusDocumentalDto implements Serializable {
	
	private Long id;
	private String codi;
	private String codiEspecific;
	private String nomEspanyol;
	private String nomCatala;
	private String nom;
	private EntitatDto entitat;
	

	private static final long serialVersionUID = -139254994389509932L;

}
