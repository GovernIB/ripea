/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.util.Date;
import java.util.List;

import es.caib.ripea.core.api.utils.Utils;
import lombok.Getter;
import lombok.Setter;

/**
 * Informació d'un document.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class DocumentDto extends NodeDto {

	protected DocumentTipusEnumDto documentTipus;
	protected DocumentEstatEnumDto estat;
	protected String ubicacio;
	private Date data;
	private Date custodiaData;
	private String custodiaId;
	private String custodiaUrl;
	private String fitxerNom;
	private String fitxerNomEnviamentPortafirmes;
	private String fitxerContentType;
	private byte[] fitxerContingut;
	private Long fitxerTamany;
	private boolean ambFirma;
	private String firmaNom;
	private String firmaContentType;
	private byte[] firmaContingut;
	private DocumentTipusFirmaEnumDto tipusFirma;
 	private Date dataCaptura;
	private String ntiVersion;
	private String ntiIdentificador;
	private String ntiOrgano;
	private String ntiOrganoDescripcio;
	private NtiOrigenEnumDto ntiOrigen;
	private DocumentNtiEstadoElaboracionEnumDto ntiEstadoElaboracion;
	private String ntiTipoDocumental;
	private String ntiTipoDocumentalNom;
	private String ntiIdDocumentoOrigen;
	private DocumentNtiTipoFirmaEnumDto ntiTipoFirma;
	private String ntiCsv;
	private String ntiCsvRegulacion;
	private String versioDarrera;
	private int versioCount;
	private List<DocumentVersioDto> versions;
	private boolean firmaSeparada;
	private boolean ambNotificacions;
	private boolean errorDarreraNotificacio;
	private String estatDarreraNotificacio;
	private boolean errorEnviamentPortafirmes;
	private String descripcio;
	
	private Long pareId;
	private boolean docFromAnnex;
	
	private String gesDocFirmatId;
	
	private String gesDocAdjuntId;
	private String gesDocAdjuntFirmaId;
	
	private String pinbalIdpeticion;
	
	private boolean pendentMoverArxiu;
	private Long annexId;

	private boolean validacioFirmaCorrecte;
	private String validacioFirmaErrorMsg;
	private ArxiuEstatEnumDto annexArxiuEstat; // Estat a l'arxiu en l'origen
	
	private ArxiuEstatEnumDto arxiuEstat;
	
	private DocumentFirmaTipusEnumDto documentFirmaTipus;
	
	private boolean arxiuEstatDefinitiu;
	private boolean documentDeAnotacio;
	
	private boolean scanned;
	
	private Integer resolucion;
	private String idioma;
	
	public String getFitxerExtension() {
		if (fitxerNom != null) {
			return fitxerNom.substring(
					fitxerNom.lastIndexOf('.') + 1,
					fitxerNom.length());
		} else {
			return "";
		}
	}
	
	public String getFitxerExtensionUpperCase() {
		return getFitxerExtension().toUpperCase();
	}

	public MetaDocumentDto getMetaDocument() {
		return (MetaDocumentDto)getMetaNode();
	}

	public String getNtiVersionUrl() {
		return "http://administracionelectronica.gob.es/ENI/XSD/V" + ntiVersion + "/expediente-e";
	}

	public boolean isFirmat() {
		return DocumentEstatEnumDto.FIRMAT.equals(estat);
	}
	public boolean isCustodiat() {
		return DocumentEstatEnumDto.CUSTODIAT.equals(estat);
	}
	public boolean isDefinitiu() {
		return DocumentEstatEnumDto.DEFINITIU.equals(estat);
	}
	public boolean isFirmaParcial() {
		return DocumentEstatEnumDto.FIRMA_PARCIAL.equals(estat);
	}
	public boolean isPdf() {
		return (fitxerContentType != null && fitxerContentType.equals("application/pdf"));
	}
	protected DocumentDto copiarContenidor(ContingutDto original) {
		DocumentDto copia = new DocumentDto();
		copia.setId(original.getId());
		copia.setNom(original.getNom());
		return copia;
	}
	
	
	
	public String getFitxerTamanyStr() {
		return Utils.getTamanyString(this.fitxerTamany);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		DocumentDto other = (DocumentDto) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id)) {
			return false;
	 	} else if (id.equals(other.id))
			return true;
		return true;
	}
	
	@Override
	public String toString() {
		return "DocumentDto: [ \n"
					+ "id" + id + "\n"
					+ "ntiTipoDocumental: " + ntiTipoDocumental + "\n"
					+ "ntiTipoDocumentalNom: " + ntiTipoDocumentalNom + "]";
	}

}
