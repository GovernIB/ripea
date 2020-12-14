package es.caib.ripea.war.historic;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import es.caib.ripea.core.api.dto.historic.HistoricFiltreDto;
import es.caib.ripea.core.api.dto.historic.HistoricTipusEnumDto;
import lombok.Getter;

@Getter
@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class HistoricApiResponse {
	private Filtre filtre;
	private List<?> registres;
	
	public HistoricApiResponse(HistoricFiltreDto filtre, List<?> registres) {
		super();
		this.filtre = new Filtre(filtre);
		this.registres = registres;
	}


	@Getter
	private static class Filtre implements Serializable {

		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", timezone="Europe/Madrid")
		Date dataInici;
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", timezone="Europe/Madrid")
		Date dataFi;

		List<Long> organGestorsIds;
		List<Long> metaExpedientsIds;
		Boolean incorporarExpedientsComuns;			
		HistoricTipusEnumDto tipusAgrupament;
		
		public Filtre(HistoricFiltreDto filtre) {
			this.dataInici = filtre.getDataInici();
			this.dataFi = filtre.getDataFi();
			this.organGestorsIds = filtre.getOrganGestorsIds();
			this.metaExpedientsIds = filtre.getMetaExpedientsIds();
			this.incorporarExpedientsComuns = filtre.getIncorporarExpedientsComuns();
			this.tipusAgrupament = filtre.getTipusAgrupament();
		}
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -6984582899385701436L;
	}
}
