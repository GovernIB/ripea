package es.caib.ripea.service.intf.dto;

import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import org.apache.commons.lang3.StringEscapeUtils;

@Getter @Setter
public class CrearReglaResponseDto implements Serializable  {

	private static final long serialVersionUID = -2009029816995251162L;
	
	private StatusEnumDto status;
    private String msg;
    
	public CrearReglaResponseDto(
			StatusEnumDto status,
			String msg) {
		this.status = status;
		this.msg = msg;
	}
	
	public String getMsgEscapeXML(){
		return StringEscapeUtils.escapeXml(msg);
	}
}