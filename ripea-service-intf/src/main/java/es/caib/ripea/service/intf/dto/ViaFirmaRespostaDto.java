package es.caib.ripea.service.intf.dto;

import java.io.Serializable;

public class ViaFirmaRespostaDto implements Serializable{

	private String messageCode;
	private ViaFirmaCallbackEstatEnumDto status;
	
	public String getMessageCode() {
		return messageCode;
	}
	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}
	public ViaFirmaCallbackEstatEnumDto getStatus() {
		return status;
	}
	public void setStatus(ViaFirmaCallbackEstatEnumDto status) {
		this.status = status;
	}
	
	private static final long serialVersionUID = -4236622266485528851L;

}
