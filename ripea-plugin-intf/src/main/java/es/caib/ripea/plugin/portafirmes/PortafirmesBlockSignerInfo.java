package es.caib.ripea.plugin.portafirmes;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PortafirmesBlockSignerInfo implements Serializable {

	private String signerNom;
	private String signerCodi;
	private String signerId;
	private boolean signed;

	private static final long serialVersionUID = -1665824823934702923L;

}
