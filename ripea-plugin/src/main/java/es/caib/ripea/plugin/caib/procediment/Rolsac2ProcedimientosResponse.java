package es.caib.ripea.plugin.caib.procediment;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Rolsac2ProcedimientosResponse {
	private String numeroElementos;
	private Integer tiempo;
	private String mensaje;
	private String status;
	private String url;
	private List<Rolsac2Procediment> resultado;
}
