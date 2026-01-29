package es.caib.ripea.plugin.caib.procediment;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Rolsac2UAResponse {
	private String numeroElementos;
	private String status;
	private String mensaje;
	private Integer tiempo;
	private List<Rolsac2UnitatAdministrativa> resultado;
}
