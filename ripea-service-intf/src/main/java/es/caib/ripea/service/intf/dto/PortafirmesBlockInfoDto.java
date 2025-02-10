package es.caib.ripea.service.intf.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter @Setter
public class PortafirmesBlockInfoDto implements Serializable {

	private String signerNom;
	private String signerCodi;
	private String signerId;
	private boolean signed;
	private Date data;
	
	private static final long serialVersionUID = -1665824823934702923L;

}
