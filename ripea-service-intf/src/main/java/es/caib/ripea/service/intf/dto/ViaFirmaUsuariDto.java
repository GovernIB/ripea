package es.caib.ripea.service.intf.dto;

import java.io.Serializable;

public class ViaFirmaUsuariDto implements Serializable {

	private String codi;
	private String descripcio;
	
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
	
	private static final long serialVersionUID = -2597618117571032517L;
	
}
