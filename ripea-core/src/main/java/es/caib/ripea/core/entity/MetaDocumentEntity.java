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
import javax.persistence.Lob;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.ripea.core.api.dto.MetaDocumentFirmaFluxTipusEnumDto;
import es.caib.ripea.core.api.dto.MultiplicitatEnumDto;

/**
 * Classe del model de dades que representa un meta-document.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "ipa_metadocument")
@EntityListeners(AuditingEntityListener.class)
public class MetaDocumentEntity extends MetaNodeEntity {

	@Column(name = "global_expedient")
	private boolean globalExpedient;
	@Enumerated(EnumType.STRING)
	@Column(name = "global_multiplicitat")
	private MultiplicitatEnumDto globalMultiplicitat;
	@Column(name = "global_readonly")
	private boolean globalReadOnly;
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



	public boolean isGlobalExpedient() {
		return globalExpedient;
	}
	public MultiplicitatEnumDto getGlobalMultiplicitat() {
		return globalMultiplicitat;
	}
	public boolean isGlobalReadOnly() {
		return globalReadOnly;
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

	public void update(
			String codi,
			String nom,
			String descripcio,
			boolean globalExpedient,
			MultiplicitatEnumDto globalMultiplicitat,
			boolean globalReadOnly,
			boolean firmaPortafirmesActiva,
			String portafirmesDocumentTipus,
			String portafirmesFluxId,
			String[] portafirmesResponsables,
			MetaDocumentFirmaFluxTipusEnumDto portafirmesFluxTipus,
			String portafirmesCustodiaTipus,
			boolean firmaPassarelaActiva,
			String firmaPassarelaCustodiaTipus) {
		update(
				codi,
				nom,
				descripcio);
		this.globalExpedient = globalExpedient;
		this.globalMultiplicitat = globalMultiplicitat;
		this.globalReadOnly = globalReadOnly;
		this.firmaPortafirmesActiva = firmaPortafirmesActiva;
		this.portafirmesDocumentTipus = portafirmesDocumentTipus;
		this.portafirmesFluxId = portafirmesFluxId;
		this.portafirmesResponsables = getResponsablesFromArray(portafirmesResponsables);
		this.portafirmesFluxTipus = portafirmesFluxTipus;
		this.portafirmesCustodiaTipus = portafirmesCustodiaTipus;
		this.firmaPassarelaActiva = firmaPassarelaActiva;
		this.firmaPassarelaCustodiaTipus = firmaPassarelaCustodiaTipus;
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
			String nom) {
		return new Builder(
				entitat,
				codi,
				nom);
	}
	public static class Builder {
		MetaDocumentEntity built;
		Builder(
				EntitatEntity entitat,
				String codi,
				String nom) {
			built = new MetaDocumentEntity();
			built.entitat = entitat;
			built.codi = codi;
			built.nom = nom;
			built.tipus = MetaNodeTipusEnum.DOCUMENT;
		}
		public Builder descripcio(String descripcio) {
			built.descripcio = descripcio;
			return this;
		}
		public Builder globalExpedient(boolean globalExpedient) {
			built.globalExpedient = globalExpedient;
			return this;
		}
		public Builder globalMultiplicitat(MultiplicitatEnumDto globalMultiplicitat) {
			built.globalMultiplicitat = globalMultiplicitat;
			return this;
		}
		public Builder globalReadOnly(boolean globalReadOnly) {
			built.globalReadOnly = globalReadOnly;
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

	private static final long serialVersionUID = -2299453443943600172L;

}
