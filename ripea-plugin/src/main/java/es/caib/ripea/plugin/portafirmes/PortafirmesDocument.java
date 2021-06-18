/**
 * 
 */
package es.caib.ripea.plugin.portafirmes;

import java.util.List;

/**
 * Document o annex per enviar al portafirmes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PortafirmesDocument {

	private String titol;
	private String descripcio;
	private boolean firmat;
	private String arxiuNom;
	private String arxiuMime;
	private byte[] arxiuContingut;

	private boolean custodiat;
	private String custodiaId;
	private String custodiaUrl;
	private String arxiuUuid;
	private String expedientUuid;
	private String tipusFirma;
	
	private List<PortafirmesDocumentFirmant> firmants;
	
	public String getTitol() {
		return titol;
	}
	public void setTitol(String titol) {
		this.titol = titol;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		if (descripcio != null && descripcio.length() > 255)
			descripcio = descripcio.substring(0, 252).trim() + "...";
		this.descripcio = descripcio;
	}
	public boolean isFirmat() {
		return firmat;
	}
	public void setFirmat(boolean firmat) {
		this.firmat = firmat;
	}
	public String getArxiuNom() {
		return arxiuNom;
	}
	public void setArxiuNom(String arxiuNom) {
		this.arxiuNom = arxiuNom;
	}
	public String getArxiuMime() {
		return arxiuMime;
	}
	public void setArxiuMime(String arxiuMime) {
		this.arxiuMime = arxiuMime;
	}
	public byte[] getArxiuContingut() {
		return arxiuContingut;
	}
	public void setArxiuContingut(byte[] arxiuContingut) {
		this.arxiuContingut = arxiuContingut;
	}
	public boolean isCustodiat() {
		return custodiat;
	}
	public void setCustodiat(boolean custodiat) {
		this.custodiat = custodiat;
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
	public String getArxiuUuid() {
		return arxiuUuid;
	}
	public void setArxiuUuid(String arxiuUuid) {
		this.arxiuUuid = arxiuUuid;
	}
	public String getExpedientUuid() {
		return expedientUuid;
	}
	public void setExpedientUuid(String expedientUuid) {
		this.expedientUuid = expedientUuid;
	}
	public String getTipusFirma() {
		return tipusFirma;
	}
	public void setTipusFirma(String tipusFirma) {
		this.tipusFirma = tipusFirma;
	}
	public List<PortafirmesDocumentFirmant> getFirmants() {
		return firmants;
	}
	public void setFirmants(List<PortafirmesDocumentFirmant> firmants) {
		this.firmants = firmants;
	}
	public String getArxiuExtensio() {
		int index = arxiuNom.lastIndexOf(".");
		if (index != -1) {
			return arxiuNom.substring(index + 1);
		} else {
			return "";
		}
	}

}
