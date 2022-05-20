package es.caib.ripea.core.api.dto.config;



import lombok.Data;

@Data
public class ConfigResult {

    private String codiEntitatUtilitzat;
    private String configValue;
    
	public ConfigResult(
			String codiEntitatUtilitzat,
			String configValue) {
		this.codiEntitatUtilitzat = codiEntitatUtilitzat;
		this.configValue = configValue;
	}
    
    
}
