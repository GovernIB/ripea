package es.caib.ripea.plugin.caib.summarize;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BertResponse {
    private String message;
    private String summary;
}
