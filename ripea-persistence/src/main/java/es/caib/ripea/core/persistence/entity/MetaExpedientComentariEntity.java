package es.caib.ripea.core.persistence.entity;

import es.caib.ripea.service.intf.config.BaseConfig;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "metaexp_comment")
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

