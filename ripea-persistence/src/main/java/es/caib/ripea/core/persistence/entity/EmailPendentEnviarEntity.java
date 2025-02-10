/**
 * 
 */
package es.caib.ripea.core.persistence.entity;

import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.EventTipusEnumDto;
import lombok.Getter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Getter
@Entity
@Table(name = BaseConfig.DB_PREFIX + "email_pendent_enviar")
@EntityListeners(AuditingEntityListener.class)
public class EmailPendentEnviarEntity extends RipeaAuditable<Long> {

	@Column(name = "remitent", length = 64, nullable = false)
	private String remitent;
	@Column(name = "destinatari", length = 64, nullable = false)
	private String destinatari;
	@Column(name = "subject", length = 1024, nullable = false)
	private String subject;
	@Column(name = "text", length = 4000, nullable = false)
	private String text;
    @Column(name = "adjunt_id")
	private Long adjuntId;

	@Column(name = "event_tipus_enum", length = 64, nullable = false)
	@Enumerated(EnumType.STRING)
	private EventTipusEnumDto eventTipusEnum;

    public static Builder getBuilder(
			String remitent,
			String destinatari,
			String subject,
			String text,
			EventTipusEnumDto eventTipusEnum,
            Long adjuntId) {
		return new Builder(
				remitent,
				destinatari,
				subject,
				text,
				eventTipusEnum,
                adjuntId);
	}

	public static class Builder {

		EmailPendentEnviarEntity built;

		Builder(
				String remitent,
				String destinatari,
				String subject,
				String text,
				EventTipusEnumDto eventTipusEnum,
                Long adjuntId) {
			built = new EmailPendentEnviarEntity();
			built.remitent = remitent;
			built.destinatari = destinatari;
			built.subject = subject;
			built.text = text;
			built.eventTipusEnum = eventTipusEnum;
			built.adjuntId = adjuntId;
		}

		public EmailPendentEnviarEntity build() {
			return built;
		}
	}
	

	private static final long serialVersionUID = -2299453443943600172L;

}
