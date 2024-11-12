package es.caib.ripea.core.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.ripea.core.audit.RipeaAuditable;
import lombok.Getter;

@Getter
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
	@OneToMany(
			mappedBy = "estatAdditional",
			cascade = CascadeType.ALL,
			fetch = FetchType.LAZY,
			orphanRemoval = true)
    private List<ExpedientEntity> expedients;
	
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
	
	

	public void setResponsableCodi(String responsableCodi) {
		this.responsableCodi = responsableCodi;
	}

	public void updateOrdre(
			int ordre) {
		this.ordre = ordre;
	}

	private static final long serialVersionUID = 2049469376271209018L;

}
