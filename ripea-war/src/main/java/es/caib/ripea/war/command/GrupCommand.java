package es.caib.ripea.war.command;

import org.hibernate.validator.constraints.NotEmpty;
import javax.validation.constraints.Size;
import es.caib.ripea.war.helper.ConversioTipusHelper;
import es.caib.ripea.core.api.dto.GrupDto;

public class GrupCommand {

    private Long id;
    @NotEmpty @Size(max = 50)
    private String codi;
    @NotEmpty @Size(max = 512)
    private String descripcio;
 
    private Long entitatId;
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getCodi() {
        return codi;
    }
    public void setCodi(String codi) {
        this.codi = codi != null ? codi.trim() : null;
    }
    public String getDescripcio() {
        return descripcio;
    }
    public void setDescripcio(String descripcio) {
        this.descripcio = descripcio != null ? descripcio.trim() : null;
    }
    public static GrupCommand asCommand(GrupDto dto) {
        return ConversioTipusHelper.convertir(dto, GrupCommand.class);
    }
    public static GrupDto asDto(GrupCommand command) {
        return ConversioTipusHelper.convertir(command, GrupDto.class);
    }
	public Long getEntitatId() {
		return entitatId;
	}
	public void setEntitatId(Long entitatId) {
		this.entitatId = entitatId;
	}
}
