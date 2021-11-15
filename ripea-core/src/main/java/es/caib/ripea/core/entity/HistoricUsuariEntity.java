package es.caib.ripea.core.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;

import es.caib.ripea.core.api.dto.historic.HistoricTipusEnumDto;
import lombok.Data;

@Data
@Entity
@Table(name = "ipa_hist_exp_usuari")
public class HistoricUsuariEntity extends HistoricEntity {

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "usuari_codi")
	@ForeignKey(name = "ipa_hist_exp_usuari_usuari_fk")
	private UsuariEntity usuari;
	
	@Column(name = "n_tasques_tramitades")
	private Long numTasquesTramitades;
	
	
	
	public HistoricUsuariEntity(Date data, HistoricTipusEnumDto tipus) {
		super(data, tipus);
		this.numTasquesTramitades = 0L;
	}
	
	public HistoricUsuariEntity() {
		super();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 9019178673766829440L;

}
