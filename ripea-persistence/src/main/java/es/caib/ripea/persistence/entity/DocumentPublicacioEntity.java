/**
 * 
 */
package es.caib.ripea.persistence.entity;

import es.caib.ripea.service.intf.dto.DocumentEnviamentEstatEnumDto;
import es.caib.ripea.service.intf.dto.DocumentPublicacioTipusEnumDto;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import java.util.Date;

/**
 * Classe del model de dades que representa una publicació d'un document
 * d'un expedient a una butlletí oficial.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
public class DocumentPublicacioEntity extends DocumentEnviamentEntity {

	@Column(name = "pub_tipus")
	private DocumentPublicacioTipusEnumDto tipus;

	public DocumentPublicacioTipusEnumDto getTipus() {
		return tipus;
	}

	public void update(
			String assumpte,
			String observacions,
			DocumentPublicacioTipusEnumDto tipus,
			Date enviatData,
			Date processatData) {
		this.assumpte = assumpte;
		this.observacions = observacions;
		this.tipus = tipus;
		this.enviatData = enviatData;
		this.processatData = processatData;
	}

	public static Builder getBuilder(
			DocumentEnviamentEstatEnumDto estat,
			String assumpte,
			DocumentPublicacioTipusEnumDto tipus,
			ExpedientEntity expedient,
			DocumentEntity document) {
		return new Builder(
				estat,
				assumpte,
				tipus,
				expedient,
				document);
	}

	public static class Builder {
		DocumentPublicacioEntity built;
		Builder(
				DocumentEnviamentEstatEnumDto estat,
				String assumpte,
				DocumentPublicacioTipusEnumDto tipus,
				ExpedientEntity expedient,
				DocumentEntity document) {
			built = new DocumentPublicacioEntity();
			built.inicialitzar();
			built.estat = estat;
			built.assumpte = assumpte;
			built.tipus = tipus;
			built.expedient = expedient;
			built.document = document;
		}
		public Builder observacions(String observacions) {
			built.observacions = observacions;
			return this;
		}
		public Builder enviatData(Date enviatData) {
			built.enviatData = enviatData;
			return this;
		}
		public Builder processatData(Date processatData) {
			built.processatData = processatData;
			return this;
		}
		public DocumentPublicacioEntity build() {
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
		result = prime * result + ((tipus == null) ? 0 : tipus.hashCode());
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
		DocumentPublicacioEntity other = (DocumentPublicacioEntity) obj;
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
		if (tipus != other.tipus)
			return false;
		return true;
	}

	private static final long serialVersionUID = -2299453443943600172L;

}
