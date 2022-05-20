/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

import lombok.Getter;
import lombok.Setter;

/**
 * Informació d'un MetaDocument.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class MetaDocumentDto extends MetaNodeAmbMetaDadesDto implements Serializable {

	private MultiplicitatEnumDto multiplicitat;
	private boolean firmaPortafirmesActiva;
	private String portafirmesDocumentTipus;
	private String portafirmesFluxId;
	private String[] portafirmesResponsables;
	private MetaDocumentFirmaSequenciaTipusEnumDto portafirmesSequenciaTipus;
	private String portafirmesCustodiaTipus;
	private boolean firmaPassarelaActiva;
	private String firmaPassarelaCustodiaTipus;
	private String plantillaNom;
	private String plantillaContentType;
	private byte[] plantillaContingut;
	private DocumentNtiEstadoElaboracionEnumDto ntiEstadoElaboracion;
	private String ntiTipoDocumental;
	private NtiOrigenEnumDto ntiOrigen;
	private boolean firmaBiometricaActiva;
	private boolean biometricaLectura;
	private MetaDocumentFirmaFluxTipusEnumDto portafirmesFluxTipus;
	private boolean pinbalActiu;
	private MetaDocumentPinbalServeiEnumDto pinbalServei;
	private String pinbalFinalitat;
	private boolean perDefecte;
	private Long metaExpedientId;
	private boolean leftPerCreacio;
	
	
	public boolean isPermetMultiple() {
		return multiplicitat.equals(MultiplicitatEnumDto.M_0_N) || multiplicitat.equals(MultiplicitatEnumDto.M_1_N);
	}
	public boolean isLeftPerCreacio() {
		return leftPerCreacio;
	}
	public void setLeftPerCreacio(boolean leftPerCreacio) {
		this.leftPerCreacio = leftPerCreacio;
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
