/**
 * 
 */
package es.caib.ripea.core.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.ripea.core.api.dto.DocumentEnviamentEstatEnumDto;
import es.caib.ripea.core.api.dto.MetaDocumentFirmaSequenciaTipusEnumDto;
import es.caib.ripea.core.api.dto.PortafirmesCallbackEstatEnumDto;
import es.caib.ripea.core.api.dto.PortafirmesPrioritatEnumDto;

/**
 * Classe del model de dades que representa un enviament d'una versió
 * d'un document al portafirmes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
public class DocumentPortafirmesEntity extends DocumentEnviamentEntity {

	@Column(name = "pf_prioritat")
	private PortafirmesPrioritatEnumDto prioritat;
	@Temporal(TemporalType.DATE)
	@Column(name = "pf_cad_data")
	private Date caducitatData;
	@Column(name = "pf_doc_tipus", length = 64)
	private String documentTipus;
	@Column(name = "pf_responsables", length = 1024)
	private String responsables;
	@Column(name = "pf_flux_tipus")
	private MetaDocumentFirmaSequenciaTipusEnumDto fluxTipus;
	@Column(name = "pf_flux_id", length = 64)
	private String fluxId;
	@Column(name = "pf_portafirmes_id", length = 64, unique = true)
	private String portafirmesId;
	@Column(name = "pf_callback_estat")
	private PortafirmesCallbackEstatEnumDto callbackEstat;

	public PortafirmesPrioritatEnumDto getPrioritat() {
		return prioritat;
	}
	public Date getCaducitatData() {
		return caducitatData;
	}
	public String getDocumentTipus() {
		return documentTipus;
	}
	public String[] getResponsables() {
		return responsables.split(",");
	}
	public MetaDocumentFirmaSequenciaTipusEnumDto getFluxTipus() {
		return fluxTipus;
	}
	public String getFluxId() {
		return fluxId;
	}
	public String getPortafirmesId() {
		return portafirmesId;
	}
	public PortafirmesCallbackEstatEnumDto getCallbackEstat() {
		return callbackEstat;
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

	public static Builder getBuilder(
			DocumentEnviamentEstatEnumDto estat,
			String assumpte,
			PortafirmesPrioritatEnumDto prioritat,
			Date caducitatData,
			String documentTipus,
			String[] responsables,
			MetaDocumentFirmaSequenciaTipusEnumDto fluxTipus,
			String fluxId,
			ExpedientEntity expedient,
			DocumentEntity document) {
		return new Builder(
				estat,
				assumpte,
				prioritat,
				caducitatData,
				documentTipus,
				responsables,
				fluxTipus,
				fluxId,
				expedient,
				document);
	}

	public static class Builder {
		DocumentPortafirmesEntity built;
		Builder(
				DocumentEnviamentEstatEnumDto estat,
				String assumpte,
				PortafirmesPrioritatEnumDto prioritat,
				Date caducitatData,
				String documentTipus,
				String[] responsables,
				MetaDocumentFirmaSequenciaTipusEnumDto fluxTipus,
				String fluxId,
				ExpedientEntity expedient,
				DocumentEntity document) {
			built = new DocumentPortafirmesEntity();
			built.inicialitzar();
			built.estat = estat;
			built.assumpte = assumpte;
			built.prioritat = prioritat;
			built.caducitatData = caducitatData;
			built.documentTipus = documentTipus;
			built.responsables = getResponsablesFromArray(responsables);
			built.fluxTipus = fluxTipus;
			built.fluxId = fluxId;
			built.expedient = expedient;
			built.document = document;
		}
		public Builder observacions(String observacions) {
			built.observacions = observacions;
			return this;
		}
		public DocumentPortafirmesEntity build() {
			return built;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((expedient == null) ? 0 : expedient.hashCode());
		result = prime * result + ((document == null) ? 0 : document.hashCode());
		result = prime * result + ((enviatData == null) ? 0 : enviatData.hashCode());
		result = prime * result + ((processatData == null) ? 0 : processatData.hashCode());
		result = prime * result + ((caducitatData == null) ? 0 : caducitatData.hashCode());
		result = prime * result + ((documentTipus == null) ? 0 : documentTipus.hashCode());
		result = prime * result + ((fluxId == null) ? 0 : fluxId.hashCode());
		result = prime * result + ((fluxTipus == null) ? 0 : fluxTipus.hashCode());
		result = prime * result + ((prioritat == null) ? 0 : prioritat.hashCode());
		result = prime * result + ((responsables == null) ? 0 : responsables.hashCode());
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
		DocumentPortafirmesEntity other = (DocumentPortafirmesEntity) obj;
		if (expedient == null) {
			if (other.expedient != null)
				return false;
		} else if (!expedient.equals(other.expedient))
			return false;
		if (document == null) {
			if (other.document != null)
				return false;
		} else if (!document.equals(other.document))
			return false;
		if (enviatData == null) {
			if (other.enviatData != null)
				return false;
		} else if (!enviatData.equals(other.enviatData))
			return false;
		if (processatData == null) {
			if (other.processatData != null)
				return false;
		} else if (!processatData.equals(other.processatData))
			return false;
		if (caducitatData == null) {
			if (other.caducitatData != null)
				return false;
		} else if (!caducitatData.equals(other.caducitatData))
			return false;
		if (documentTipus == null) {
			if (other.documentTipus != null)
				return false;
		} else if (!documentTipus.equals(other.documentTipus))
			return false;
		if (fluxId == null) {
			if (other.fluxId != null)
				return false;
		} else if (!fluxId.equals(other.fluxId))
			return false;
		if (fluxTipus != other.fluxTipus)
			return false;
		if (prioritat != other.prioritat)
			return false;
		if (responsables == null) {
			if (other.responsables != null)
				return false;
		} else if (!responsables.equals(other.responsables))
			return false;
		return true;
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

	private static final long serialVersionUID = -2299453443943600172L;

}
