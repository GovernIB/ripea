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
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.ripea.core.api.dto.DocumentNtiEstadoElaboracionEnumDto;
import es.caib.ripea.core.api.dto.MetaDocumentFirmaFluxTipusEnumDto;
import es.caib.ripea.core.api.dto.MetaDocumentFirmaSequenciaTipusEnumDto;
import es.caib.ripea.core.api.dto.MetaDocumentPinbalServeiEnumDto;
import es.caib.ripea.core.api.dto.MetaDocumentTipusGenericEnumDto;
import es.caib.ripea.core.api.dto.MultiplicitatEnumDto;
import es.caib.ripea.core.api.dto.NtiOrigenEnumDto;

/**
 * Classe del model de dades que representa un meta-document.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(
		name = "ipa_metadocument",
		uniqueConstraints = {
				@UniqueConstraint(name = "ipa_metadoc_metaexp_codi_uk", columnNames = { "meta_expedient_id", "codi" })
		}
)
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
	@Column(name = "portafirmes_seqtip", length = 256)
	private MetaDocumentFirmaSequenciaTipusEnumDto portafirmesSequenciaTipus;
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
	//@Lob
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "plantilla_contingut")
	private byte[] plantillaContingut;
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "meta_expedient_id")
	@ForeignKey(name = "ipa_metaexp_metadoc_fk")
	private MetaExpedientEntity metaExpedient;
	
	@Column(name = "ordre")
	private int ordre;
	@Column(name = "nti_origen", length = 2)
	@Enumerated(EnumType.STRING)
	private NtiOrigenEnumDto ntiOrigen;
	@Column(name = "nti_estela", length = 4)
	@Enumerated(EnumType.STRING)
	private DocumentNtiEstadoElaboracionEnumDto ntiEstadoElaboracion;
	
	
	@Column(name = "nti_tipdoc", length = 4)
	private String ntiTipoDocumental;
	
//	@ManyToOne
//	@JoinColumn(name = "nti_tipusdoc")
//	@ForeignKey(name = "ipa_tipdoc_metadoc_fk")
//	private TipusDocumentalEntity ntiTipusDocumental;
	
	
	@Column(name = "firma_biometrica")
	private boolean firmaBiometricaActiva;
	@Column(name = "biometrica_lectura")
	private boolean biometricaLectura;
	
	@Column(name = "meta_document_tipus_gen", length = 256)
	@Enumerated(EnumType.STRING)
	private MetaDocumentTipusGenericEnumDto metaDocumentTipusGeneric;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "portafirmes_fluxtip", length = 256)
	private MetaDocumentFirmaFluxTipusEnumDto portafirmesFluxTipus;

	@Column(name = "codi", length = 64, nullable = false)
	private String codiPropi;

	@Column(name = "pinbal_actiu", nullable = false)
	private boolean pinbalActiu;
	@Column(name = "pinbal_servei", length = 64)
	private MetaDocumentPinbalServeiEnumDto pinbalServei;
	@Column(name = "pinbal_finalitat", length = 512)
	protected String pinbalFinalitat;
	
	@Column(name = "pinbal_utilitzar_cif_organ", nullable = false)
	private boolean pinbalUtilitzarCifOrgan;
	
	
	@Column(name = "per_defecte")
	private boolean perDefecte;
	
	@Transient
	private boolean leftPerCreacio;
	

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
	public MetaDocumentFirmaSequenciaTipusEnumDto getPortafirmesSequenciaTipus() {
		return portafirmesSequenciaTipus;
	}
	public MetaDocumentTipusGenericEnumDto getMetaDocumentTipusGeneric() {
		return metaDocumentTipusGeneric;
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
	public String getNtiTipoDocumental() {
		return ntiTipoDocumental;
	}
	public boolean isFirmaBiometricaActiva() {
		return firmaBiometricaActiva;
	}
	public boolean isBiometricaLectura() {
		return biometricaLectura;
	}
	public MetaDocumentFirmaFluxTipusEnumDto getPortafirmesFluxTipus() {
		return portafirmesFluxTipus;
	}
	public boolean isPinbalActiu() {
		return pinbalActiu;
	}
	public MetaDocumentPinbalServeiEnumDto getPinbalServei() {
		return pinbalServei;
	}
	public String getPinbalFinalitat() {
		return pinbalFinalitat;
	}
	public boolean isPerDefecte() {
		return perDefecte;
	}
	public int getOrdre() {
		return ordre;
	}
	public boolean isLeftPerCreacio() {
		return leftPerCreacio;
	}
	public void setLeftPerCreacio(boolean leftPerCreacio) {
		this.leftPerCreacio = leftPerCreacio;
	}
	
	public boolean isPinbalUtilitzarCifOrgan() {
		return pinbalUtilitzarCifOrgan;
	}

	
	public void updateOrdre(int ordre) {
		this.ordre = ordre;
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
			MetaDocumentFirmaSequenciaTipusEnumDto portafirmesSequenciaTipus,
			String portafirmesCustodiaTipus,
			boolean firmaPassarelaActiva,
			String firmaPassarelaCustodiaTipus,
			NtiOrigenEnumDto ntiOrigen,
			DocumentNtiEstadoElaboracionEnumDto ntiEstadoElaboracion,
			String ntiTipoDocumental,
			boolean firmaBiometricaActiva,
			boolean biometricaLectura,
			MetaDocumentFirmaFluxTipusEnumDto portafirmesFluxTipus,
			boolean pinbalActiu,
			MetaDocumentPinbalServeiEnumDto pinbalServei,
			String pinbalFinalitat,
			boolean pinbalUtilitzarCifOrgan) {
		update(
				codi,
				nom,
				descripcio);
		this.multiplicitat = multiplicitat;
		this.firmaPortafirmesActiva = firmaPortafirmesActiva;
		this.portafirmesDocumentTipus = portafirmesDocumentTipus;
		this.portafirmesFluxId = portafirmesFluxId;
		this.portafirmesResponsables = getResponsablesFromArray(portafirmesResponsables);
		this.portafirmesSequenciaTipus = portafirmesSequenciaTipus;
		this.portafirmesCustodiaTipus = portafirmesCustodiaTipus;
		this.firmaPassarelaActiva = firmaPassarelaActiva;
		this.firmaPassarelaCustodiaTipus = firmaPassarelaCustodiaTipus;
		this.ntiOrigen = ntiOrigen;
		this.ntiEstadoElaboracion = ntiEstadoElaboracion;
		this.ntiTipoDocumental = ntiTipoDocumental;
		this.firmaBiometricaActiva = firmaBiometricaActiva;
		this.biometricaLectura = biometricaLectura;
		this.portafirmesFluxTipus = portafirmesFluxTipus;
		this.codiPropi = codi;
		this.pinbalActiu = pinbalActiu;
		this.pinbalServei = pinbalServei;
		this.pinbalFinalitat = pinbalFinalitat;
		this.pinbalUtilitzarCifOrgan = pinbalUtilitzarCifOrgan;
		
	}

	public void updatePlantilla(
			String plantillaNom,
			String plantillaContentType,
			byte[] plantillaContingut) {
		this.plantillaNom = plantillaNom;
		this.plantillaContentType = plantillaContentType;
		this.plantillaContingut = plantillaContingut;
	}

	public void updatePerDefecte(boolean perDefecte) {
		this.perDefecte = perDefecte;
	}

	public static Builder getBuilder(
			EntitatEntity entitat,
			String codi,
			String nom,
			MultiplicitatEnumDto multiplicitat,
			MetaExpedientEntity metaExpedient,
			NtiOrigenEnumDto ntiOrigen,
			DocumentNtiEstadoElaboracionEnumDto ntiEstadoElaboracion,
			String ntiTipoDocumental,
			boolean pinbalActiu,
			String pinbalFinalitat,
			int ordre) {
		return new Builder(
				entitat,
				codi,
				nom,
				multiplicitat,
				metaExpedient,
				ntiOrigen,
				ntiEstadoElaboracion,
				ntiTipoDocumental,
				pinbalActiu,
				pinbalFinalitat,
				ordre);
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
				String ntiTipoDocumental,
				boolean pinbalActiu,
				String pinbalFinalitat,
				int ordre) {
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
			built.codiPropi = codi;
			built.pinbalActiu = pinbalActiu;
			built.pinbalFinalitat = pinbalFinalitat;
			built.ordre = ordre;
			
			
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
		public Builder portafirmesSequenciaTipus(MetaDocumentFirmaSequenciaTipusEnumDto portafirmesSequenciaTipus) {
			built.portafirmesSequenciaTipus = portafirmesSequenciaTipus;
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
		public Builder portafirmesFluxTipus(MetaDocumentFirmaFluxTipusEnumDto portafirmesFluxTipus) {
			built.portafirmesFluxTipus = portafirmesFluxTipus;
			return this;
		}
		public Builder pinbalServei(MetaDocumentPinbalServeiEnumDto pinbalServei) {
			built.pinbalServei = pinbalServei;
			return this;
		}
		public MetaDocumentEntity build() {
			return built;
		}
	}

	private static String getResponsablesFromArray(String[] portafirmesResponsables) {
		StringBuilder responsablesStr = new StringBuilder();
		if (portafirmesResponsables != null) {
			for (String responsable: portafirmesResponsables) {
				if (responsablesStr.length() > 0)
					responsablesStr.append(",");
				responsablesStr.append(responsable);
			}
			return responsablesStr.toString();
		} else {
			return null;
		}

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

	@Override
	public String toString() {
		return "Metadocument: [" +
				"metanode: " + super.toString() + ", " +
				"id: " + this.getId() + ", " +
				"codi: " + this.codi + ", " +
				"nom: " + this.nom + ", " +
				"ntiTipoDocumental: " + this.ntiTipoDocumental + "]";
//		return ToStringBuilder. reflectionToString(this);
	}
	private static final long serialVersionUID = -2299453443943600172L;

}