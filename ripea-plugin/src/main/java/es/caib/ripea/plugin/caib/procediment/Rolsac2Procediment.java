package es.caib.ripea.plugin.caib.procediment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Rolsac2Procediment {
	//https://github.com/GovernIB/rolsac2/blob/rolsac-2.0/rolsac2-api-interna/src/main/java/es/caib/rolsac2/api/interna/v1/model/Procedimientos.java
	private long codigo;
	private Integer codigoSIA;
	private String nombreProcedimientoWorkFlow;
	private String objeto;
	private String destinatarios;
	private String observaciones;
	private Link linkUnidadAdministrativaResponsable;
	private Link linkUnidadAdministrativaCompetente;
	private Link linkUnidadAdministrativaInstructora;
	private int comun;
}