package es.caib.ripea.service.intf.model;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
	
	@NotNull
	private DocumentTipusEnumDto documentTipus;
	@NotNull
	private DocumentEstatEnumDto estat;
	@Size(max = 255)
	private String ubicacio;
	@NotNull
	private Date data;
	@NotNull
	private Date dataCaptura;
	private Date custodiaData;
	@Size(max = 256)
	private String custodiaId;
	@Size(max = 256)
	private String custodiaCsv;
	@Size(max = 256)
	private String fitxerNom;
	@Size(max = 256)
	private String fitxerContentType;
	private Long fitxerTamany;
//	private byte[] fitxerContingut;
	@Size(max = 32)
	private String versioDarrera;
	@NotNull
	private int versioCount;
	@NotNull
	@Size(max = 5)
	private String ntiVersion;
	@NotNull
	@Size(max = 48)
	private String ntiIdentificador;
	@NotNull
	@Size(max = 9)
	private String ntiOrgano;
	@NotNull
	private NtiOrigenEnumDto ntiOrigen;
	@NotNull
	private DocumentNtiEstadoElaboracionEnumDto ntiEstadoElaboracion;
	@NotNull
	@Size(max = 4)
	private String ntiTipoDocumental;
	@Size(max = 48)
	private String ntiIdDocumentoOrigen;
	private DocumentNtiTipoFirmaEnumDto ntiTipoFirma;
	@Size(max = 256)
	private String ntiCsv;
	@Size(max = 512)
	private String ntiCsvRegulacion;
	@Size(max = 512)
	protected String descripcio;
	
	//Tipus de document firmat:
		//Document firmat putjat manualment
		//Document firmat des dels navegador
		//Document firmat que es rep des del portafirmes callback
		//Document que vene d'una anotació de registre
		//Document generat de les resposta de PINBAL
	
	// document signed in portafirmes that arrived in callback and was not saved in arxiu 
	@Size(max = 256)
	private String gesDocFirmatId;
	@Size(max = 512)
	private String nomFitxerFirmat;
	//document uploaded manually in ripea that was not saved in arxiu
	// document sense firma o amb firma adjunta
	@Size(max = 256)
	private String gesDocAdjuntId;
	// firma separada
	@Size(max = 256)
	private String gesDocAdjuntFirmaId;
	//ID del contingut original guardat al sistema de fitxers
	@Size(max = 36)
	private String gesDocOriginalId;
	// firma separada of document saved as esborrany in arxiu
	@Size(max = 36)
	private String arxiuUuidFirma;
	@Size(max = 64)
	private String pinbalIdpeticion;
	private boolean validacioFirmaCorrecte;
	@Size(max = 1000)
	private String validacioFirmaErrorMsg;
	private ArxiuEstatEnumDto annexArxiuEstat;
	private ArxiuEstatEnumDto arxiuEstat;
	private DocumentFirmaTipusEnumDto documentFirmaTipus;
	private ResourceReference<ExpedientEstatResource, Long> expedientEstatAdditional;
}
