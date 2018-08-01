/**
 * 
 */
package es.caib.ripea.plugin.arxiu;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Estructura que representa un expedient de l'arxiu.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ArxiuExpedient extends ArxiuContingut {

	private String eniVersio;
	private String eniIdentificador;
	private ArxiuOrigenContingut eniOrigen;
	private Date eniDataObertura;
	private String eniClassificacio;
	private ArxiuExpedientEstat eniEstat;
	private List<String> eniOrgans;
	private List<String> eniInteressats;
	private List<String> eniFirmaTipus;
	private List<String> eniFirmaCsv;
	private List<String> eniFirmaCsvDefinicio;

	private List<ArxiuFill> fills;



	public ArxiuExpedient(
			String nodeId,
			String titol) {
		super(
				nodeId,
				titol,
				null,
				null,
				null);
	}
	public ArxiuExpedient(
			String nodeId,
			String titol,
			List<ArxiuFill> fills,
			Map<String, Object> metadades,
			List<String> aspectes,
			String json) {
		super(
				nodeId,
				titol,
				metadades,
				aspectes,
				json);
		this.fills = fills;
	}

	public List<ArxiuFill> getFills() {
		return fills;
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
	public Date getEniDataObertura() {
		return eniDataObertura;
	}
	public void setEniDataObertura(Date eniDataObertura) {
		this.eniDataObertura = eniDataObertura;
	}
	public String getEniClassificacio() {
		return eniClassificacio;
	}
	public void setEniClassificacio(String eniClassificacio) {
		this.eniClassificacio = eniClassificacio;
	}
	public ArxiuExpedientEstat getEniEstat() {
		return eniEstat;
	}
	public void setEniEstat(ArxiuExpedientEstat eniEstat) {
		this.eniEstat = eniEstat;
	}
	public List<String> getEniOrgans() {
		return eniOrgans;
	}
	public void setEniOrgans(List<String> eniOrgans) {
		this.eniOrgans = eniOrgans;
	}
	public List<String> getEniInteressats() {
		return eniInteressats;
	}
	public void setEniInteressats(List<String> eniInteressats) {
		this.eniInteressats = eniInteressats;
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

	/*public String getEniVersio() {
		return getMetadataValueAsString(
				metadades,
				metadadesMapeig.get(ArxiuMetadadaEnum.ENI_VERSIO));
	}
	public String getEniIdentificador() {
		return getMetadataValueAsString(
				metadades,
				metadadesMapeig.get(ArxiuMetadadaEnum.ENI_IDENTIFICADOR));
	}
	public ArxiuOrigenContingut getEniOrigen() {
		String origen = getMetadataValueAsString(
				metadades,
				metadadesMapeig.get(ArxiuMetadadaEnum.ENI_ORIGEN));
		return ArxiuOrigenContingut.valorAsEnum(origen);
	}

	public List<String> getEniOrgans() {
		return getMetadataValueAsListString(
				metadades,
				metadadesMapeig.get(ArxiuMetadadaEnum.ENI_ORGANS));
	}
	public Date getEniDataObertura() throws ParseException {
		return getMetadataValueAsDate(
				metadades,
				metadadesMapeig.get(ArxiuMetadadaEnum.ENI_DATA_OBERTURA));
	}
	public String getEniClassificacio() {
		return getMetadataValueAsString(
				metadades,
				metadadesMapeig.get(ArxiuMetadadaEnum.ENI_CLASSIFICACIO));
	}
	public ArxiuExpedientEstat getEniEstat() {
		String estat = getMetadataValueAsString(
				metadades,
				metadadesMapeig.get(ArxiuMetadadaEnum.ENI_ESTAT));
		return ArxiuExpedientEstat.valorAsEnum(estat);
	}
	public List<String> getEniInteressats() {
		return getMetadataValueAsListString(
				metadades,
				metadadesMapeig.get(ArxiuMetadadaEnum.ENI_INTERESSATS));
	}
	public List<String> getEniFirmaTipus() {
		return getMetadataValueAsListString(
				metadades,
				metadadesMapeig.get(ArxiuMetadadaEnum.ENI_FIRMA_TIPUS));
	}
	public List<String> getEniFirmaCsv() {
		return getMetadataValueAsListString(
				metadades,
				metadadesMapeig.get(ArxiuMetadadaEnum.ENI_FIRMA_CSV));
	}
	public List<String> getEniFirmaCsvDefinicio() {
		return getMetadataValueAsListString(
				metadades,
				metadadesMapeig.get(ArxiuMetadadaEnum.ENI_FIRMA_CSV_DEF));
	}*/
	/*private String nodeId;
	private String titol;
	private String descripcio;

	private String eniVersio;
	private String eniIdentificador;
	private ArxiuOrigenContingut eniOrigen;
	private Date eniDataObertura;
	private String eniClassificacio;
	private ArxiuExpedientEstat eniEstat;
	private List<String> eniOrgans;
	private List<String> eniInteressats;
	private List<String> eniFirmaTipus;
	private List<String> eniFirmaCsv;
	private List<String> eniFirmaCsvDefinicio;

	private String serieDocumental;
	private String aplicacioCreacio;

	private String json;

	public String getNodeId() {
		return nodeId;
	}
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
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
	public Date getEniDataObertura() {
		return eniDataObertura;
	}
	public void setEniDataObertura(Date eniDataObertura) {
		this.eniDataObertura = eniDataObertura;
	}
	public String getEniClassificacio() {
		return eniClassificacio;
	}
	public void setEniClassificacio(String eniClassificacio) {
		this.eniClassificacio = eniClassificacio;
	}
	public ArxiuExpedientEstat getEniEstat() {
		return eniEstat;
	}
	public void setEniEstat(ArxiuExpedientEstat eniEstat) {
		this.eniEstat = eniEstat;
	}
	public List<String> getEniOrgans() {
		return eniOrgans;
	}
	public void setEniOrgans(List<String> eniOrgans) {
		this.eniOrgans = eniOrgans;
	}
	public List<String> getEniInteressats() {
		return eniInteressats;
	}
	public void setEniInteressats(List<String> eniInteressats) {
		this.eniInteressats = eniInteressats;
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
	public String getJson() {
		return json;
	}
	public void setJson(String json) {
		this.json = json;
	}*/

}
