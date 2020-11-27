package es.caib.ripea.core.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import es.caib.ripea.core.api.dto.HistoricTipusEnumDto;
import es.caib.ripea.core.repository.ContingutLogRepository;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ipa_hist_expedient")
public class HistoricExpedientEntity extends HistoricEntity {

	@Column(name = "n_exped_amb_alertes")
	private Long numExpedientsAmbAlertes;
	@Column(name = "n_exped_errors_valid")
	private Long numExpedientsAmbErrorsValidacio;
	@Column(name = "n_docs_pendents_sign")
	private Long numDocsPendentsSignar;
	@Column(name = "n_docs_sign")
	private Long numDocsSignats;
	@Column(name = "n_docs_pendents_notif")
	private Long numDocsPendentsNotificar;
	@Column(name = "n_docs_notif")
	private Long numDocsNotificats;

	public HistoricExpedientEntity() {
		super();
	}

	public HistoricExpedientEntity(Date data, HistoricTipusEnumDto tipus) {
		super(data, tipus);
		this.numExpedientsAmbAlertes = 0L;
		this.numExpedientsAmbErrorsValidacio = 0L;
		this.numDocsPendentsSignar = 0L;
		this.numDocsSignats = 0L;
		this.numDocsPendentsNotificar = 0L;
		this.numDocsNotificats = 0L;
	}

	public void fillDailyHistoric(
			ContingutLogRepository contingutLogRepository,
			MetaExpedientEntity metaExpedient,
			Date date) {
		super.fillDailyHistoric(contingutLogRepository, metaExpedient, date);

	}
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 2212441099187320050L;

}
