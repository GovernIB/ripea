package es.caib.ripea.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.ripea.core.audit.RipeaAuditable;
/**
 * Classe del model de dades que representa un canvi de lloc
 * d'un contenidor.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "ipa_exp_comment")
@Inheritance(strategy=InheritanceType.JOINED)
@EntityListeners(AuditingEntityListener.class)
public class ExpedientComentariEntity extends  RipeaAuditable<Long> {

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "expedient_id")
	protected ExpedientEntity expedient;
	@Column(name = "text", length = 1024)
	protected String text;

	public ExpedientEntity getExpedient() {
		return expedient;
	}
	public String getText() {
		return text;
	}
	
	public static Builder getBuilder(
			ExpedientEntity expedient,
			String text) {
		return new Builder(
				expedient,
				text);
	}
	public static class Builder {
		ExpedientComentariEntity built;
		Builder(
				ExpedientEntity expedient,
				String text) {
			built = new ExpedientComentariEntity();
			built.expedient = expedient;
			built.text = text;
		}
		public ExpedientComentariEntity build() {
			return built;
		}
	}

	private static final long serialVersionUID = -2299453443943600172L;

}

