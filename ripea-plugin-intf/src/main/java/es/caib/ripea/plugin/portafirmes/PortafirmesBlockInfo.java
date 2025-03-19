package es.caib.ripea.plugin.portafirmes;

import java.io.Serializable;
import java.util.List;

public class PortafirmesBlockInfo implements Serializable {

	private List<PortafirmesBlockSignerInfo> signers;
	
	
	public List<PortafirmesBlockSignerInfo> getSigners() {
		return signers;
	}

	public void setSigners(List<PortafirmesBlockSignerInfo> signers) {
		this.signers = signers;
	}
	
	private static final long serialVersionUID = -1665824823934702923L;
}
