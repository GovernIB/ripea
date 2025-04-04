package es.caib.ripea.persistence.entity.resourceentity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import es.caib.ripea.persistence.entity.DocumentEntity;
import es.caib.ripea.persistence.entity.DocumentPortafirmesEntity;
import es.caib.ripea.persistence.entity.ExpedientEntity;
import es.caib.ripea.persistence.entity.DocumentPortafirmesEntity.Builder;
import es.caib.ripea.service.intf.dto.DocumentEnviamentEstatEnumDto;
import es.caib.ripea.service.intf.dto.MetaDocumentFirmaFluxTipusEnumDto;
import es.caib.ripea.service.intf.dto.MetaDocumentFirmaSequenciaTipusEnumDto;
import es.caib.ripea.service.intf.dto.PortafirmesCallbackEstatEnumDto;
import es.caib.ripea.service.intf.dto.PortafirmesPrioritatEnumDto;
import es.caib.ripea.service.intf.model.DocumentPortafirmesResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@DiscriminatorValue("DocumentPortafirmesEntity")
public class DocumentPortafirmesResourceEntity extends DocumentEnviamentResourceEntity<DocumentPortafirmesResource> {

	@Column(name = "pf_prioritat")
	private PortafirmesPrioritatEnumDto prioritat;
	@Temporal(TemporalType.DATE)
	@Column(name = "pf_cad_data")
	private Date caducitatData;
	@Column(name = "pf_doc_tipus", length = 64)
	private String documentTipus;
	@Column(name = "pf_responsables", length = 1024)
	private String responsables;
	@Column(name = "pf_seq_tipus")
	private MetaDocumentFirmaSequenciaTipusEnumDto sequenciaTipus;
	@Column(name = "pf_flux_tipus")
	@Enumerated(EnumType.STRING)
	private MetaDocumentFirmaFluxTipusEnumDto fluxTipus;
	@Column(name = "pf_flux_id", length = 64)
	private String fluxId;
	@Column(name = "pf_portafirmes_id", length = 64, unique = true)
	private String portafirmesId;
	@Column(name = "pf_callback_estat")
	private PortafirmesCallbackEstatEnumDto callbackEstat;
	@Column(name = "pf_motiu_rebuig")
	private String motiuRebuig;
	@Column(name = "pf_avis_firma_parcial")
	private Boolean avisFirmaParcial;
	@Column(name = "pf_firma_parcial")
	private Boolean firmaParcial;
	
	@Transient
	private String name;
	@Transient
	private String administrationId;
	
	public String[] getResponsables() {
		if (responsables != null) {
			return responsables.split(",");
		} else {
			return null;
		}
		
	}

	public void updateEnviat(
			Date enviatData,
			String portafirmesId) {
		super.updateEnviat(enviatData);
		this.portafirmesId = portafirmesId;
	}

	public void updateCallbackEstat(
			PortafirmesCallbackEstatEnumDto callbackEstat) {
		this.callbackEstat = callbackEstat;
	}
	
	public void updateMotiuRebuig(
			String motiuRebuig) {
		this.motiuRebuig = motiuRebuig;
	}

	public void updateName(String name) {
		this.name = name;
	}

	public void updateAdministrationId(String administrationId) {
		this.administrationId = administrationId;
	}
	
	public static Builder getBuilder(
			DocumentEnviamentEstatEnumDto estat,
			String assumpte,
			PortafirmesPrioritatEnumDto prioritat,
			Date caducitatData,
			String documentTipus,
			String[] responsables,
			MetaDocumentFirmaSequenciaTipusEnumDto sequenciaTipus,
			MetaDocumentFirmaFluxTipusEnumDto fluxTipus,
			String fluxId,
			ExpedientResourceEntity expedient,
			DocumentResourceEntity document,
			Boolean avisFirmaParcial,
			Boolean firmaParcial) {
		return new Builder(
				estat,
				assumpte,
				prioritat,
				caducitatData,
				documentTipus,
				responsables,
				sequenciaTipus,
				fluxTipus,
				fluxId,
				expedient,
				document,
				avisFirmaParcial,
				firmaParcial);
	}

	public static class Builder {
		DocumentPortafirmesResourceEntity built;
		Builder(
				DocumentEnviamentEstatEnumDto estat,
				String assumpte,
				PortafirmesPrioritatEnumDto prioritat,
				Date caducitatData,
				String documentTipus,
				String[] responsables,
				MetaDocumentFirmaSequenciaTipusEnumDto sequenciaTipus,
				MetaDocumentFirmaFluxTipusEnumDto fluxTipus,
				String fluxId,
				ExpedientResourceEntity expedient,
				DocumentResourceEntity document,
				Boolean avisFirmaParcial,
				Boolean firmaParcial) {
			built = new DocumentPortafirmesResourceEntity();
			built.inicialitzar();
			built.estat = estat;
			built.assumpte = assumpte;
			built.prioritat = prioritat;
			built.caducitatData = caducitatData;
			built.documentTipus = documentTipus;
			built.responsables = getResponsablesFromArray(responsables);
			built.sequenciaTipus = sequenciaTipus;
			built.fluxTipus = fluxTipus;
			built.fluxId = fluxId;
			built.expedient = expedient;
			built.document = document;
			built.avisFirmaParcial = avisFirmaParcial;
			built.firmaParcial = firmaParcial;
		}
		public Builder observacions(String observacions) {
			built.observacions = observacions;
			return this;
		}
		public DocumentPortafirmesResourceEntity build() {
			return built;
		}
	}
	
	private static String getResponsablesFromArray(String[] responsables) {
		StringBuilder responsablesStr = new StringBuilder();
		if (responsables != null) {
			for (String responsable: responsables) {
				if (responsablesStr.length() > 0)
					responsablesStr.append(",");
				responsablesStr.append(responsable);
			}
		}
		return responsablesStr.toString();
	}
}
