package es.caib.ripea.core.api.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class HistoricFiltreDto {

	private Date dataInici;
	private Date dataFi;

	private List<Long> organGestorsIds;
	private List<Long> metaExpedientsIds;

	private Boolean incorporarExpedientsComuns;
	
	private HistoricDadesMostrarEnum dadesMostrar;
		
	private HistoricTipusEnumDto tipusAgrupament; // DIARI, MENSUAL, DIA CONCRET 

}
