package es.caib.ripea.plugin.caib.procediment;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class ProcedimientosResponse {
	private String numeroElementos;
	private String status;
	private List<Procediment> resultado;
	public String getNumeroElementos() {
		return numeroElementos;
	}
	public void setNumeroElementos(String numeroElementos) {
		this.numeroElementos = numeroElementos;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public List<Procediment> getResultado() {
		return resultado;
	}
	public void setResultado(List<Procediment> resultado) {
		this.resultado = resultado;
	}
}