package es.caib.ripea.service.intf.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
* Informació retornada per la consulta de justificant d'un enviament.
*
* @author Limit Tecnologies <limit@limit.es>
*/
@Getter @Setter
public class RespostaJustificantEnviamentNotibDto {

	private boolean error;
	private Date errorData;
	private String errorDescripcio;
	private byte[] justificant;
	
	
}
