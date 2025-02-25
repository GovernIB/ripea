package es.caib.ripea.service.intf.model;

import java.util.Date;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.ArxiuEstatEnumDto;
import es.caib.ripea.service.intf.dto.DocumentEstatEnumDto;
import es.caib.ripea.service.intf.dto.DocumentFirmaTipusEnumDto;
import es.caib.ripea.service.intf.dto.DocumentNtiEstadoElaboracionEnumDto;
import es.caib.ripea.service.intf.dto.DocumentNtiTipoFirmaEnumDto;
import es.caib.ripea.service.intf.dto.DocumentTipusEnumDto;
import es.caib.ripea.service.intf.dto.NtiOrigenEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@ResourceConfig(quickFilterFields = { "fitxerNom" }, descriptionField = "fitxerNom")
public class DocumentResource extends NodeResource {
	
	private DocumentTipusEnumDto documentTipus;
	private DocumentEstatEnumDto estat;
	private String ubicacio;
	private Date data;
	private Date dataCaptura;
	private Date custodiaData;
	private String custodiaId;
	private String custodiaCsv;
	private String fitxerNom;
	private String fitxerContentType;
	private Long fitxerTamany;
	private byte[] fitxerContingut;
	private String versioDarrera;
	private int versioCount;
	private String ntiVersion;
	private String ntiIdentificador;
	private String ntiOrgano;
	private NtiOrigenEnumDto ntiOrigen;
	private DocumentNtiEstadoElaboracionEnumDto ntiEstadoElaboracion;
	private String ntiTipoDocumental;
	private String ntiIdDocumentoOrigen;
	private DocumentNtiTipoFirmaEnumDto ntiTipoFirma;
	private String ntiCsv;
	private String ntiCsvRegulacion;
	protected String descripcio;
	
	//Tipus de document firmat:
		//Document firmat putjat manualment
		//Document firmat des dels navegador
		//Document firmat que es rep des del portafirmes callback
		//Document que vene d'una anotació de registre
		//Document generat de les resposta de PINBAL
	
	// document signed in portafirmes that arrived in callback and was not saved in arxiu 
	private String gesDocFirmatId;
	private String nomFitxerFirmat;
	//document uploaded manually in ripea that was not saved in arxiu
	// document sense firma o amb firma adjunta
	private String gesDocAdjuntId;
	// firma separada
	private String gesDocAdjuntFirmaId;
	//ID del contingut original guardat al sistema de fitxers
	private String gesDocOriginalId;
	// firma separada of document saved as esborrany in arxiu
	private String arxiuUuidFirma;
	private String pinbalIdpeticion;
	private boolean validacioFirmaCorrecte;
	private String validacioFirmaErrorMsg;
	private ArxiuEstatEnumDto annexArxiuEstat;
	private ArxiuEstatEnumDto arxiuEstat;
	private DocumentFirmaTipusEnumDto documentFirmaTipus;
	private ResourceReference<ExpedientEstatResource, Long> expedientEstatAdditional;
}
