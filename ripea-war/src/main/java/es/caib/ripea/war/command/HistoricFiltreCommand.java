package es.caib.ripea.war.command;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import es.caib.ripea.core.api.dto.historic.HistoricDadesMostrarEnum;
import es.caib.ripea.core.api.dto.historic.HistoricFiltreDto;
import es.caib.ripea.core.api.dto.historic.HistoricTipusEnumDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HistoricFiltreCommand {

	private Date dataInici;
	private Date dataFi;

	private List<Long> organGestorsIds;
	private List<Long> metaExpedientsIds;
	
	private Boolean incorporarExpedientsComuns;
	
	@NotNull
	private HistoricDadesMostrarEnum dadesMostrar;
	
	private HistoricTipusEnumDto tipusAgrupament; // DIARI, MENSUAL, DIA CONCRET

	private boolean showingTables;
		
	public HistoricFiltreCommand() {
		DateTime dateStartToday = (new LocalDate()).toDateTimeAtStartOfDay();
		this.dataFi = dateStartToday.minusDays(1).toDate();
		this.dataInici = dateStartToday.minusDays(30).toDate();
		this.organGestorsIds = new ArrayList<Long>();
		this.metaExpedientsIds = new ArrayList<Long>();
		this.dadesMostrar = HistoricDadesMostrarEnum.ENTITAT;
		this.tipusAgrupament = HistoricTipusEnumDto.DIARI;
		this.showingTables = true;
		this.incorporarExpedientsComuns = false;
	}

	public void updateConditional(
			Date dataInici, 
			Date dataFi, 
			List<Long> organGestorsIds, 
			List<Long> metaExpedientsIds,
			Boolean incorporarExpedientsComuns,
			HistoricTipusEnumDto tipusAgrupament) {
		if (dataInici != null) {
			this.dataInici = dataInici;
		}
		
		if (dataFi != null) {
			this.dataFi = dataFi;
		}
		
		if (organGestorsIds != null) {
			this.organGestorsIds = organGestorsIds;
		}
		
		if (metaExpedientsIds != null) {
			this.metaExpedientsIds = metaExpedientsIds;
		}
		
		if (incorporarExpedientsComuns != null) {
			this.incorporarExpedientsComuns = incorporarExpedientsComuns;
		}

		if (tipusAgrupament != null) {
			this.tipusAgrupament =tipusAgrupament;
		}
	}
	
	public boolean showingDadesEntitat() {
		return dadesMostrar != null && dadesMostrar == HistoricDadesMostrarEnum.ENTITAT;
	}

	public boolean showingDadesOrganGestor() {
		return dadesMostrar != null && dadesMostrar == HistoricDadesMostrarEnum.ORGANGESTOR;
	}

	public boolean showingDadesUsuari() {
		return dadesMostrar != null && dadesMostrar == HistoricDadesMostrarEnum.USUARI;
	}

	public boolean showingDadesInteressat() {
		return dadesMostrar != null && dadesMostrar == HistoricDadesMostrarEnum.INTERESSAT;
	}

	public HistoricFiltreDto asDto() {
		return ConversioTipusHelper.convertir(this, HistoricFiltreDto.class);
	}
	
}
