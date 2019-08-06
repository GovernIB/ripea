package es.caib.ripea.plugin.viafirma;

import java.io.Serializable;

public class ViaFirmaError implements Serializable {

	private String codiMissatge;
	private String tipus;
	private String descripcioMissatge;
	private String trace;
	
	public String getCodiMissatge() {
		return codiMissatge;
	}
	public void setCodiMissatge(String codiMissatge) {
		this.codiMissatge = codiMissatge;
	}
	public String getTipus() {
		return tipus;
	}
	public void setTipus(String tipus) {
		this.tipus = tipus;
	}
	public String getDescripcioMissatge() {
		return descripcioMissatge;
	}
	public void setDescripcioMissatge(String descripcioMissatge) {
		this.descripcioMissatge = descripcioMissatge;
	}
	public String getTrace() {
		return trace;
	}
	public void setTrace(String trace) {
		this.trace = trace;
	}
	
	private static final long serialVersionUID = -6470506261247871495L;

}
