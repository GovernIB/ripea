/**
 * 
 */
package es.caib.ripea.service.intf.dto;

import java.io.Serializable;

/**
 * Informació d'un contingut emmagatzemada a l'arxiu.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class FirmaDto implements Serializable {

	private String tipus;
	private String perfil;
	private String fitxerNom;
	private byte[] contingut;
	private String tipusMime;
	private String csvRegulacio;
	private Boolean autofirma = false;

	public String getTipus() {
		return tipus;
	}
	public void setTipus(String tipus) {
		this.tipus = tipus;
	}
	public String getPerfil() {
		return perfil;
	}
	public void setPerfil(String perfil) {
		this.perfil = perfil;
	}
	public String getFitxerNom() {
		return fitxerNom;
	}
	public void setFitxerNom(String fitxerNom) {
		this.fitxerNom = fitxerNom;
	}
	public byte[] getContingut() {
		return contingut;
	}
	public void setContingut(byte[] contingut) {
		this.contingut = contingut;
	}
	public String getTipusMime() {
		return tipusMime;
	}
	public void setTipusMime(String tipusMime) {
		this.tipusMime = tipusMime;
	}
	public String getCsvRegulacio() {
		return csvRegulacio;
	}
	public void setCsvRegulacio(String csvRegulacio) {
		this.csvRegulacio = csvRegulacio;
	}
	public boolean getAutofirma() {
		return autofirma != null ? autofirma.booleanValue() : false;
	}
	public void setAutofirma(boolean autofirma) {
		this.autofirma = autofirma;
	}
	public String getContingutComString() {
		if (contingut != null) {
			return new String(contingut);
		}
		return null;
	}

	private static final long serialVersionUID = -2124829280908976623L;

}
