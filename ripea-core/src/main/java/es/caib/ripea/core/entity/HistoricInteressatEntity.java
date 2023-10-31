package es.caib.ripea.core.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import es.caib.ripea.core.api.dto.historic.HistoricTipusEnumDto;
import lombok.Data;

@Data
@Entity
@Table(name = "ipa_hist_exp_interessat")
public class HistoricInteressatEntity extends HistoricEntity {

//	@ManyToOne(optional = false, fetch = FetchType.EAGER)
//	@JoinColumn(name = "interessat_id")
//	@ForeignKey(name = "ipa_hist_exp_interessat_interessat_fk")
	@Column(name = "interessat_doc_num" )
	private String interessatDocNum;
	
	public HistoricInteressatEntity(Date data, HistoricTipusEnumDto tipus) {
		super(data, tipus);
		// TODO Auto-generated constructor stub
	}
	
	public HistoricInteressatEntity() {
		super();
	}


	/**
	 * 
	 */
	private static final long serialVersionUID = 7867827924949828619L;

	
}