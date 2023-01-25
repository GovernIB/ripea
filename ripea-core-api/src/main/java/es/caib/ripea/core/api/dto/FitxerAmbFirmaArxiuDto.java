package es.caib.ripea.core.api.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class FitxerAmbFirmaArxiuDto implements Serializable{

	private FitxerDto fitxer;
	private ArxiuFirmaDto arxiuFirma;

	
	private static final long serialVersionUID = 1L;
}
