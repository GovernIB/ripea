/**
 * 
 */
package es.caib.ripea.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.ripea.core.audit.RipeaAuditable;

/**
 * Classe del model de dades que representa un expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "ipa_expedient_estat")
@EntityListeners(AuditingEntityListener.class)
public class ExpedientEstatEntity extends RipeaAuditable<Long>{
	

	@Column(name = "codi", length = 256, nullable = false)
	private String codi;
	@Column(name = "nom", length = 256, nullable = false)
	private String nom;
	@Column(name = "ordre", nullable = false)
	private int ordre;
	@Column(name = "color", length = 256)
	private String color;
	@Column(name = "inicial")
	private boolean inicial;
	
	@Column(name = "responsable_codi")
	private String responsableCodi;
	
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "metaexpedient_id")
	@ForeignKey(name = "ipa_metaexp_expedientestat_fk")
	private MetaExpedientEntity metaExpedient;
	
	
	public void updateInicial(
			Boolean inicial) {
		this.inicial = inicial;
	}
	
	public void update(
			String codi,
			String nom,
			String color,
			MetaExpedientEntity metaExpedient,
			String responsableCodi) {
		this.codi = codi;
		this.nom = nom;
		this.color = color;
		this.metaExpedient = metaExpedient;
		this.responsableCodi = responsableCodi;
	}

	public static Builder getBuilder(
			String codi,
			String nom,
			int ordre,
			String color,
			MetaExpedientEntity metaExpedient,
			String responsableCodi) {
		return new Builder(
				codi,
				nom,
				ordre,
				color,
				metaExpedient,
				responsableCodi);
	}
	
	public static class Builder {
		ExpedientEstatEntity built;
		Builder(
				String codi,
				String nom,
				int ordre,
				String color,
				MetaExpedientEntity metaExpedient,
				String responsableCodi) {
			built = new ExpedientEstatEntity();
			built.codi = codi;
			built.nom = nom;
			built.ordre = ordre;
			built.color = color;
			built.metaExpedient = metaExpedient;
			built.responsableCodi = responsableCodi;
		}

		public ExpedientEstatEntity build() {
			return built;
		}
	}
	
	
	public String getResponsableCodi() {
		return responsableCodi;
	}

	public void setResponsableCodi(String responsableCodi) {
		this.responsableCodi = responsableCodi;
	}

	public boolean isInicial() {
		return inicial;
	}

	public void updateOrdre(
			int ordre) {
		this.ordre = ordre;
	}

	public String getCodi() {
		return codi;
	}

	public String getNom() {
		return nom;
	}

	public int getOrdre() {
		return ordre;
	}

	public String getColor() {
		return color;
	}

	public MetaExpedientEntity getMetaExpedient() {
		return metaExpedient;
	}

	private static final long serialVersionUID = 2049469376271209018L;

}