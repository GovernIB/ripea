package es.caib.ripea.service.intf.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@Getter @Setter
public class FitxerAmbFirmaArxiuDto implements Serializable{

	private FitxerDto fitxer;
	private ArxiuFirmaDto arxiuFirma;

	
	private static final long serialVersionUID = 1L;
}
