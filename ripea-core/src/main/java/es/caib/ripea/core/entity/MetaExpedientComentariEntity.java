package es.caib.ripea.core.persistence;

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

@Entity
@Table(name = "ipa_metaexp_comment")
@Inheritance(strategy=InheritanceType.JOINED)
@EntityListeners(AuditingEntityListener.class)
public class MetaExpedientComentariEntity extends RipeaAuditable<Long> {


	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "meta_expedient_id")
	protected MetaExpedientEntity metaExpedient;
	@Column(name = "text", length = 1024)
	protected String text;
	
	@Column(name = "email_enviat", nullable = false)
	private boolean emailEnviat;

	
	public static Builder getBuilder(
			MetaExpedientEntity metaExpedient,
			String text) {
		return new Builder(
				metaExpedient,
				text);
	}
	public static class Builder {
		MetaExpedientComentariEntity built;
		Builder(
				MetaExpedientEntity metaExpedient,
				String text) {
			built = new MetaExpedientComentariEntity();
			built.metaExpedient = metaExpedient;
			built.text = text;
			built.emailEnviat = false;
		}
		public MetaExpedientComentariEntity build() {
			return built;
		}
	}
	
	public MetaExpedientEntity getMetaExpedient() {
		return metaExpedient;
	}
	public String getText() {
		return text;
	}
	
	public void updateEmailEnviat(boolean emailEnviat) {
		this.emailEnviat = emailEnviat;
	}



	private static final long serialVersionUID = 1L;

}

