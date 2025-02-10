package es.caib.ripea.core.persistence.entity;

import es.caib.ripea.service.intf.config.BaseConfig;
import lombok.Getter;
import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.List;

@Getter
@Entity
@Table(name = BaseConfig.DB_PREFIX + "expedient_estat")
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
	@ForeignKey(name = BaseConfig.DB_PREFIX + "metaexp_expedientestat_fk")
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
