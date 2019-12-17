/**
 * 
 */
package es.caib.ripea.core.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.ripea.core.api.dto.DocumentNtiEstadoElaboracionEnumDto;
import es.caib.ripea.core.api.dto.DocumentNtiTipoDocumentalEnumDto;
import es.caib.ripea.core.api.dto.MetaDocumentFirmaFluxTipusEnumDto;
import es.caib.ripea.core.api.dto.MetaDocumentTipusGenericEnumDto;
import es.caib.ripea.core.api.dto.MultiplicitatEnumDto;
import es.caib.ripea.core.api.dto.NtiOrigenEnumDto;

/**
 * Classe del model de dades que representa un meta-document.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "ipa_metadocument")
@EntityListeners(AuditingEntityListener.class)
public class MetaDocumentEntity extends MetaNodeEntity {

	@Column(name = "multiplicitat")
	private MultiplicitatEnumDto multiplicitat;
	@Column(name = "firma_pfirma")
	private boolean firmaPortafirmesActiva;
	@Column(name = "portafirmes_doctip", length = 64)
	private String portafirmesDocumentTipus;
	@Column(name = "portafirmes_fluxid", length = 64)
	private String portafirmesFluxId;
	@Column(name = "portafirmes_respons", length = 512)
	private String portafirmesResponsables;
	@Enumerated(EnumType.STRING)
	@Column(name = "portafirmes_fluxtip")
	private MetaDocumentFirmaFluxTipusEnumDto portafirmesFluxTipus;
	@Column(name = "portafirmes_custip", length = 64)
	private String portafirmesCustodiaTipus;
	@Column(name = "firma_passarela")
	private boolean firmaPassarelaActiva;
	@Column(name = "passarela_custip", length = 64)
	private String firmaPassarelaCustodiaTipus;
	@Column(name = "plantilla_nom", length = 256)
	private String plantillaNom;
	@Column(name = "plantilla_content_type", length = 256)
	private String plantillaContentType;
	@Lob
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "plantilla_contingut")
	private byte[] plantillaContingut;
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "meta_expedient_id")
	@ForeignKey(name = "ipa_metaexp_metadoc_fk")
	private MetaExpedientEntity metaExpedient;
	
	@Column(name = "nti_origen", length = 2)
	@Enumerated(EnumType.STRING)
	private NtiOrigenEnumDto ntiOrigen;
	@Column(name = "nti_estela", length = 4)
	@Enumerated(EnumType.STRING)
	private DocumentNtiEstadoElaboracionEnumDto ntiEstadoElaboracion;
	@Column(name = "nti_tipdoc", length = 4)
	@Enumerated(EnumType.STRING)
	private DocumentNtiTipoDocumentalEnumDto ntiTipoDocumental;
	
	@Column(name = "firma_biometrica")
	private boolean firmaBiometricaActiva;
	@Column(name = "biometrica_lectura")
	private boolean biometricaLectura;
	
	@Column(name = "meta_document_tipus_gen", length = 256)
	@Enumerated(EnumType.STRING)
	private MetaDocumentTipusGenericEnumDto metaDocumentTipusGeneric;
	
	public MultiplicitatEnumDto getMultiplicitat() {
		return multiplicitat;
	}
	public boolean isFirmaPortafirmesActiva() {
		return firmaPortafirmesActiva;
	}
	public String getPortafirmesDocumentTipus() {
		return portafirmesDocumentTipus;
	}
	public String getPortafirmesFluxId() {
		return portafirmesFluxId;
	}
	public String[] getPortafirmesResponsables() {
		if (portafirmesResponsables == null)
			return null;
		return portafirmesResponsables.split(",");
	}
	public MetaDocumentFirmaFluxTipusEnumDto getPortafirmesFluxTipus() {
		return portafirmesFluxTipus;
	}
	public String getPortafirmesCustodiaTipus() {
		return portafirmesCustodiaTipus;
	}
	public boolean isFirmaPassarelaActiva() {
		return firmaPassarelaActiva;
	}
	public String getFirmaPassarelaCustodiaTipus() {
		return firmaPassarelaCustodiaTipus;
	}
	public String getPlantillaNom() {
		return plantillaNom;
	}
	public String getPlantillaContentType() {
		return plantillaContentType;
	}
	public byte[] getPlantillaContingut() {
		return plantillaContingut;
	}
	public MetaExpedientEntity getMetaExpedient() {
		return metaExpedient;
	}
	public NtiOrigenEnumDto getNtiOrigen() {
		return ntiOrigen;
	}
	public DocumentNtiEstadoElaboracionEnumDto getNtiEstadoElaboracion() {
		return ntiEstadoElaboracion;
	}
	public DocumentNtiTipoDocumentalEnumDto getNtiTipoDocumental() {
		return ntiTipoDocumental;
	}
	public boolean isFirmaBiometricaActiva() {
		return firmaBiometricaActiva;
	}
	public boolean isBiometricaLectura() {
		return biometricaLectura;
	}
	
	public void update(
			String codi,
			String nom,
			String descripcio,
			MultiplicitatEnumDto multiplicitat,
			boolean firmaPortafirmesActiva,
			String portafirmesDocumentTipus,
			String portafirmesFluxId,
			String[] portafirmesResponsables,
			MetaDocumentFirmaFluxTipusEnumDto portafirmesFluxTipus,
			String portafirmesCustodiaTipus,
			boolean firmaPassarelaActiva,
			String firmaPassarelaCustodiaTipus,
			NtiOrigenEnumDto ntiOrigen,
			DocumentNtiEstadoElaboracionEnumDto ntiEstadoElaboracion,
			DocumentNtiTipoDocumentalEnumDto ntiTipoDocumental,
			boolean firmaBiometricaActiva,
			boolean biometricaLectura) {
		update(
				codi,
				nom,
				descripcio);
		this.multiplicitat = multiplicitat;
		this.firmaPortafirmesActiva = firmaPortafirmesActiva;
		this.portafirmesDocumentTipus = portafirmesDocumentTipus;
		this.portafirmesFluxId = portafirmesFluxId;
		this.portafirmesResponsables = getResponsablesFromArray(portafirmesResponsables);
		this.portafirmesFluxTipus = portafirmesFluxTipus;
		this.portafirmesCustodiaTipus = portafirmesCustodiaTipus;
		this.firmaPassarelaActiva = firmaPassarelaActiva;
		this.firmaPassarelaCustodiaTipus = firmaPassarelaCustodiaTipus;
		this.ntiOrigen = ntiOrigen;
		this.ntiEstadoElaboracion = ntiEstadoElaboracion;
		this.ntiTipoDocumental = ntiTipoDocumental;
		this.firmaBiometricaActiva = firmaBiometricaActiva;
		this.biometricaLectura = biometricaLectura;
	}

	public void updatePlantilla(
			String plantillaNom,
			String plantillaContentType,
			byte[] plantillaContingut) {
		this.plantillaNom = plantillaNom;
		this.plantillaContentType = plantillaContentType;
		this.plantillaContingut = plantillaContingut;
	}

	public static Builder getBuilder(
			EntitatEntity entitat,
			String codi,
			String nom,
			MultiplicitatEnumDto multiplicitat,
			MetaExpedientEntity metaExpedient,
			NtiOrigenEnumDto ntiOrigen,
			DocumentNtiEstadoElaboracionEnumDto ntiEstadoElaboracion,
			DocumentNtiTipoDocumentalEnumDto ntiTipoDocumental) {
		return new Builder(
				entitat,
				codi,
				nom,
				multiplicitat,
				metaExpedient,
				ntiOrigen,
				ntiEstadoElaboracion,
				ntiTipoDocumental);
	}
	public static class Builder {
		MetaDocumentEntity built;
		Builder(
				EntitatEntity entitat,
				String codi,
				String nom,
				MultiplicitatEnumDto multiplicitat,
				MetaExpedientEntity metaExpedient,
				NtiOrigenEnumDto ntiOrigen,
				DocumentNtiEstadoElaboracionEnumDto ntiEstadoElaboracion,
				DocumentNtiTipoDocumentalEnumDto ntiTipoDocumental) {
			built = new MetaDocumentEntity();
			built.entitat = entitat;
			built.codi = codi;
			built.nom = nom;
			built.multiplicitat = multiplicitat;
			built.metaExpedient = metaExpedient;
			built.tipus = MetaNodeTipusEnum.DOCUMENT;
			built.firmaPortafirmesActiva = false;
			built.firmaPassarelaActiva = false;
			built.ntiOrigen = ntiOrigen;
			built.ntiEstadoElaboracion = ntiEstadoElaboracion;
			built.ntiTipoDocumental = ntiTipoDocumental;
			built.firmaBiometricaActiva = false;
			built.biometricaLectura = false;
		}
		
		public Builder biometricaLectura(boolean biometricaLectura) {
			built.biometricaLectura = biometricaLectura;
			return this;
		}
		
		public Builder firmaPortafirmesActiva(boolean firmaPortafirmesActiva) {
			built.firmaPortafirmesActiva = firmaPortafirmesActiva;
			return this;
		}
		
		public Builder firmaBiometricaActiva(boolean firmaBiometricaActiva) {
			built.firmaBiometricaActiva = firmaBiometricaActiva;
			return this;
		}
		
		public Builder descripcio(String descripcio) {
			built.descripcio = descripcio;
			return this;
		}
		public Builder portafirmesDocumentTipus(String portafirmesDocumentTipus) {
			built.portafirmesDocumentTipus = portafirmesDocumentTipus;
			return this;
		}
		public Builder portafirmesFluxId(String portafirmesFluxId) {
			built.portafirmesFluxId = portafirmesFluxId;
			return this;
		}
		public Builder portafirmesResponsables(String[] portafirmesResponsables) {
			built.portafirmesResponsables = getResponsablesFromArray(portafirmesResponsables);
			return this;
		}
		public Builder portafirmesFluxTipus(MetaDocumentFirmaFluxTipusEnumDto portafirmesFluxTipus) {
			built.portafirmesFluxTipus = portafirmesFluxTipus;
			return this;
		}
		public Builder portafirmesCustodiaTipus(String portafirmesCustodiaTipus) {
			built.portafirmesCustodiaTipus = portafirmesCustodiaTipus;
			return this;
		}
		public Builder firmaPassarelaActiva(boolean firmaPassarelaActiva) {
			built.firmaPassarelaActiva = firmaPassarelaActiva;
			return this;
		}
		public Builder firmaPassarelaCustodiaTipus(String firmaPassarelaCustodiaTipus) {
			built.firmaPassarelaCustodiaTipus = firmaPassarelaCustodiaTipus;
			return this;
		}
		public MetaDocumentEntity build() {
			return built;
		}
	}

	private static String getResponsablesFromArray(String[] portafirmesResponsables) {
		StringBuilder responsablesStr = new StringBuilder();
		for (String responsable: portafirmesResponsables) {
			if (responsablesStr.length() > 0)
				responsablesStr.append(",");
			responsablesStr.append(responsable);
		}
		return responsablesStr.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((codi == null) ? 0 : codi.hashCode());
		result = prime * result + ((metaExpedient == null) ? 0 : metaExpedient.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		MetaDocumentEntity other = (MetaDocumentEntity) obj;
		if (codi == null) {
			if (other.codi != null)
				return false;
		} else if (!codi.equals(other.codi))
			return false;
		if (metaExpedient == null) {
			if (other.metaExpedient != null)
				return false;
		} else if (!metaExpedient.equals(other.metaExpedient))
			return false;
		return true;
	}

	private static final long serialVersionUID = -2299453443943600172L;

}