package es.caib.ripea.plugin.portafirmes;

import java.io.Serializable;

public class PortafirmesFluxResposta implements Serializable {

	private String fluxId;
	private boolean error;
	private PortafirmesFluxErrorTipus errorTipus;
	
	public String getFluxId() {
		return fluxId;
	}
	public void setFluxId(String fluxId) {
		this.fluxId = fluxId;
	}
	public boolean isError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}
	public PortafirmesFluxErrorTipus getErrorTipus() {
		return errorTipus;
	}
	public void setErrorTipus(PortafirmesFluxErrorTipus errorTipus) {
		this.errorTipus = errorTipus;
	}
	private static final long serialVersionUID = -6768802833333049841L;
}
