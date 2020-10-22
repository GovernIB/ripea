package es.caib.ripea.war.command;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import es.caib.ripea.core.api.dto.HistoricDadesMostrarEnum;
import es.caib.ripea.core.api.dto.HistoricFiltreDto;
import es.caib.ripea.core.api.dto.HistoricTipusEnumDto;
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

	private List<HistoricDadesMostrarEnum> dadesMostrar;

	private HistoricTipusEnumDto tipusAgrupament; // DIARI, MENSUAL, DIA CONCRET

	private boolean showingTables;
		
	public HistoricFiltreCommand() {
		DateTime dateStartToday = (new LocalDate()).toDateTimeAtStartOfDay();
		this.dataFi = dateStartToday.toDate();
		this.dataInici = dateStartToday.minusDays(30).toDate();
		this.organGestorsIds = new ArrayList<Long>();
		this.metaExpedientsIds = new ArrayList<Long>();
		this.dadesMostrar = new ArrayList<HistoricDadesMostrarEnum>();
		this.dadesMostrar.add(HistoricDadesMostrarEnum.ENTITAT);
		this.tipusAgrupament = HistoricTipusEnumDto.DIARI;
		this.showingTables = true;
	}

	public boolean showingDadesEntitat() {
		return dadesMostrar != null && dadesMostrar.contains(HistoricDadesMostrarEnum.ENTITAT);
	}

	public boolean showingDadesOrganGestor() {
		return dadesMostrar != null && dadesMostrar.contains(HistoricDadesMostrarEnum.ORGANGESTOR);
	}

	public boolean showingDadesUsuari() {
		return dadesMostrar != null && dadesMostrar.contains(HistoricDadesMostrarEnum.USUARI);
	}

	public boolean showingDadesInteressat() {
		return dadesMostrar != null && dadesMostrar.contains(HistoricDadesMostrarEnum.INTERESSAT);
	}

	public HistoricFiltreDto asDto() {
		return ConversioTipusHelper.convertir(this, HistoricFiltreDto.class);
	}
	
}
