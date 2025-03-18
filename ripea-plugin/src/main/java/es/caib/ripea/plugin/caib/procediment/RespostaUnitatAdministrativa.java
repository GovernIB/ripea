package es.caib.ripea.plugin.caib.procediment;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class RespostaUnitatAdministrativa {

	@JsonProperty("numeroElementos")
    private Integer numeroElementos;
    @JsonProperty("status")
    private String status;
    @JsonProperty("resultado")
    private List<UnitatAdministrativa> resultado = new ArrayList<UnitatAdministrativa>();
	    
}
