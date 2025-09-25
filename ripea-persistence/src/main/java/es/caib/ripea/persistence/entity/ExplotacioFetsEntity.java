package es.caib.ripea.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import es.caib.ripea.service.intf.config.BaseConfig;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "explot_fet")
@Getter
@Setter
public class ExplotacioFetsEntity extends RipeaPersistable<Long> {

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name="dimensio_id")
	protected ExplotacioDimensioEntity dimensio;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name="temps_id")
	protected ExplotacioTempsEntity temps;
	
	@Column(name = "exp_obert")			private Long expedientsOberts;
	@Column(name = "exp_obert_tot")		private Long expedientsObertsTotal;
	@Column(name = "exp_tancat")		private Long expedientsTancats;
	@Column(name = "exp_tancat_tot")	private Long expedientsTancatsTotal;
	@Column(name = "tas_pendent")		private Long tasquesPendents;
	@Column(name = "tas_pendent_tot")	private Long tasquesPendentsTotal;
	@Column(name = "tas_finalitzada")	private Long tasquesFinalitzades;
	@Column(name = "tas_finalitzada_tot")	private Long tasquesFinalitzadesTotal;
	@Column(name = "ano_pendent")		private Long anotacionsPendents;
	@Column(name = "ano_pendent_tot")	private Long anotacionsPendentsTotal;
	@Column(name = "ano_processada")	private Long anotacionsProcessades;
	@Column(name = "ano_processada_tot")private Long anotacionsProcessadesTotal;
	@Column(name = "pin_enviats")		private Long pinbalEnviaments;
	@Column(name = "pin_enviats_tot")	private Long pinbalEnviamentsTotal;
	@Column(name = "not_enviada")		private Long notificacionsEnviades;
	@Column(name = "not_enviada_tot")	private Long notificacionsEnviadesTotal;
	@Column(name = "com_enviada")		private Long comunicacionsEnviades;
	@Column(name = "com_enviada_tot")	private Long comunicacionsEnviadesTotal;
	@Column(name = "fir_enviada")		private Long firmesEnviades;
	@Column(name = "fir_enviada_tot")	private Long firmesEnviadesTotal;
}
