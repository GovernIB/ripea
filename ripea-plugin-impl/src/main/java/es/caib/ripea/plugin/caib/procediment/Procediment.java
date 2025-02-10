package es.caib.ripea.plugin.caib.procediment;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Procediment {
	private String codigo;
	private String codigoSIA;
	
	private String nombre;
	private Boolean comun;
	private String resumen;
	
	@JsonProperty("link_unidadAdministrativa")
	private Link unidadAdministrativa;

	
	
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getCodigoSIA() {
		return codigoSIA;
	}
	public void setCodigoSIA(String codigoSIA) {
		this.codigoSIA = codigoSIA;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public Boolean getComun() {
		return comun;
	}
	public void setComun(Boolean comun) {
		this.comun = comun;
	}
	public String getResumen() {
		return resumen;
	}
	public void setResumen(String resumen) {
		this.resumen = resumen;
	}
	public Link getUnidadAdministrativa() {
		return unidadAdministrativa;
	}
	public void setUnidadAdministrativa(Link unidadAdministrativa) {
		this.unidadAdministrativa = unidadAdministrativa;
	}
	
	
}
