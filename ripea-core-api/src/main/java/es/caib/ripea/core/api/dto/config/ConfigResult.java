package es.caib.ripea.core.api.dto.config;



import lombok.Data;

@Data
public class ConfigResult {

    private String codiEntitat;
    private String configValue;
    
	public ConfigResult(
			String codiEntitat,
			String configValue) {
		this.codiEntitat = codiEntitat;
		this.configValue = configValue;
	}
    
    
}
