package es.caib.ripea.core.api.dto;

import java.util.Date;

public class ImportacioDto {

	private String numeroRegistre;
	private TipusRegistreEnumDto tipusRegistre;
	private Date dataPresentacioFormatted;
	private TipusDestiEnumDto destiTipus;
	private String carpetaNom;
	
	public String getNumeroRegistre() {
		return numeroRegistre;
	}
	public void setNumeroRegistre(String numeroRegistre) {
		this.numeroRegistre = numeroRegistre;
	}
	public TipusRegistreEnumDto getTipusRegistre() {
		return tipusRegistre;
	}
	public void setTipusRegistre(TipusRegistreEnumDto tipusRegistre) {
		this.tipusRegistre = tipusRegistre;
	}
	public Date getDataPresentacioFormatted() {
		return dataPresentacioFormatted;
	}
	public void setDataPresentacioFormatted(Date dataPresentacioFormatted) {
		this.dataPresentacioFormatted = dataPresentacioFormatted;
	}
	public TipusDestiEnumDto getDestiTipus() {
		return destiTipus;
	}
	public void setDestiTipus(TipusDestiEnumDto destiTipus) {
		this.destiTipus = destiTipus;
	}
	public String getCarpetaNom() {
		return carpetaNom;
	}
	public void setCarpetaNom(String carpetaNom) {
		this.carpetaNom = carpetaNom;
	}
}
