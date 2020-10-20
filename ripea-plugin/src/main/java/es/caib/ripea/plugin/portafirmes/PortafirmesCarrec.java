package es.caib.ripea.plugin.portafirmes;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PortafirmesCarrec implements Serializable {

	private String carrecId;
	private String carrecName;
	private String entitatId;
	private String usuariPersonaId;
	private String usuariPersonaNif;
	private String usuariPersonaEmail;
	private String usuariPersonaNom;
	private static final long serialVersionUID = -5465570380256495538L;
}
