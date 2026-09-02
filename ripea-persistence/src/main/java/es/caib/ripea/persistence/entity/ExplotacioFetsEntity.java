package es.caib.ripea.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.ExplotFetsAmbDimensioDto;
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
	
	@Column(name = "prc_actius_tot")		private Long procedimentsActiusTotal;
	@Column(name = "srv_actius_tot")		private Long serveisActiusTotal;
	
	@Column(name = "exp_creat")			private Long expedientsCreats;
	@Column(name = "exp_creat_tot")		private Long expedientsCreatsTotal;
	@Column(name = "exp_tancat")		private Long expedientsTancats;
	@Column(name = "exp_tancat_tot")	private Long expedientsTancatsTotal;
	
	@Column(name = "tas_pendent")		private Long tasquesPendents;
	@Column(name = "tas_pendent_tot")	private Long tasquesPendentsTotal;
	@Column(name = "tas_iniciada")		private Long tasquesIniciades;
	@Column(name = "tas_iniciada_tot")	private Long tasquesIniciadesTotal;
	@Column(name = "tas_finalitzada_ok")		private Long tasquesFinalitzadesDinsTermini;
	@Column(name = "tas_finalitzada_ko")		private Long tasquesFinalitzadesForaTermini;
	@Column(name = "tas_finalitzada_total_ok")		private Long tasquesFinalitzadesTotalDinsTermini;
	@Column(name = "tas_finalitzada_total_ko")		private Long tasquesFinalitzadesTotalForaTermini;
	@Column(name = "tas_cancelada")		private Long tasquesCancelades;
	@Column(name = "tas_cancelada_tot")	private Long tasquesCanceladesTotal;
	@Column(name = "tas_rebutjada")		private Long tasquesRebutjades;
	@Column(name = "tas_rebutjada_tot")	private Long tasquesRebutjadesTotal;
	@Column(name = "tas_agafada")		private Long tasquesAgafades;
	@Column(name = "tas_agafada_tot")	private Long tasquesAgafadesTotal;
	@Column(name = "tas_creada")		private Long tasquesCreades;
	@Column(name = "tas_creada_tot")	private Long tasquesCreadesTotal;
	@Column(name = "tas_notfin_fora_termini")		private Long tasquesNoFinalitzadesForaTermini;
	@Column(name = "tas_notfin_fora_termini_tot")	private Long tasquesNoFinalitzadesForaTerminiTotal;

	@Column(name = "ano_noves")		private Long anotacionsNoves;
	@Column(name = "ano_noves_tot")	private Long anotacionsNovesTotal;
	@Column(name = "ano_processada")	private Long anotacionsProcessades;
	@Column(name = "ano_processada_tot")private Long anotacionsProcessadesTotal;
	@Column(name = "ano_rebutjada")	private Long anotacionsRebutjades;
	@Column(name = "ano_rebutjada_tot")private Long anotacionsRebutjadesTotal;
	
	@Column(name = "pin_enviats_ok")		private Long pinbalEnviamentsOk;
	@Column(name = "pin_enviats_ko")		private Long pinbalEnviamentsError;
	@Column(name = "pin_enviats_tot_ok")	private Long pinbalEnviamentsTotalOk;
	@Column(name = "pin_enviats_tot_ko")	private Long pinbalEnviamentsTotalError;
	
	@Column(name = "not_enviada")			private Long notificacionsEnviades;
	@Column(name = "not_enviada_tot")		private Long notificacionsEnviadesTotal;
	@Column(name = "not_pendent")			private Long notificacionsPendents;
	@Column(name = "not_pendent_tot")		private Long notificacionsPendentsTotal;
	@Column(name = "not_registrada")		private Long notificacionsRegistrades;
	@Column(name = "not_registrada_tot")	private Long notificacionsRegistradesTotal;
	@Column(name = "not_finalitzada")		private Long notificacionsFinalitzades;
	@Column(name = "not_finalitzada_tot")	private Long notificacionsFinalitzadesTotal;
	@Column(name = "not_processada")		private Long notificacionsProcessades;
	@Column(name = "not_processada_tot")	private Long notificacionsProcessadesTotal;
	@Column(name = "not_env_err")			private Long notificacionsEnvError;
	@Column(name = "not_env_err_tot")		private Long notificacionsEnvErrorTotal;
	@Column(name = "not_fin_err")			private Long notificacionsFinError;
	@Column(name = "not_fin_err_tot")		private Long notificacionsFinErrorTotal;
	
	@Column(name = "fir_enviat")		private Long firmesEnviades;
	@Column(name = "fir_enviat_tot")	private Long firmesEnviadesTotal;
	@Column(name = "fir_iniciat")		private Long firmesIniciades;
	@Column(name = "fir_iniciat_tot")	private Long firmesIniciadesTotal;
	@Column(name = "fir_pausat")		private Long firmesPausades;
	@Column(name = "fir_pausat_tot")	private Long firmesPausadesTotal;
	@Column(name = "fir_firmat")		private Long firmesFirmades;
	@Column(name = "fir_firmat_tot")	private Long firmesFirmadesTotal;
	@Column(name = "fir_rebutjat")		private Long firmesRebutjades;
	@Column(name = "fir_rebutjat_tot")	private Long firmesRebutjadesTotal;
	@Column(name = "fir_parcial")		private Long firmesParcials;
	@Column(name = "fir_parcial_tot")	private Long firmesParcialsTotal;
	
	public void updateFromDto(ExplotFetsAmbDimensioDto fetsDto) {
		this.setAnotacionsNoves(fetsDto.getAnotacionsNoves()!=null?fetsDto.getAnotacionsNoves():0l);
		this.setAnotacionsNovesTotal(fetsDto.getAnotacionsNovesTotal()!=null?fetsDto.getAnotacionsNovesTotal():0l);
		this.setAnotacionsProcessades(fetsDto.getAnotacionsProcessades()!=null?fetsDto.getAnotacionsProcessades():0l);
		this.setAnotacionsProcessadesTotal(fetsDto.getAnotacionsProcessadesTotal()!=null?fetsDto.getAnotacionsProcessadesTotal():0l);
		this.setAnotacionsRebutjades(fetsDto.getAnotacionsRebutjades()!=null?fetsDto.getAnotacionsRebutjades():0l);
		this.setAnotacionsRebutjadesTotal(fetsDto.getAnotacionsRebutjadesTotal()!=null?fetsDto.getAnotacionsRebutjadesTotal():0l);
		this.setExpedientsCreats(fetsDto.getExpedientsCreats()!=null?fetsDto.getExpedientsCreats():0l);
		this.setExpedientsCreatsTotal(fetsDto.getExpedientsCreatsTotal()!=null?fetsDto.getExpedientsCreatsTotal():0l);
		this.setExpedientsTancats(fetsDto.getExpedientsTancats()!=null?fetsDto.getExpedientsTancats():0l);
		this.setExpedientsTancatsTotal(fetsDto.getExpedientsTancatsTotal()!=null?fetsDto.getExpedientsTancatsTotal():0l);
		this.setFirmesFirmades(fetsDto.getFirmesFirmades()!=null?fetsDto.getFirmesFirmades():0l);
		this.setFirmesFirmadesTotal(fetsDto.getFirmesFirmadesTotal()!=null?fetsDto.getFirmesFirmadesTotal():0l);
		this.setFirmesEnviades(fetsDto.getFirmesEnviades()!=null?fetsDto.getFirmesEnviades():0l);
		this.setFirmesEnviadesTotal(fetsDto.getFirmesEnviadesTotal()!=null?fetsDto.getFirmesEnviadesTotal():0l);
		this.setFirmesIniciades(fetsDto.getFirmesIniciades()!=null?fetsDto.getFirmesIniciades():0l);
		this.setFirmesIniciadesTotal(fetsDto.getFirmesIniciadesTotal()!=null?fetsDto.getFirmesIniciadesTotal():0l);
		this.setFirmesParcials(fetsDto.getFirmesParcials()!=null?fetsDto.getFirmesParcials():0l);
		this.setFirmesParcialsTotal(fetsDto.getFirmesParcialsTotal()!=null?fetsDto.getFirmesParcialsTotal():0l);
		this.setFirmesPausades(fetsDto.getFirmesPausades()!=null?fetsDto.getFirmesPausades():0l);
		this.setFirmesPausadesTotal(fetsDto.getFirmesPausadesTotal()!=null?fetsDto.getFirmesPausadesTotal():0l);
		this.setFirmesRebutjades(fetsDto.getFirmesRebutjades()!=null?fetsDto.getFirmesRebutjades():0l);
		this.setFirmesRebutjadesTotal(fetsDto.getFirmesRebutjadesTotal()!=null?fetsDto.getFirmesRebutjadesTotal():0l);
		this.setNotificacionsEnvError(fetsDto.getNotificacionsEnvError()!=null?fetsDto.getNotificacionsEnvError():0l);
		this.setNotificacionsEnvErrorTotal(fetsDto.getNotificacionsEnvErrorTotal()!=null?fetsDto.getNotificacionsEnvErrorTotal():0l);
		this.setNotificacionsEnviades(fetsDto.getNotificacionsEnviades()!=null?fetsDto.getNotificacionsEnviades():0l);
		this.setNotificacionsEnviadesTotal(fetsDto.getNotificacionsEnviadesTotal()!=null?fetsDto.getNotificacionsEnviadesTotal():0l);
		this.setNotificacionsFinalitzades(fetsDto.getNotificacionsFinalitzades()!=null?fetsDto.getNotificacionsFinalitzades():0l);
		this.setNotificacionsFinalitzadesTotal(fetsDto.getNotificacionsFinalitzadesTotal()!=null?fetsDto.getNotificacionsFinalitzadesTotal():0l);
		this.setNotificacionsFinError(fetsDto.getNotificacionsFinError()!=null?fetsDto.getNotificacionsFinError():0l);
		this.setNotificacionsFinErrorTotal(fetsDto.getNotificacionsFinErrorTotal()!=null?fetsDto.getNotificacionsFinErrorTotal():0l);
		this.setNotificacionsPendents(fetsDto.getNotificacionsPendents()!=null?fetsDto.getNotificacionsPendents():0l);
		this.setNotificacionsPendentsTotal(fetsDto.getNotificacionsPendentsTotal()!=null?fetsDto.getNotificacionsPendentsTotal():0l);
		this.setNotificacionsProcessades(fetsDto.getNotificacionsProcessades()!=null?fetsDto.getNotificacionsProcessades():0l);
		this.setNotificacionsProcessadesTotal(fetsDto.getNotificacionsProcessadesTotal()!=null?fetsDto.getNotificacionsProcessadesTotal():0l);
		this.setNotificacionsRegistrades(fetsDto.getNotificacionsRegistrades()!=null?fetsDto.getNotificacionsRegistrades():0l);
		this.setNotificacionsRegistradesTotal(fetsDto.getNotificacionsRegistradesTotal()!=null?fetsDto.getNotificacionsRegistradesTotal():0l);
		this.setPinbalEnviamentsOk(fetsDto.getPinbalEnviamentsOk()!=null?fetsDto.getPinbalEnviamentsOk():0l);
		this.setPinbalEnviamentsError(fetsDto.getPinbalEnviamentsError()!=null?fetsDto.getPinbalEnviamentsError():0l);
		this.setPinbalEnviamentsTotalOk(fetsDto.getPinbalEnviamentsTotalOk()!=null?fetsDto.getPinbalEnviamentsTotalOk():0l);
		this.setPinbalEnviamentsTotalError(fetsDto.getPinbalEnviamentsTotalError()!=null?fetsDto.getPinbalEnviamentsTotalError():0l);
		this.setProcedimentsActiusTotal(fetsDto.getProcedimentActiusTotal()!=null?fetsDto.getProcedimentActiusTotal():0l);
		this.setServeisActiusTotal(fetsDto.getServeisActiusTotal()!=null?fetsDto.getServeisActiusTotal():0l);
		this.setTasquesAgafades(fetsDto.getTasquesAgafades()!=null?fetsDto.getTasquesAgafades():0l);
		this.setTasquesAgafadesTotal(fetsDto.getTasquesAgafadesTotal()!=null?fetsDto.getTasquesAgafadesTotal():0l);
		this.setTasquesCreades(fetsDto.getTasquesCreades()!=null?fetsDto.getTasquesCreades():0l);
		this.setTasquesCreadesTotal(fetsDto.getTasquesCreadesTotal()!=null?fetsDto.getTasquesCreadesTotal():0l);
		this.setTasquesNoFinalitzadesForaTermini(fetsDto.getTasquesNoFinalitzadesForaTermini()!=null?fetsDto.getTasquesNoFinalitzadesForaTermini():0l);
		this.setTasquesNoFinalitzadesForaTerminiTotal(fetsDto.getTasquesNoFinalitzadesForaTerminiTotal()!=null?fetsDto.getTasquesNoFinalitzadesForaTerminiTotal():0l);
		this.setTasquesCancelades(fetsDto.getTasquesCancelades()!=null?fetsDto.getTasquesCancelades():0l);
		this.setTasquesCanceladesTotal(fetsDto.getTasquesCanceladesTotal()!=null?fetsDto.getTasquesCanceladesTotal():0l);
		this.setTasquesFinalitzadesDinsTermini(fetsDto.getTasquesFinalitzadesDinsTermini()!=null?fetsDto.getTasquesFinalitzadesDinsTermini():0l);
		this.setTasquesFinalitzadesForaTermini(fetsDto.getTasquesFinalitzadesForaTermini()!=null?fetsDto.getTasquesFinalitzadesForaTermini():0l);
		this.setTasquesFinalitzadesTotalDinsTermini(fetsDto.getTasquesFinalitzadesTotalDinsTermini()!=null?fetsDto.getTasquesFinalitzadesTotalDinsTermini():0l);
		this.setTasquesFinalitzadesTotalForaTermini(fetsDto.getTasquesFinalitzadesTotalForaTermini()!=null?fetsDto.getTasquesFinalitzadesTotalForaTermini():0l);
		this.setTasquesIniciades(fetsDto.getTasquesIniciades()!=null?fetsDto.getTasquesIniciades():0l);
		this.setTasquesIniciadesTotal(fetsDto.getTasquesIniciadesTotal()!=null?fetsDto.getTasquesIniciadesTotal():0l);
		this.setTasquesPendents(fetsDto.getTasquesPendents()!=null?fetsDto.getTasquesPendents():0l);
		this.setTasquesPendentsTotal(fetsDto.getTasquesPendentsTotal()!=null?fetsDto.getTasquesPendentsTotal():0l);
		this.setTasquesRebutjades(fetsDto.getTasquesRebutjades()!=null?fetsDto.getTasquesRebutjades():0l);
		this.setTasquesRebutjadesTotal(fetsDto.getTasquesRebutjadesTotal()!=null?fetsDto.getTasquesRebutjadesTotal():0l);
	}
}
