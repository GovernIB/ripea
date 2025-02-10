package es.caib.ripea.service.intf.dto;

import java.io.Serializable;

public class ViaFirmaDispositiuDto implements Serializable{

	private String codiAplicacio;
	private String codi;
	private String descripcio;
	private String local;
	private String estat;
	private String token;
	private String identificador;
	private String tipus;
	private String emailUsuari;
	private String codiUsuari;
	private String identificadorNacional;
	
	public String getCodiAplicacio() {
		return codiAplicacio;
	}
	public void setCodiAplicacio(String codiAplicacio) {
		this.codiAplicacio = codiAplicacio;
	}
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	public String getLocal() {
		return local;
	}
	public void setLocal(String local) {
		this.local = local;
	}
	public String getEstat() {
		return estat;
	}
	public void setEstat(String estat) {
		this.estat = estat;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getIdentificador() {
		return identificador;
	}
	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}
	public String getTipus() {
		return tipus;
	}
	public void setTipus(String tipus) {
		this.tipus = tipus;
	}
	public String getEmailUsuari() {
		return emailUsuari;
	}
	public void setEmailUsuari(String emailUsuari) {
		this.emailUsuari = emailUsuari;
	}
	public String getCodiUsuari() {
		return codiUsuari;
	}
	public void setCodiUsuari(String codiUsuari) {
		this.codiUsuari = codiUsuari;
	}
	public String getIdentificadorNacional() {
		return identificadorNacional;
	}
	public void setIdentificadorNacional(String identificadorNacional) {
		this.identificadorNacional = identificadorNacional;
	}

	private static final long serialVersionUID = -8167813645558089235L;

}
