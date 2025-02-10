package es.caib.ripea.core.persistence;

import es.caib.ripea.core.audit.RipeaAuditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

/**
 * Classe del model de dades que representa un comentari en una tasca.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "ipa_exp_tasca_comment")
@Inheritance(strategy=InheritanceType.JOINED)
@EntityListeners(AuditingEntityListener.class)
public class ExpedientTascaComentariEntity extends RipeaAuditable<Long> {

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "exp_tasca_id")
	protected ExpedientTascaEntity expedientTasca;
	@Column(name = "text", length = 1024)
	protected String text;

	public ExpedientTascaEntity getExpedientTasca() {
		return expedientTasca;
	}
	public String getText() {
		return text;
	}
	
	public static Builder getBuilder(
			ExpedientTascaEntity expedientTasca,
			String text) {
		return new Builder(
				expedientTasca,
				text);
	}
	public static class Builder {
		ExpedientTascaComentariEntity built;
		Builder(
				ExpedientTascaEntity expedientTasca,
				String text) {
			built = new ExpedientTascaComentariEntity();
			built.expedientTasca = expedientTasca;
			built.text = text;
		}
		public ExpedientTascaComentariEntity build() {
			return built;
		}
	}

	private static final long serialVersionUID = -8638358693060117666L;

}

