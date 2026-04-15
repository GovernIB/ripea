package es.caib.ripea.persistence.entity.resourceentity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import es.caib.ripea.persistence.base.entity.BaseAuditableEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.ItemValidacioTascaEnum;
import es.caib.ripea.service.intf.dto.TipusValidacioTascaEnum;
import es.caib.ripea.service.intf.model.MetaExpedientTascaValidacioResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "METAEXP_TASCA_VALIDACIO")
@Getter
@Setter
@NoArgsConstructor
public class MetaExpedientTascaValidacioResourceEntity extends BaseAuditableEntity<MetaExpedientTascaValidacioResource> {

	@Column(name = "ITEM_TIPUS", length = 8, nullable = false)
	@Enumerated(EnumType.STRING)
    private ItemValidacioTascaEnum itemValidacio;
	
	@Column(name = "VALIDACIO_TIPUS", length = 8, nullable = false)
	@Enumerated(EnumType.STRING)
    private TipusValidacioTascaEnum tipusValidacio;

	@Column(name = "ITEM_ID")
	protected Long itemId;
	
	@Column(name = "ACTIVA")
	protected boolean activa = true;
	
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "TASCA_ID")
	private MetaExpedientTascaResourceEntity metaExpedientTasca;
}