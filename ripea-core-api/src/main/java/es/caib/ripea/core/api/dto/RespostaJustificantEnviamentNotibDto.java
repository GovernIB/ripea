package es.caib.ripea.core.api.dto;


import java.util.Date;

import lombok.Getter;
import lombok.Setter;

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
