/**
 * 
 */
package es.caib.ripea.core.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.ripea.core.audit.RipeaAuditable;

@Entity
@Table(	name = "ipa_massiva_contingut")
@EntityListeners(AuditingEntityListener.class)
public class ExecucioMassivaContingutEntity extends RipeaAuditable<Long> {
	
	private static final int ERROR_TAMANY = 2046;
	
	public enum ExecucioMassivaEstat {
		ESTAT_FINALITZAT,
		ESTAT_ERROR,
		ESTAT_PENDENT,
		ESTAT_CANCELAT
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "datA_inici")
	private Date dataInici;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_fi")
	private Date dataFi;
	@Column(name = "estat")
	@Enumerated(EnumType.STRING)
	private ExecucioMassivaEstat estat;
	@Column(name = "error", length = ERROR_TAMANY)
	private String error;
	@Column(name = "ordre", nullable = false)
	private int ordre;
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "execucio_massiva_id")
	@ForeignKey(name = "ipa_exmas_exmascon_fk")
	private ExecucioMassivaEntity execucioMassiva;
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name="contingut_id")
	@ForeignKey(name="hel_cont_exmascon_fk")
	private ContingutEntity contingut;

	public Date getDataInici() {
		return dataInici;
	}
	public Date getDataFi() {
		return dataFi;
	}
	public ExecucioMassivaEstat getEstat() {
		return estat;
	}
	public String getError() {
		return error;
	}
	public int getOrdre() {
		return ordre;
	}
	public ExecucioMassivaEntity getExecucioMassiva() {
		return execucioMassiva;
	}
	public ContingutEntity getContingut() {
		return contingut;
	}

	public void updateError(
			Date ara,
			String error) {
		this.dataInici = ara;
		this.dataFi = ara;
		this.estat = ExecucioMassivaEstat.ESTAT_ERROR;
		this.error = error;
	}
	public void updateDataInici(
			Date dataInici) {
		this.dataInici = dataInici;
	}
	public void updateFinalitzat(
			Date dataFi) {
		this.dataFi = dataFi;
		this.estat = ExecucioMassivaEstat.ESTAT_FINALITZAT;
	}

	public static Builder getBuilder(
			ExecucioMassivaEntity execucioMassiva,
			ContingutEntity contingut,
			int ordre) {
		return new Builder(
				execucioMassiva,
				contingut,
				ordre);
	}
	public static class Builder {
		ExecucioMassivaContingutEntity built;
		Builder(ExecucioMassivaEntity execucioMassiva,
				ContingutEntity contingut,
				int ordre) {
			built = new ExecucioMassivaContingutEntity();
			built.execucioMassiva = execucioMassiva;
			built.contingut = contingut;
			built.ordre = ordre;
			built.estat = ExecucioMassivaEstat.ESTAT_PENDENT;
		}
		public ExecucioMassivaContingutEntity build() {
			return built;
		}
	}

	private static final long serialVersionUID = 5407126790947037434L;

}
