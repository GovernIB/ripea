package es.caib.ripea.core.api.dto;

import java.io.Serializable;
import java.util.List;

public class PortafirmesBlockDto implements Serializable {

	private Long id;
	private List<PortafirmesBlockInfoDto> signers;
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<PortafirmesBlockInfoDto> getSigners() {
		return signers;
	}

	public void setSigners(List<PortafirmesBlockInfoDto> signers) {
		this.signers = signers;
	}
	
	private static final long serialVersionUID = -1665824823934702923L;
}
