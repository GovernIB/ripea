/**
 * 
 */
package es.caib.ripea.plugin.arxiu;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Estructura que representa un document de l'arxiu.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ArxiuDocument extends ArxiuContingut {

	private String versioId;
	private String versioNodeId;
	private String eniVersio;
	private String eniIdentificador;
	private ArxiuOrigenContingut eniOrigen;
	private Date eniDataCaptura;
	private ArxiuEstatElaboracio eniEstatElaboracio;
	private ArxiuTipusDocumental eniTipusDocumental;
	private ArxiuFormatNom eniFormatNom;
	private ArxiuFormatExtensio eniFormatExtensio;
	private List<String> eniOrgans;
	private List<String> eniFirmaTipus;
	private List<String> eniFirmaCsv;
	private List<String> eniFirmaCsvDefinicio;
	private String eniDocumentOrigenId;

	private List<ArxiuDocumentContingut> continguts;

	public ArxiuDocument(
			String nodeId,
			String titol,
			List<ArxiuDocumentContingut> continguts) {
		super(
				nodeId,
				titol,
				null,
				null,
				null);
		this.continguts = continguts;
	}

	public ArxiuDocument(
			String nodeId,
			String titol,
			List<ArxiuDocumentContingut> continguts,
			Map<String, Object> metadades,
			List<String> aspectes,
			String json) {
		super(
				nodeId,
				titol,
				metadades,
				aspectes,
				json);
		this.continguts = continguts;
	}

	public String getVersioId() {
		return versioId;
	}
	public void setVersioId(String versioId) {
		this.versioId = versioId;
	}
	public String getVersioNodeId() {
		return versioNodeId;
	}
	public void setVersioNodeId(String versioNodeId) {
		this.versioNodeId = versioNodeId;
	}
	public String getEniVersio() {
		return eniVersio;
	}
	public void setEniVersio(String eniVersio) {
		this.eniVersio = eniVersio;
	}
	public String getEniIdentificador() {
		return eniIdentificador;
	}
	public void setEniIdentificador(String eniIdentificador) {
		this.eniIdentificador = eniIdentificador;
	}
	public ArxiuOrigenContingut getEniOrigen() {
		return eniOrigen;
	}
	public void setEniOrigen(ArxiuOrigenContingut eniOrigen) {
		this.eniOrigen = eniOrigen;
	}
	public Date getEniDataCaptura() {
		return eniDataCaptura;
	}
	public void setEniDataCaptura(Date eniDataCaptura) {
		this.eniDataCaptura = eniDataCaptura;
	}
	public ArxiuEstatElaboracio getEniEstatElaboracio() {
		return eniEstatElaboracio;
	}
	public void setEniEstatElaboracio(ArxiuEstatElaboracio eniEstatElaboracio) {
		this.eniEstatElaboracio = eniEstatElaboracio;
	}
	public ArxiuTipusDocumental getEniTipusDocumental() {
		return eniTipusDocumental;
	}
	public void setEniTipusDocumental(ArxiuTipusDocumental eniTipusDocumental) {
		this.eniTipusDocumental = eniTipusDocumental;
	}
	public ArxiuFormatNom getEniFormatNom() {
		return eniFormatNom;
	}
	public void setEniFormatNom(ArxiuFormatNom eniFormatNom) {
		this.eniFormatNom = eniFormatNom;
	}
	public ArxiuFormatExtensio getEniFormatExtensio() {
		return eniFormatExtensio;
	}
	public void setEniFormatExtensio(ArxiuFormatExtensio eniFormatExtensio) {
		this.eniFormatExtensio = eniFormatExtensio;
	}
	public List<String> getEniOrgans() {
		return eniOrgans;
	}
	public void setEniOrgans(List<String> eniOrgans) {
		this.eniOrgans = eniOrgans;
	}
	public List<String> getEniFirmaTipus() {
		return eniFirmaTipus;
	}
	public void setEniFirmaTipus(List<String> eniFirmaTipus) {
		this.eniFirmaTipus = eniFirmaTipus;
	}
	public List<String> getEniFirmaCsv() {
		return eniFirmaCsv;
	}
	public void setEniFirmaCsv(List<String> eniFirmaCsv) {
		this.eniFirmaCsv = eniFirmaCsv;
	}
	public List<String> getEniFirmaCsvDefinicio() {
		return eniFirmaCsvDefinicio;
	}
	public void setEniFirmaCsvDefinicio(List<String> eniFirmaCsvDefinicio) {
		this.eniFirmaCsvDefinicio = eniFirmaCsvDefinicio;
	}
	public String getEniDocumentOrigenId() {
		return eniDocumentOrigenId;
	}
	public void setEniDocumentOrigenId(String eniDocumentOrigenId) {
		this.eniDocumentOrigenId = eniDocumentOrigenId;
	}

	public List<ArxiuDocumentContingut> getContinguts() {
		return continguts;
	}

	/*private String nodeId;
	private String versioId;
	private String versioNodeId;
	private String titol;
	private String descripcio;

	private String eniVersio;
	private String eniIdentificador;
	private ArxiuOrigenContingut eniOrigen;
	private Date eniDataCaptura;
	private ArxiuEstatElaboracio eniEstatElaboracio;
	private ArxiuTipusDocumental eniTipusDocumental;
	private ArxiuFormatNom eniFormatNom;
	private ArxiuFormatExtensio eniFormatExtensio;
	private List<String> eniOrgans;
	private List<String> eniFirmaTipus;
	private List<String> eniFirmaCsv;
	private List<String> eniFirmaCsvDefinicio;
	private String eniDocumentOrigenId;

	private String serieDocumental;
	private String aplicacioCreacio;

	private ArxiuDocumentContingut contingut;

	private String json;

	public String getNodeId() {
		return nodeId;
	}
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	public String getVersioId() {
		return versioId;
	}
	public void setVersioId(String versioId) {
		this.versioId = versioId;
	}
	public String getVersioNodeId() {
		return versioNodeId;
	}
	public void setVersioNodeId(String versioNodeId) {
		this.versioNodeId = versioNodeId;
	}
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
		this.descripcio = descripcio;
	}
	public String getEniVersio() {
		return eniVersio;
	}
	public void setEniVersio(String eniVersio) {
		this.eniVersio = eniVersio;
	}
	public String getEniIdentificador() {
		return eniIdentificador;
	}
	public void setEniIdentificador(String eniIdentificador) {
		this.eniIdentificador = eniIdentificador;
	}
	public ArxiuOrigenContingut getEniOrigen() {
		return eniOrigen;
	}
	public void setEniOrigen(ArxiuOrigenContingut eniOrigen) {
		this.eniOrigen = eniOrigen;
	}
	public Date getEniDataCaptura() {
		return eniDataCaptura;
	}
	public void setEniDataCaptura(Date eniDataCaptura) {
		this.eniDataCaptura = eniDataCaptura;
	}
	public ArxiuEstatElaboracio getEniEstatElaboracio() {
		return eniEstatElaboracio;
	}
	public void setEniEstatElaboracio(ArxiuEstatElaboracio eniEstatElaboracio) {
		this.eniEstatElaboracio = eniEstatElaboracio;
	}
	public ArxiuTipusDocumental getEniTipusDocumental() {
		return eniTipusDocumental;
	}
	public void setEniTipusDocumental(ArxiuTipusDocumental eniTipusDocumental) {
		this.eniTipusDocumental = eniTipusDocumental;
	}
	public ArxiuFormatNom getEniFormatNom() {
		return eniFormatNom;
	}
	public void setEniFormatNom(ArxiuFormatNom eniFormatNom) {
		this.eniFormatNom = eniFormatNom;
	}
	public ArxiuFormatExtensio getEniFormatExtensio() {
		return eniFormatExtensio;
	}
	public void setEniFormatExtensio(ArxiuFormatExtensio eniFormatExtensio) {
		this.eniFormatExtensio = eniFormatExtensio;
	}
	public List<String> getEniOrgans() {
		return eniOrgans;
	}
	public void setEniOrgans(List<String> eniOrgans) {
		this.eniOrgans = eniOrgans;
	}
	public List<String> getEniFirmaTipus() {
		return eniFirmaTipus;
	}
	public void setEniFirmaTipus(List<String> eniFirmaTipus) {
		this.eniFirmaTipus = eniFirmaTipus;
	}
	public List<String> getEniFirmaCsv() {
		return eniFirmaCsv;
	}
	public void setEniFirmaCsv(List<String> eniFirmaCsv) {
		this.eniFirmaCsv = eniFirmaCsv;
	}
	public List<String> getEniFirmaCsvDefinicio() {
		return eniFirmaCsvDefinicio;
	}
	public void setEniFirmaCsvDefinicio(List<String> eniFirmaCsvDefinicio) {
		this.eniFirmaCsvDefinicio = eniFirmaCsvDefinicio;
	}
	public String getEniDocumentOrigenId() {
		return eniDocumentOrigenId;
	}
	public void setEniDocumentOrigenId(String eniDocumentOrigenId) {
		this.eniDocumentOrigenId = eniDocumentOrigenId;
	}
	public String getSerieDocumental() {
		return serieDocumental;
	}
	public void setSerieDocumental(String serieDocumental) {
		this.serieDocumental = serieDocumental;
	}
	public String getAplicacioCreacio() {
		return aplicacioCreacio;
	}
	public void setAplicacioCreacio(String aplicacioCreacio) {
		this.aplicacioCreacio = aplicacioCreacio;
	}
	public ArxiuDocumentContingut getContingut() {
		return contingut;
	}
	public void setContingut(ArxiuDocumentContingut contingut) {
		this.contingut = contingut;
	}
	public String getJson() {
		return json;
	}
	public void setJson(String json) {
		this.json = json;
	}*/

}
