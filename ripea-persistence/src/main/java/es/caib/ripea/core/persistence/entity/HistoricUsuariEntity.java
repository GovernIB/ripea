package es.caib.ripea.core.persistence.entity;

import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.historic.HistoricTipusEnumDto;
import lombok.Data;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = BaseConfig.DB_PREFIX + "hist_exp_usuari")
public class HistoricUsuariEntity extends HistoricEntity {

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "usuari_codi")
	@ForeignKey(name = BaseConfig.DB_PREFIX + "hist_exp_usuari_usuari_fk")
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
