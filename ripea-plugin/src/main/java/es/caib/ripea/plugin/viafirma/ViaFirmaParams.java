package es.caib.ripea.plugin.viafirma;

import java.io.Serializable;

/**
 * Paràmetres entrada per el sistema viaFirma
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ViaFirmaParams implements Serializable {

	private boolean lecturaObligatoria;
	private String titol;
	private String descripcio;
	private String contingut;
	private String codiUsuari;
	private String contrasenya;
	private ViaFirmaDispositiu viaFirmaDispositiu;
	
	public boolean isLecturaObligatoria() {
		return lecturaObligatoria;
	}
	public void setLecturaObligatoria(boolean lecturaObligatoria) {
		this.lecturaObligatoria = lecturaObligatoria;
	}
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
	public String getContingut() {
		return contingut;
	}
	public void setContingut(String contingut) {
		this.contingut = contingut;
	}
	public String getCodiUsuari() {
		return codiUsuari;
	}
	public void setCodiUsuari(String codiUsuari) {
		this.codiUsuari = codiUsuari;
	}
	public String getContrasenya() {
		return contrasenya;
	}
	public void setContrasenya(String contrasenya) {
		this.contrasenya = contrasenya;
	}
	public ViaFirmaDispositiu getViaFirmaDispositiu() {
		return viaFirmaDispositiu;
	}
	public void setViaFirmaDispositiu(ViaFirmaDispositiu viaFirmaDispositiu) {
		this.viaFirmaDispositiu = viaFirmaDispositiu;
	}

	private static final long serialVersionUID = 7518162775992143312L;
}
