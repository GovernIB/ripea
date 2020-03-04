package es.caib.ripea.core.api.dto;

import java.io.Serializable;

public class PortafirmesFluxRespostaDto implements Serializable {

	private String fluxId;
	private boolean error;
	private PortafirmesFluxErrorTipusDto errorTipus;
	
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
	public PortafirmesFluxErrorTipusDto getErrorTipus() {
		return errorTipus;
	}
	public void setErrorTipus(PortafirmesFluxErrorTipusDto errorTipus) {
		this.errorTipus = errorTipus;
	}

	private static final long serialVersionUID = -6768802833333049841L;
}
