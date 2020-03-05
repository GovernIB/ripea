package es.caib.ripea.core.api.dto;

public class ImportacioDto {

	private String numeroRegistre;
	private TipusRegistreEnumDto tipusRegistre;
	
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
	
}
