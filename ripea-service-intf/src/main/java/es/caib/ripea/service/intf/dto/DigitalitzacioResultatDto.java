package es.caib.ripea.service.intf.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DigitalitzacioResultatDto {

	private boolean error;
	private String errorDescripcio;
	private DigitalitzacioEstatDto estat;
	private byte[] contingut;
	private String nomDocument;
	private String mimeType;
	private String eniTipoFirma;
	private Integer resolucion;
	private String idioma;
	

		
}
