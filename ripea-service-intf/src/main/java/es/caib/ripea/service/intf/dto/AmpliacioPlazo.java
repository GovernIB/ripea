package es.caib.ripea.service.intf.dto;

public class AmpliacioPlazo {
	
	private String estado;
	private String identificador;
	private String fechaCaducidad;
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
	public String getFechaCaducidad() {
		return fechaCaducidad;
	}
	public void setFechaCaducidad(
			String fechaCaducidad) {
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