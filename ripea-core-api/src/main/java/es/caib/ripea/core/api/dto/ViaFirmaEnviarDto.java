/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.io.Serializable;

/**
 * Informació d'una dada.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ViaFirmaEnviarDto implements Serializable {

	private String titol;
	private String descripcio;
	private String codiUsuariViaFirma;
	private String contrasenyaUsuariViaFirma;
	private ViaFirmaDispositiuDto viaFirmaDispositiu;
	
	public String getTitol() {
		return titol;
	}
	public void setTitol(String titol) {
		this.titol = titol;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	public String getCodiUsuariViaFirma() {
		return codiUsuariViaFirma;
	}
	public void setCodiUsuariViaFirma(String codiUsuariViaFirma) {
		this.codiUsuariViaFirma = codiUsuariViaFirma;
	}
	public String getContrasenyaUsuariViaFirma() {
		return contrasenyaUsuariViaFirma;
	}
	public void setContrasenyaUsuariViaFirma(String contrasenyaUsuariViaFirma) {
		this.contrasenyaUsuariViaFirma = contrasenyaUsuariViaFirma;
	}
	public ViaFirmaDispositiuDto getViaFirmaDispositiu() {
		return viaFirmaDispositiu;
	}
	public void setViaFirmaDispositiu(ViaFirmaDispositiuDto viaFirmaDispositiu) {
		this.viaFirmaDispositiu = viaFirmaDispositiu;
	}


	private static final long serialVersionUID = 6451235953471494106L;
}
