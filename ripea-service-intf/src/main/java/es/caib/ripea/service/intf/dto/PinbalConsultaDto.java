/**
 * 
 */
package es.caib.ripea.service.intf.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Informació d'una consulta a PINBAL.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class PinbalConsultaDto {

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
