package es.caib.ripea.core.api.dto.config;

import lombok.Data;

import java.util.List;

@Data
public class ConfigGroupDto {
    private String key;
    private String description;
    private List<ConfigDto> configs;
    private List<ConfigGroupDto> innerConfigs;
}
