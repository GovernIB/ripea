package es.caib.ripea.plugin.digitalitzacio;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DigitalitzacioResultat {

	private boolean error;
	private String errorDescripcio;
	private DigitalitzacioEstat estat;
	private byte[] contingut;
	private String nomDocument;
	private String mimeType;
	private String eniTipoFirma;
	private Integer resolucion;
	private String idioma;
	
	
}
