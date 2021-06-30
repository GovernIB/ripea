package es.caib.ripea.core.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.ripea.core.api.dto.historic.HistoricTipusEnumDto;
import es.caib.ripea.core.audit.RipeaAuditable;
import es.caib.ripea.core.repository.ContingutLogRepository;
import lombok.Getter;
import lombok.Setter;

/**
 * Classe del model de dades que representa un meta-node.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@Getter
@Setter
@Entity
@Table(name = "ipa_historic")
@Inheritance(strategy = InheritanceType.JOINED)
@EntityListeners(AuditingEntityListener.class)
public class HistoricEntity extends RipeaAuditable<Long> {

	@Version
	@Column(name = "version")
	private int version;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "entitat_id")
	@ForeignKey(name = "ipa_historic_entitat_fk")
	protected EntitatEntity entitat;

	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "organ_id")
	@ForeignKey(name = "ipa_historic_organ_fk")
	protected OrganGestorEntity organGestor;

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "metaexpedient_id")
	@ForeignKey(name = "ipa_historic_metaexp_fk")
	protected MetaExpedientEntity metaExpedient;

	@Column(name = "tipus", nullable = false)
	protected HistoricTipusEnumDto tipus;

	@Column(name = "data")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date data;

	@Column(name = "n_exped_creats")
	protected Long numExpedientsCreats;

	@Column(name = "n_exped_creats_acum")
	protected Long numExpedientsCreatsTotal;

	@Column(name = "n_exped_oberts")
	protected Long numExpedientsOberts;

	@Column(name = "n_exped_oberts_acum")
	protected Long numExpedientsObertsTotal;

	@Column(name = "n_exped_tancats")
	protected Long numExpedientsTancats;

	@Column(name = "n_exped_tancats_acum")
	protected Long numExpedientsTancatsTotal;

	@Column(name = "organ_id", insertable = false, updatable=false)
	protected Long organGestorId;
	
	public HistoricEntity() {
	}
	
	public HistoricEntity(Date data, HistoricTipusEnumDto tipus) {
		this.data = data;
		this.tipus = tipus;
		
		this.numExpedientsCreats = 0L;
		this.numExpedientsCreatsTotal = 0L;
		this.numExpedientsOberts = 0L;
		this.numExpedientsObertsTotal = 0L;
		this.numExpedientsTancats = 0L;
		this.numExpedientsTancatsTotal = 0L;
	}

	public void fillDailyHistoric(
			ContingutLogRepository contingutLogRepository,
			MetaExpedientEntity metaExpedient,
			Date date) {
		this.setEntitat(metaExpedient.getEntitat());
		this.setOrganGestor(metaExpedient.getOrganGestor());
		this.setMetaExpedient(metaExpedient);
	}
	
//	public Long getOrganGestorId() {
//		return this.organGestorId == null ? (long)-1 : this.organGestorId;
//	}
//	@PostLoad
//	private void onLoad() {
//	    if (this.organGestor != null ) {
//	    	this.organGestorId = this.organGestor.getId();
//	    } else {
//	    	this.organGestorId = (long)-1;
//	    }
//	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -5718074962313127460L;

}
