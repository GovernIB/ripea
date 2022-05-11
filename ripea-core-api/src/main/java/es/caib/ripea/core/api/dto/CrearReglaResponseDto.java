/**
 * 
 */
package es.caib.ripea.core.api.dto;

import org.apache.commons.lang.StringEscapeUtils;

import lombok.Getter;
import lombok.Setter;


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
