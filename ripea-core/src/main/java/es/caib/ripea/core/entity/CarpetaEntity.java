/**
 * 
 */
package es.caib.ripea.core.entity;

import es.caib.ripea.core.api.dto.ContingutTipusEnumDto;
import lombok.Getter;
import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Classe del model de dades que representa una carpeta.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "ipa_carpeta")
@EntityListeners(AuditingEntityListener.class)
@Getter
public class CarpetaEntity extends ContingutEntity {
 
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "expedient_relacionat")
	@ForeignKey(name = "ipa_carpeta_exprel_fk")
	private ExpedientEntity expedientRelacionat;
	
	public void updateNom(
			String nom) {
		this.nom = nom;
	}
	
	public void updateExpedientRelacionat(ExpedientEntity expedientRelacionat) {
		this.expedientRelacionat = expedientRelacionat;
	}
	
	public static Builder getBuilder(
			String nom,
			ContingutEntity pare,
			EntitatEntity entitat,
			ExpedientEntity expedient) {
		return new Builder(
				nom,
				pare,
				entitat,
				expedient);
	}
	public static class Builder {
		CarpetaEntity built;
		Builder(
				String nom,
				ContingutEntity pare,
				EntitatEntity entitat,
				ExpedientEntity expedient) {
			built = new CarpetaEntity();
			built.nom = nom;
			built.pare = pare;
			built.entitat = entitat;
			built.expedient = expedient;
			built.tipus = ContingutTipusEnumDto.CARPETA;
		}
		public CarpetaEntity build() {
			return built;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		CarpetaEntity other = (CarpetaEntity) obj;
		if (nom == null) {
			if (other.nom != null)
				return false;
		} else if (!nom.equals(other.nom))
			return false;
		return true;
	}
	private static final long serialVersionUID = -2299453443943600172L;

}
