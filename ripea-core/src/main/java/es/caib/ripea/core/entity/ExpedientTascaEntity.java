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

import es.caib.ripea.core.api.dto.TascaEstatEnumDto;
import es.caib.ripea.core.audit.RipeaAuditable;

/**
 * Classe del model de dades que representa una tasca del expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(	name = "ipa_expedient_tasca")
@EntityListeners(AuditingEntityListener.class)
@SuppressWarnings("serial")
public class ExpedientTascaEntity extends RipeaAuditable<Long> {

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "expedient_id")
	@ForeignKey(name = "ipa_exp_exptasc_fk")
	private ExpedientEntity expedient;
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "metaexp_tasca_id")
	@ForeignKey(name = "ipa_metaexptasca_exptasc_fk")
	private MetaExpedientTascaEntity metaExpedientTasca;
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "responsable_codi")
	@ForeignKey(name = "ipa_usuari_exptasc_fk")
	private UsuariEntity responsable;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_inici", nullable = false)
	private Date dataInici;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_fi")
	private Date dataFi;

	@Column(name = "estat", length = 20, nullable = false)
	@Enumerated(EnumType.STRING)
	private TascaEstatEnumDto estat;
	
	@Column(name = "motiu_rebuig", length = 1024)
	@Enumerated(EnumType.STRING)
	private String motiuRebuig;

	@Temporal(TemporalType.DATE)
	@Column(name = "data_limit")
	private Date dataLimit;
	
	public static Builder getBuilder(
			ExpedientEntity expedient,
			MetaExpedientTascaEntity metaExpedientTasca,
			UsuariEntity responsable,
			Date dataLimit) {
		return new Builder(
				expedient,
				metaExpedientTasca,
				responsable,
				dataLimit);
	}
	
	public static class Builder {
		ExpedientTascaEntity built;
		Builder(
				ExpedientEntity expedient,
				MetaExpedientTascaEntity metaExpedientTasca,
				UsuariEntity responsable,
				Date dataLimit) {
			built = new ExpedientTascaEntity();
			built.expedient = expedient;
			built.metaExpedientTasca = metaExpedientTasca;
			built.responsable = responsable;
			built.dataInici = new Date();
			built.estat = TascaEstatEnumDto.PENDENT;
			built.dataLimit = dataLimit;
		}
		public ExpedientTascaEntity build() {
			return built;
		}
	}

	public void updateEstat(TascaEstatEnumDto estat) {
		this.estat = estat;
		if (estat == TascaEstatEnumDto.FINALITZADA) {
			dataFi = new Date();
		}
	}
	public void updateDataFi(Date dataFi) {
		this.dataFi = dataFi;
	}
	
	public void updateRebutjar(String motiuRebuig) {
		this.motiuRebuig = motiuRebuig;
		this.estat = TascaEstatEnumDto.REBUTJADA;
	}
	
	public ExpedientEntity getExpedient() {
		return expedient;
	}

	public MetaExpedientTascaEntity getMetaExpedientTasca() {
		return metaExpedientTasca;
	}

	public UsuariEntity getResponsable() {
		return responsable;
	}

	public Date getDataInici() {
		return dataInici;
	}
	public Date getDataLimit() {
		return dataLimit;
	}
	public Date getDataFi() {
		return dataFi;
	}

	public TascaEstatEnumDto getEstat() {
		return estat;
	}
	
	public String getMotiuRebuig() {
		return motiuRebuig;
	}

}
