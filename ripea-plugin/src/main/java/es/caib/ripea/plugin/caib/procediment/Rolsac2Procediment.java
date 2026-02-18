package es.caib.ripea.plugin.caib.procediment;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Rolsac2Procediment {
	
	//https://github.com/GovernIB/rolsac2/blob/rolsac-2.0/rolsac2-api-interna/src/main/java/es/caib/rolsac2/api/interna/v1/model/Procedimientos.java
	
	/*private long codigo;
	private Integer codigoSIA;
	private String nombreProcedimientoWorkFlow;
	private String observaciones;
	private Link linkUnidadAdministrativaResponsable;
	private Link linkUnidadAdministrativaCompetente;
	private Link linkUnidadAdministrativaInstructora;
	private Boolean comun;*/
	
	private Rolsac2Inicio iniciacion;
	private String tipo;
	private String estado;
	private String lopdDestinatario;
	private String destinatarios;
	private String lopdCabecera;
	private Boolean activoLOPD;
	private Integer uaInstructor;
	private Boolean publicado;
	private Date fechaCaducidad;
	private long codigo;
	private String workflow;
	private Boolean interno;
	private Link linkUnidadAdministrativaCompetente;
	private String terminoResolucion;
	private Integer codigoWF;
	private Rolsac2TipoProcedimiento tipoProcedimiento;
	private Boolean estadoSIA;
	private String habilitadoFuncionario;
	private String lopdDerechos;
	private Boolean esPdu;
	private String observaciones;
	private Date fechaActualizacion;
	private Boolean habilitadoApoderado;
	private Link linkUnidadAdministrativaResponsable;
	private Date fechaPublicacion;
	private String lopdResponsable;
	private Integer uaCompetente;
	private Link linkLopdInfoAdicional;
	private String lopdFinalidad;
	private String objeto;
	private Rolsac2Silencio silencio;
	private Boolean tramitElectronica;
	private String requisitos;
	private String incidenciasEmail;
	private String nombreProcedimientoWorkFlow;
	private String responsableEmail;
	private String responsableTelefono;
	private String responsable;
	private Boolean hateoasEnabled;
	private Integer codigoSIA;
	private Integer tipoVia;
	private Integer uaResponsable;
	private Boolean tramitTelefonica;
	private Date fechaSIA;
	private Rolsac2Inicio lopdLegitimacion;
	private Boolean tramitPresencial;
	private Link linkUnidadAdministrativaInstructora;
	private Boolean comun;
	private Boolean tieneTasa;
}
