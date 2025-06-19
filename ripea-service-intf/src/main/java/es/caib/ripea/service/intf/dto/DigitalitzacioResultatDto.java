package es.caib.ripea.service.intf.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DigitalitzacioResultatDto implements Serializable {
	private static final long serialVersionUID = 8625490921780066599L;
	private boolean error;
	private String errorDescripcio;
	private DigitalitzacioEstatDto estat;
	private byte[] contingut;
	private String nomDocument;
	private String mimeType;
	private String eniTipoFirma;
	private Integer resolucion;
	private String idioma;
	private String usuari; //Usuari que ha iniciat el proces de firma
}