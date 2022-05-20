package es.caib.ripea.core.api.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PortafirmesBlockInfoDto implements Serializable {

	private String signerNom;
	private String signerCodi;
	private String signerId;
	private boolean signed;
	private Date data;
	
	private static final long serialVersionUID = -1665824823934702923L;

}
