package es.caib.ripea.core.api.dto;

import java.util.Date;

public class ImportacioDto {

	private String numeroRegistre;
	private TipusRegistreEnumDto tipusRegistre;
	private Date dataPresentacioFormatted;
	
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
}
