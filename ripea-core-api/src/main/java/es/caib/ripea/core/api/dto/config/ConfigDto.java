package es.caib.ripea.core.api.dto.config;



import java.util.ArrayList;
import java.util.List;

import es.caib.ripea.core.api.dto.EntitatDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
//@NoArgsConstructor
@AllArgsConstructor
public class

ConfigDto {
    private String key;
    private String value;
    private String description;
    private boolean jbossProperty;
    private List<EntitatConfig> entitatsConfig;
    private String entitatCodi;
    private String entitatValue;

    private String typeCode;
    private List<String> validValues;

    public ConfigDto() {
        entitatsConfig = new ArrayList<>();
    }

    public static final String prefix = "es.caib.ripea";

    public String addEntitatKey(EntitatDto entitat) {

        String [] split = key.split(prefix);
        if (entitat == null || entitat.getCodi() == null || entitat.getCodi() == "" || split == null || split.length == 0 || split.length != 2) {
            return null;
        }
        EntitatConfig config = new EntitatConfig();
        config.setCodi(entitat.getCodi());
        config.setConfigKey(prefix + "." + entitat.getCodi() + split[1]);
        entitatsConfig.add(config);
        return config.getConfigKey();
    }

    public String crearEntitatKey() {

        if (entitatCodi == null || entitatCodi == "" || key == null || key == "") {
            return null;
        }
        String [] split = key.split(prefix);
        return (prefix + "." + entitatCodi + split[1]);
    }
}
