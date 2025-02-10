/**
 * 
 */
package es.caib.ripea.core.persistence;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.ripea.core.api.dto.LogObjecteTipusEnumDto;
import es.caib.ripea.core.api.dto.LogTipusEnumDto;
import es.caib.ripea.core.api.utils.Utils;
import es.caib.ripea.core.audit.RipeaAuditable;
import lombok.Getter;

/**
 * Classe del model de dades que representa el registre d'una acció
 * feta sobre un contingut.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Entity
@Table(name = "ipa_cont_log")
@EntityListeners(AuditingEntityListener.class)
public class ContingutLogEntity extends RipeaAuditable<Long> {

	@Column(name = "tipus", nullable = false)
	private LogTipusEnumDto tipus;
//	@ManyToOne(optional = false, fetch = FetchType.LAZY)
//	@JoinColumn(name = "contingut_id")
//	@ForeignKey(name = "ipa_contingut_contlog_fk")
	@Column(name = "contingut_id")
	protected Long contingutId;
	
	@Column(name = "objecte_id", length = 64)
	private String objecteId;
	@Column(name = "objecte_tipus")
	private LogObjecteTipusEnumDto objecteTipus;
	@Column(name = "objecte_log_tipus")
	private LogTipusEnumDto objecteLogTipus;
	@Column(name = "param1", length = 256)
	private String param1;
	@Column(name = "param2", length = 256)
	private String param2;
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "contmov_id")
	@ForeignKey(name = "ipa_contmov_contlog_fk")
	protected ContingutMovimentEntity contingutMoviment;
	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "pare_id")
	@ForeignKey(name = "ipa_pare_contlog_fk")
	protected ContingutLogEntity pare;

	public void updateParams(
			String param1,
			String param2) {
		this.param1 = param1;
		this.param2 = param2;
	}

	public static Builder getBuilder(
			LogTipusEnumDto tipus,
			Long contingutId) {
		return new Builder(
				tipus,
				contingutId);
	}
	public static class Builder {
		ContingutLogEntity built;
		Builder(
				LogTipusEnumDto tipus,
				Long contingutId) {
			built = new ContingutLogEntity();
			built.tipus = tipus;
			built.contingutId = contingutId;
		}
		public Builder objecte(Persistable<? extends Serializable> objecte) {
			built.objecteId = objecte.getId().toString();
			return this;
		}
		public Builder objecteTipus(LogObjecteTipusEnumDto objecteTipus) {
			built.objecteTipus = objecteTipus;
			return this;
		}
		public Builder objecteLogTipus(LogTipusEnumDto objecteLogTipus) {
			built.objecteLogTipus = objecteLogTipus;
			return this;
		}
		public Builder param1(String param1) {
			built.param1 = Utils.abbreviate(param1, 256);
			return this;
		}
		public Builder param2(String param2) {
			built.param2 = Utils.abbreviate(param2, 256);
			return this;
		}
		public Builder pare(ContingutLogEntity pare) {
			built.pare = pare;
			return this;
		}
		public Builder contingutMoviment(ContingutMovimentEntity contingutMoviment) {
			built.contingutMoviment = contingutMoviment;
			return this;
		}
		public ContingutLogEntity build() {
			return built;
		}
	}

	private static final long serialVersionUID = -2299453443943600172L;

}
