/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'un MetaDocument.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class MetaDocumentDto extends MetaNodeAmbMetaDadesDto implements Serializable {

	private MultiplicitatEnumDto multiplicitat;
	private boolean firmaPortafirmesActiva;
	private String portafirmesDocumentTipus;
	private String portafirmesFluxId;
	private String[] portafirmesResponsables;
	private MetaDocumentFirmaFluxTipusEnumDto portafirmesFluxTipus;
	private String portafirmesCustodiaTipus;
	private boolean firmaPassarelaActiva;
	private String firmaPassarelaCustodiaTipus;
	private String plantillaNom;
	private String plantillaContentType;
	private DocumentNtiEstadoElaboracionEnumDto ntiEstadoElaboracion;
	private DocumentNtiTipoDocumentalEnumDto ntiTipoDocumental;
	private NtiOrigenEnumDto ntiOrigen;
	
	public MultiplicitatEnumDto getMultiplicitat() {
		return multiplicitat;
	}
	public void setMultiplicitat(MultiplicitatEnumDto multiplicitat) {
		this.multiplicitat = multiplicitat;
	}
	public boolean isFirmaPortafirmesActiva() {
		return firmaPortafirmesActiva;
	}
	public void setFirmaPortafirmesActiva(boolean firmaPortafirmesActiva) {
		this.firmaPortafirmesActiva = firmaPortafirmesActiva;
	}
	public String getPortafirmesDocumentTipus() {
		return portafirmesDocumentTipus;
	}
	public void setPortafirmesDocumentTipus(String portafirmesDocumentTipus) {
		this.portafirmesDocumentTipus = portafirmesDocumentTipus;
	}
	public String getPortafirmesFluxId() {
		return portafirmesFluxId;
	}
	public void setPortafirmesFluxId(String portafirmesFluxId) {
		this.portafirmesFluxId = portafirmesFluxId;
	}
	public String[] getPortafirmesResponsables() {
		return portafirmesResponsables;
	}
	public void setPortafirmesResponsables(String[] portafirmesResponsables) {
		this.portafirmesResponsables = portafirmesResponsables;
	}
	public MetaDocumentFirmaFluxTipusEnumDto getPortafirmesFluxTipus() {
		return portafirmesFluxTipus;
	}
	public void setPortafirmesFluxTipus(MetaDocumentFirmaFluxTipusEnumDto portafirmesFluxTipus) {
		this.portafirmesFluxTipus = portafirmesFluxTipus;
	}
	public String getPortafirmesCustodiaTipus() {
		return portafirmesCustodiaTipus;
	}
	public void setPortafirmesCustodiaTipus(String portafirmesCustodiaTipus) {
		this.portafirmesCustodiaTipus = portafirmesCustodiaTipus;
	}
	public boolean isFirmaPassarelaActiva() {
		return firmaPassarelaActiva;
	}
	public void setFirmaPassarelaActiva(boolean firmaPassarelaActiva) {
		this.firmaPassarelaActiva = firmaPassarelaActiva;
	}
	public String getFirmaPassarelaCustodiaTipus() {
		return firmaPassarelaCustodiaTipus;
	}
	public void setFirmaPassarelaCustodiaTipus(String firmaPassarelaCustodiaTipus) {
		this.firmaPassarelaCustodiaTipus = firmaPassarelaCustodiaTipus;
	}
	public String getPlantillaNom() {
		return plantillaNom;
	}
	public void setPlantillaNom(String plantillaNom) {
		this.plantillaNom = plantillaNom;
	}
	public String getPlantillaContentType() {
		return plantillaContentType;
	}
	public void setPlantillaContentType(String plantillaContentType) {
		this.plantillaContentType = plantillaContentType;
	}
	public DocumentNtiEstadoElaboracionEnumDto getNtiEstadoElaboracion() {
		return ntiEstadoElaboracion;
	}
	public void setNtiEstadoElaboracion(DocumentNtiEstadoElaboracionEnumDto ntiEstadoElaboracion) {
		this.ntiEstadoElaboracion = ntiEstadoElaboracion;
	}
	public DocumentNtiTipoDocumentalEnumDto getNtiTipoDocumental() {
		return ntiTipoDocumental;
	}
	public void setNtiTipoDocumental(DocumentNtiTipoDocumentalEnumDto ntiTipoDocumental) {
		this.ntiTipoDocumental = ntiTipoDocumental;
	}
	public NtiOrigenEnumDto getNtiOrigen() {
		return ntiOrigen;
	}
	public void setNtiOrigen(NtiOrigenEnumDto ntiOrigen) {
		this.ntiOrigen = ntiOrigen;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MetaDocumentDto other = (MetaDocumentDto) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	private static final long serialVersionUID = -139254994389509932L;

}
