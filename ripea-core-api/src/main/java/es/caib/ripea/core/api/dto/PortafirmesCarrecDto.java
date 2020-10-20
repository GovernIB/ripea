package es.caib.ripea.core.api.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

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
