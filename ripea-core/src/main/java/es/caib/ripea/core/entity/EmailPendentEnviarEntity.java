/**
 * 
 */
package es.caib.ripea.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.ripea.core.api.dto.EventTipusEnumDto;
import es.caib.ripea.core.audit.RipeaAuditable;


@Entity
@Table(name="ipa_email_pendent_enviar")
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
	
	@Column(name = "event_tipus_enum", length = 64, nullable = false)
	@Enumerated(EnumType.STRING)
	private EventTipusEnumDto eventTipusEnum;
	
	
	public String getRemitent() {
	    return remitent;
	}
	public String getDestinatari() {
	    return destinatari;
	}
	public String getSubject() {
	    return subject;
	}
	public String getText() {
	    return text;
	}
	public EventTipusEnumDto getEventTipusEnum() {
	    return eventTipusEnum;
	}
	public static Builder getBuilder(
			String remitent,
			String destinatari,
			String subject,
			String text,
			EventTipusEnumDto eventTipusEnum) {
		return new Builder(
				remitent,
				destinatari,
				subject,
				text,
				eventTipusEnum);
	}

	public static class Builder {

		EmailPendentEnviarEntity built;

		Builder(
				String remitent,
				String destinatari,
				String subject,
				String text,
				EventTipusEnumDto eventTipusEnum) {
			built = new EmailPendentEnviarEntity();
			built.remitent = remitent;
			built.destinatari = destinatari;
			built.subject = subject;
			built.text = text;
			built.eventTipusEnum = eventTipusEnum;
		}

		public EmailPendentEnviarEntity build() {
			return built;
		}
	}
	

	private static final long serialVersionUID = -2299453443943600172L;

}
