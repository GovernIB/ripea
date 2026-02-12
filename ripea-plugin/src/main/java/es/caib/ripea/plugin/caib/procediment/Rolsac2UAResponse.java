package es.caib.ripea.plugin.caib.procediment;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Rolsac2UAResponse {
	private String totalCount;
	private String status;
	private String mensaje;
	private Integer tiempo;
	private List<Rolsac2UnitatAdministrativa> items;
}
