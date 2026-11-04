/**
 * 
 */
package es.caib.ripea.service.intf.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

/**
 * Informació d'una consulta a PINBAL.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class PinbalConsultaDto {

	/**
	 * Serveis que tenen camps propis en aquest DTO i en el formulari de consulta de la interfície JSP, que els
	 * envia amb els clients antics de PINBAL (un client per servei). La resta de serveis només es poden consultar
	 * des de la interfície REACT, que construeix el formulari amb les dades específiques del servei a PINBAL.
	 */
	public static final Set<String> SERVEIS_FORMULARI_JSP = Set.of(
			"SVDDGPCIWS02",
			"SVDDGPVIWS02",
			"SVDCCAACPASWS01",
			"SVDSCDDWS01",
			"SCDCPAJU",
			"SVDSCTFNWS01",
			"SVDCCAACPCWS01",
			"Q2827003ATGSS001",
			"SVDDELSEXWS01",
			"SCDHPAJU",
			"NIVRENTI",
			"ECOT103",
			"SVDDGPRESIDENCIALEGALDOCWS01",
			"SVDRRCCNACIMIENTOWS01",
			"SVDRRCCMATRIMONIOWS01",
			"SVDRRCCDEFUNCIONWS01",
			"SVDBECAWS01");

	private String peticionId;
	private Long interessatId;
	private String finalitat;
	private PinbalConsentimentEnumDto consentiment;
	private String comunitatAutonomaCodi;
	private String provinciaCodi;
	private String municipiCodi;
	private String dataConsulta;
	private String dataNaixement;
	private SiNoEnumDto consentimentTipusDiscapacitat;
	private String numeroTitol;
	
	
	private String codiNacionalitat;
	private String paisNaixament;
	private String provinciaNaixament;
	private String poblacioNaixament;
	private String codiPoblacioNaixament;
	private SexeEnumDto sexe;
	private String nomPare;
	private String nomMare;
	private String telefon;
	private String email;
	
	private Integer nombreAnysHistoric;
	
	private Integer exercici;
	
	private TipusPassaportEnumDto tipusPassaport;
	private Date dataCaducidad;
	private Date dataExpedicion;
	private String numeroSoporte;
	
	private String registreCivil;
	private String tom;
	private String pagina;
	private Date dataRegistre;
	private boolean ausenciaSegundoApellido;
	
	private Integer curs;
	
	private String municipiRegistreSVDRRCCDEFUNCIONWS01;
	private String municipiNaixamentSVDRRCCDEFUNCIONWS01;
	private String municipiNaixamentSVDRRCCMATRIMONIOWS01;
	private String municipiRegistreSVDRRCCMATRIMONIOWS01;
	private String municipiRegistreSVDRRCCNACIMIENTOWS01;
	private String municipiNaixamentSVDRRCCNACIMIENTOWS01;
	private String municipiNaixamentSVDDELSEXWS01;
	
}
