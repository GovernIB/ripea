/**
 * 
 */
package es.caib.ripea.core.persistence;

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

import es.caib.ripea.core.api.dto.ElementTipusEnumDto;
import es.caib.ripea.core.api.dto.ExecucioMassivaEstatDto;
import es.caib.ripea.core.audit.RipeaAuditable;

@Entity
@Table(	name = "ipa_massiva_contingut")
@EntityListeners(AuditingEntityListener.class)
public class ExecucioMassivaContingutEntity extends RipeaAuditable<Long> {
	
	private static final int ERROR_TAMANY = 2046;
	
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "datA_inici")
	private Date dataInici;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_fi")
	private Date dataFi;
	@Column(name = "estat")
	@Enumerated(EnumType.STRING)
	private ExecucioMassivaEstatDto estat;
	@Column(name = "error", length = ERROR_TAMANY)
	private String error;
	@Column(name = "ordre", nullable = false)
	private int ordre;
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "execucio_massiva_id")
	@ForeignKey(name = "ipa_exmas_exmascon_fk")
	private ExecucioMassivaEntity execucioMassiva;
	
	@Column(name = "element_id")
	private Long elementId;
	@Column(name = "element_nom", length = 256)
	private String elementNom;
	@Enumerated(EnumType.STRING)
	@Column(name = "element_tipus", length = 16)
	private ElementTipusEnumDto elementTipus;

	public Date getDataInici() {
		return dataInici;
	}
	public Date getDataFi() {
		return dataFi;
	}
	public ExecucioMassivaEstatDto getEstat() {
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
	public Long getElementId() {
		return elementId;
	}
	public ElementTipusEnumDto getElementTipus() {
		return elementTipus;
	}
	public String getElementNom() {
		return elementNom;
	}

	public void updateError(
			Date ara,
			String error) {
		this.dataInici = ara;
		this.dataFi = ara;
		this.estat = ExecucioMassivaEstatDto.ESTAT_ERROR;
		this.error = error;
	}
	public void updateDataInici(
			Date dataInici) {
		this.dataInici = dataInici;
	}
	public void updateFinalitzat(
			Date dataFi) {
		this.dataFi = dataFi;
		this.estat = ExecucioMassivaEstatDto.ESTAT_FINALITZAT;
	}
	public void updateCancelat(Date dataFi) {
		this.dataFi = dataFi;
		this.estat = ExecucioMassivaEstatDto.ESTAT_CANCELAT;
	}
	public void updateEstatDataFi(
			ExecucioMassivaEstatDto estat,
			Date dataFi) {
		this.estat = estat;
		this.dataFi = dataFi;
	}

	public static Builder getBuilder(
			ExecucioMassivaEntity execucioMassiva,
			Long elementId,
			String elementNom, 
			ElementTipusEnumDto elementTipus, 
			int ordre) {
		return new Builder(
				execucioMassiva,
				elementId,
				elementNom, 
				elementTipus, 
				ordre);
	}
	public static class Builder {
		ExecucioMassivaContingutEntity built;
		Builder(
				ExecucioMassivaEntity execucioMassiva,
				Long elementId,
				String elementNom, 
				ElementTipusEnumDto elementTipus, 
				int ordre) {
			built = new ExecucioMassivaContingutEntity();
			built.execucioMassiva = execucioMassiva;
			built.elementId = elementId;
			built.elementNom = elementNom;
			built.elementTipus = elementTipus;
			built.ordre = ordre;
			built.estat = ExecucioMassivaEstatDto.ESTAT_PENDENT;
		}
		public ExecucioMassivaContingutEntity build() {
			return built;
		}
	}

	private static final long serialVersionUID = 5407126790947037434L;

}
