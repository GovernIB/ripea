package es.caib.ripea.service.intf.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter @Setter
public class DocumentEnviamentInteressatDto {
	
	private Long id;
	protected InteressatDto interessat;
	private String enviamentReferencia;
	private String enviamentDatatEstat;
	private Date enviamentDatatData;
	private String enviamentDatatOrigen;
	private Date enviamentCertificacioData;
	private String enviamentCertificacioOrigen;
	protected Boolean error;
	protected String errorDescripcio;
	private Date registreData;
	private Integer registreNumero;
	private String registreNumeroFormatat;
	private boolean finalitzat;
	
	//Utilitzats en formulari de ampliar plaç
	private Integer diesAmpliacio;
	private String motiu;
}