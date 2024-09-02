package es.caib.ripea.core.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Resum {
    private String resum;
    private String titol;
}
