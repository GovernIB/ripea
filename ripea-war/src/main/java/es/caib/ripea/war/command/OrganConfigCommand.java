/**
 * 
 */
package es.caib.ripea.war.command;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.ripea.core.api.dto.config.OrganConfigDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;
import lombok.Data;

@Data
public class OrganConfigCommand  {
	
	private boolean crear;
	@NotNull
	private String key;
	private String value;
	private Long organGestorId;
    private boolean jbossProperty;
	
    public boolean isBooleanValue() {
        return value!=null && value.equals("true");
    }

    public void setBooleanValue(boolean booleanValue) {
        this.value = booleanValue ? "true" : "false";
    }
	
	
	public static OrganConfigCommand asCommand(OrganConfigDto dto) {
		OrganConfigCommand command = ConversioTipusHelper.convertir(
				dto,
				OrganConfigCommand.class);

		return command;
	}
	public static OrganConfigDto asDto(OrganConfigCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				OrganConfigDto.class);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}


}
