package es.caib.ripea.plugin.viafirma;

import java.io.Serializable;

/**
 * Resposta del sistema de firma viaFirma
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ViaFirmaResponse implements Serializable {

	private String codiMissatge;
	private ViaFirmaError viaFirmaError;
	
	public String getCodiMissatge() {
		return codiMissatge;
	}
	public void setCodiMissatge(String codiMissatge) {
		this.codiMissatge = codiMissatge;
	}
	public ViaFirmaError getViaFirmaError() {
		return viaFirmaError;
	}
	public void setViaFirmaError(ViaFirmaError viaFirmaError) {
		this.viaFirmaError = viaFirmaError;
	}

	private static final long serialVersionUID = -3912014760151984611L;
}
