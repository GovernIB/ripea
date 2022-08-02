package es.caib.ripea.war.helper;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Alert {
    private String text;
    private String trace;
}
