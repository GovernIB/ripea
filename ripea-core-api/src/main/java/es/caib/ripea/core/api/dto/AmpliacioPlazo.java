package es.caib.ripea.core.api.dto;

import javax.xml.datatype.XMLGregorianCalendar;

public class AmpliacioPlazo {
	
	private String estado;
	private String identificador;
	private XMLGregorianCalendar fechaCaducidad;
	private String mensajeError;
	private String codigo;
  
	public String getEstado() {
		return estado;
	}
	public void setEstado(
			String estado) {
		this.estado = estado;
	}
	public String getIdentificador() {
		return identificador;
	}
	public void setIdentificador(
			String identificador) {
		this.identificador = identificador;
	}
	public XMLGregorianCalendar getFechaCaducidad() {
		return fechaCaducidad;
	}
	public void setFechaCaducidad(
			XMLGregorianCalendar fechaCaducidad) {
		this.fechaCaducidad = fechaCaducidad;
	}
	public String getMensajeError() {
		return mensajeError;
	}
	public void setMensajeError(
			String mensajeError) {
		this.mensajeError = mensajeError;
	}
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(
			String codigo) {
		this.codigo = codigo;
	}
}