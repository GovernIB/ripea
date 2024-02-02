package es.caib.ripea.core.api.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@ToString
@Getter
@Setter
public class IntegracioFiltreDto implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String entitatCodi;
	private Date dataInici;
	private Date dataFi;
	private IntegracioAccioTipusEnumDto tipus;
	private String descripcio;
	private IntegracioAccioEstatEnumDto estat;


    
}

