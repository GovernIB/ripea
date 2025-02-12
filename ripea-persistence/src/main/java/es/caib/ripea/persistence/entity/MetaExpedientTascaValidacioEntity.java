package es.caib.ripea.persistence.entity;

import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.ItemValidacioTascaEnum;
import es.caib.ripea.service.intf.dto.TipusValidacioTascaEnum;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "METAEXP_TASCA_VALIDACIO")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter
public class MetaExpedientTascaValidacioEntity extends RipeaAuditable<Long> {

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
	@ForeignKey(name = BaseConfig.DB_PREFIX + "METAEXP_TASCA_VALIDACIO_FK")
	private MetaExpedientTascaEntity metaExpedientTasca;
	
	public static Builder getBuilder(
			ItemValidacioTascaEnum itemValidacio,
			TipusValidacioTascaEnum tipusValidacio,
			Long itemId,
			boolean activa) {
		return new Builder(
				itemValidacio,
				tipusValidacio,
				itemId,
				activa);
	}
	public static class Builder {
		MetaExpedientTascaValidacioEntity built;
		Builder(
				ItemValidacioTascaEnum itemValidacio,
				TipusValidacioTascaEnum tipusValidacio,
				Long itemId,
				boolean activa) {
			built = new MetaExpedientTascaValidacioEntity();
			built.itemValidacio = itemValidacio;
			built.tipusValidacio = tipusValidacio;
			built.itemId = itemId;
			built.activa = activa;
		}
		public MetaExpedientTascaValidacioEntity build() {
			return built;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((itemId == null) ? 0 : itemId.hashCode());
		result = prime * result + ((itemValidacio == null) ? 0 : itemValidacio.hashCode());
		result = prime * result + ((tipusValidacio == null) ? 0 : tipusValidacio.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		MetaExpedientTascaValidacioEntity other = (MetaExpedientTascaValidacioEntity) obj;
		if (itemValidacio == null) {
			if (other.itemValidacio != null)
				return false;
		} else if (!itemValidacio.equals(other.itemValidacio))
			return false;
		if (tipusValidacio == null) {
			if (other.tipusValidacio != null)
				return false;
		} else if (!tipusValidacio.equals(other.tipusValidacio))
			return false;
		if (itemId == null) {
			if (other.itemId != null)
				return false;
		} else if (!itemId.equals(other.itemId))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "MetaExpedientTascaValidacioEntity [id="+this.getId()+", itemValidacio=" + itemValidacio + ", tipusValidacio="
				+ tipusValidacio + ", itemId=" + itemId + "]";
	}

	private static final long serialVersionUID = -3447268545755617403L;
}