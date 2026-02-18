package es.caib.ripea.persistence.entity.resourceentity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.ForeignKey;

import es.caib.ripea.persistence.base.entity.BaseAuditableEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.model.MetaExpedientCarpetaResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "metaexpedient_carpeta")
@Getter
@Setter
@NoArgsConstructor
public class MetaExpedientCarpetaResourceEntity extends BaseAuditableEntity<MetaExpedientCarpetaResource> {

	@Version
	@Column(name = "version")
	private int version;

	@Column(name = "nom", length = 256, nullable = false)
	protected String nom;
	
	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "pare_id")
	@ForeignKey(name = BaseConfig.DB_PREFIX + "metaexpedient_carpeta_pare_fk")
	protected MetaExpedientCarpetaResourceEntity pare;
	
	
	@OneToMany(
			mappedBy = "pare",
			fetch = FetchType.LAZY,
			orphanRemoval = true)
	protected Set<MetaExpedientCarpetaResourceEntity> fills = new HashSet<MetaExpedientCarpetaResourceEntity>();
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "meta_expedient_id")
	@ForeignKey(name = BaseConfig.DB_PREFIX + "metaexp_metaexpcarp_fk")
	private MetaExpedientResourceEntity metaExpedient;
}
