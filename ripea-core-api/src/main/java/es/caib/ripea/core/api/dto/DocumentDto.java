/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.util.Date;
import java.util.List;

/**
 * Informació d'un document.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
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
	
	private Long pareId;
	
	
	public Long getPareId() {
		return pareId;
	}

	public void setPareId(Long pareId) {
		this.pareId = pareId;
	}

	public String getFitxerExtension() {
		return fitxerNom.substring(
				fitxerNom.lastIndexOf('.')+1,
				fitxerNom.length());
	}
	
	public String getFitxerExtensionUpperCase() {
		return getFitxerExtension().toUpperCase();
	}

	public boolean isFirmaSeparada() {
		return firmaSeparada;
	}
	public void setFirmaSeparada(boolean firmaSeparada) {
		this.firmaSeparada = firmaSeparada;
	}
	public DocumentTipusEnumDto getDocumentTipus() {
		return documentTipus;
	}
	public void setDocumentTipus(DocumentTipusEnumDto documentTipus) {
		this.documentTipus = documentTipus;
	}
	public DocumentEstatEnumDto getEstat() {
		return estat;
	}
	public void setEstat(DocumentEstatEnumDto estat) {
		this.estat = estat;
	}
	public String getUbicacio() {
		return ubicacio;
	}
	public void setUbicacio(String ubicacio) {
		this.ubicacio = ubicacio;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public Date getCustodiaData() {
		return custodiaData;
	}
	public void setCustodiaData(Date custodiaData) {
		this.custodiaData = custodiaData;
	}
	public String getCustodiaId() {
		return custodiaId;
	}
	public void setCustodiaId(String custodiaId) {
		this.custodiaId = custodiaId;
	}
	public String getCustodiaUrl() {
		return custodiaUrl;
	}
	public void setCustodiaUrl(String custodiaUrl) {
		this.custodiaUrl = custodiaUrl;
	}
	public String getFitxerNom() {
		return fitxerNom;
	}
	public void setFitxerNom(String fitxerNom) {
		this.fitxerNom = fitxerNom;
	}
	public String getFitxerNomEnviamentPortafirmes() {
		return fitxerNomEnviamentPortafirmes;
	}
	public void setFitxerNomEnviamentPortafirmes(String fitxerNomEnviamentPortafirmes) {
		this.fitxerNomEnviamentPortafirmes = fitxerNomEnviamentPortafirmes;
	}
	public String getFitxerContentType() {
		return fitxerContentType;
	}
	public void setFitxerContentType(String fitxerContentType) {
		this.fitxerContentType = fitxerContentType;
	}
	public byte[] getFitxerContingut() {
		return fitxerContingut;
	}
	public void setFitxerContingut(byte[] fitxerContingut) {
		this.fitxerContingut = fitxerContingut;
	}
	public boolean isAmbFirma() {
		return ambFirma;
	}
	public void setAmbFirma(boolean ambFirma) {
		this.ambFirma = ambFirma;
	}
	public String getFirmaNom() {
		return firmaNom;
	}
	public void setFirmaNom(String firmaNom) {
		this.firmaNom = firmaNom;
	}
	public String getFirmaContentType() {
		return firmaContentType;
	}
	public void setFirmaContentType(String firmaContentType) {
		this.firmaContentType = firmaContentType;
	}
	public byte[] getFirmaContingut() {
		return firmaContingut;
	}
	public void setFirmaContingut(byte[] firmaContingut) {
		this.firmaContingut = firmaContingut;
	}
	public DocumentTipusFirmaEnumDto getTipusFirma() {
		return tipusFirma;
	}
	public void setTipusFirma(DocumentTipusFirmaEnumDto tipusFirma) {
		this.tipusFirma = tipusFirma;
	}
	public Date getDataCaptura() {
		return dataCaptura;
	}
	public void setDataCaptura(Date dataCaptura) {
		this.dataCaptura = dataCaptura;
	}
	public String getNtiVersion() {
		return ntiVersion;
	}
	public void setNtiVersion(String ntiVersion) {
		this.ntiVersion = ntiVersion;
	}
	public String getNtiIdentificador() {
		return ntiIdentificador;
	}
	public void setNtiIdentificador(String ntiIdentificador) {
		this.ntiIdentificador = ntiIdentificador;
	}
	public String getNtiOrgano() {
		return ntiOrgano;
	}
	public void setNtiOrgano(String ntiOrgano) {
		this.ntiOrgano = ntiOrgano;
	}
	public String getNtiOrganoDescripcio() {
		return ntiOrganoDescripcio;
	}
	public void setNtiOrganoDescripcio(String ntiOrganoDescripcio) {
		this.ntiOrganoDescripcio = ntiOrganoDescripcio;
	}
	public NtiOrigenEnumDto getNtiOrigen() {
		return ntiOrigen;
	}
	public void setNtiOrigen(NtiOrigenEnumDto ntiOrigen) {
		this.ntiOrigen = ntiOrigen;
	}
	public DocumentNtiEstadoElaboracionEnumDto getNtiEstadoElaboracion() {
		return ntiEstadoElaboracion;
	}
	public void setNtiEstadoElaboracion(DocumentNtiEstadoElaboracionEnumDto ntiEstadoElaboracion) {
		this.ntiEstadoElaboracion = ntiEstadoElaboracion;
	}
	public String getNtiTipoDocumental() {
		return ntiTipoDocumental;
	}
	public void setNtiTipoDocumental(String ntiTipoDocumental) {
		this.ntiTipoDocumental = ntiTipoDocumental;
	}
	public String getNtiTipoDocumentalNom() {
		return ntiTipoDocumentalNom;
	}
	public void setNtiTipoDocumentalNom(String ntiTipoDocumentalNom) {
		this.ntiTipoDocumentalNom = ntiTipoDocumentalNom;
	}
	public String getNtiIdDocumentoOrigen() {
		return ntiIdDocumentoOrigen;
	}
	public void setNtiIdDocumentoOrigen(String ntiIdDocumentoOrigen) {
		this.ntiIdDocumentoOrigen = ntiIdDocumentoOrigen;
	}
	public DocumentNtiTipoFirmaEnumDto getNtiTipoFirma() {
		return ntiTipoFirma;
	}
	public void setNtiTipoFirma(DocumentNtiTipoFirmaEnumDto ntiTipoFirma) {
		this.ntiTipoFirma = ntiTipoFirma;
	}
	public String getNtiCsv() {
		return ntiCsv;
	}
	public void setNtiCsv(String ntiCsv) {
		this.ntiCsv = ntiCsv;
	}
	public String getNtiCsvRegulacion() {
		return ntiCsvRegulacion;
	}
	public void setNtiCsvRegulacion(String ntiCsvRegulacion) {
		this.ntiCsvRegulacion = ntiCsvRegulacion;
	}
	public String getVersioDarrera() {
		return versioDarrera;
	}
	public void setVersioDarrera(String versioDarrera) {
		this.versioDarrera = versioDarrera;
	}
	public int getVersioCount() {
		return versioCount;
	}
	public void setVersioCount(int versioCount) {
		this.versioCount = versioCount;
	}
	public List<DocumentVersioDto> getVersions() {
		return versions;
	}
	public void setVersions(List<DocumentVersioDto> versions) {
		this.versions = versions;
	}
	public boolean isAmbNotificacions() {
		return ambNotificacions;
	}
	public void setAmbNotificacions(boolean ambNotificacions) {
		this.ambNotificacions = ambNotificacions;
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

	protected DocumentDto copiarContenidor(ContingutDto original) {
		DocumentDto copia = new DocumentDto();
		copia.setId(original.getId());
		copia.setNom(original.getNom());
		return copia;
	}

}
