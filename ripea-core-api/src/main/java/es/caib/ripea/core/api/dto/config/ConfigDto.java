package es.caib.ripea.core.api.dto.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigDto {
    private String key;
    private String value;
    private String description;
    private boolean jbossProperty;

    private String typeCode;
    private List<String> validValues;
}
