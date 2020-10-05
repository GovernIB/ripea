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

	private HistoricDadesMostrarEnum dadesMostrar;

	private HistoricTipusEnumDto tipusAgrupament; // DIARI, MENSUAL, DIA CONCRET

	public HistoricFiltreCommand() {
		DateTime dateStartToday = (new LocalDate()).toDateTimeAtStartOfDay();
		this.dataFi = dateStartToday.toDate();
		this.dataInici = dateStartToday.minusDays(30).toDate();
		this.organGestorsIds = new ArrayList<Long>();
		this.metaExpedientsIds = new ArrayList<Long>();
		this.dadesMostrar = HistoricDadesMostrarEnum.ENTITAT;
		this.tipusAgrupament = HistoricTipusEnumDto.DIARI;
	}

	public HistoricFiltreDto asDto() {
		return ConversioTipusHelper.convertir(this, HistoricFiltreDto.class);
	}

}
