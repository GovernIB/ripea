package es.caib.ripea.service.intf.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
public class PortafirmesCarrecDto implements Serializable {

	private String carrecId;
	private String carrecName;
	private String entitatId;
	private String usuariPersonaId;
	private String usuariPersonaNif;
	private String usuariPersonaEmail;
	private String usuariPersonaNom;
	private static final long serialVersionUID = 7006948072730800264L;
}
