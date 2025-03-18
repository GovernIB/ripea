package es.caib.ripea.plugin.caib.procediment;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class ProcedimientosResponse {
	private String numeroElementos;
	private String status;
	private List<Procediment> resultado;
	private String mensaje;

}