/**
 * 
 */
package es.caib.ripea.service.intf.dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringEscapeUtils;


@Getter @Setter
public class CrearReglaResponseDto  {

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
