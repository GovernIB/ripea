package es.caib.ripea.plugin.caib.procediment;

import lombok.Data;

@Data
public class FiltroProcedimientos {
	private String codigoUADir3;
	private Integer codigoSia;
	private String estadoSia = "A";
	private Integer buscarEnDescendientesUA = 1;
}