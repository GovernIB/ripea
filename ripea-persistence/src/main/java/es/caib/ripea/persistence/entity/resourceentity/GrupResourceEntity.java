package es.caib.ripea.persistence.entity.resourceentity;

import es.caib.ripea.persistence.base.entity.BaseAuditableEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.model.GrupResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * Entitat de base de dades que representa un node.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "grup")
@Getter
@Setter
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class GrupResourceEntity extends BaseAuditableEntity<GrupResource> {

	@Column(name = "rol", length = 50, nullable = false)
	private String rol;
	@Column(name = "codi", length = 50, nullable = false)
	private String codi;
	@Column(name = "descripcio", length = 512, nullable = false)
	private String descripcio;


	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(
			name = "entitat_id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "entitat_ipa_grup_fk"))
	protected EntitatResourceEntity entitat;


	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "organ_id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "organ_grup_fk"))
	private OrganGestorResourceEntity organGestor;

	@ManyToMany(mappedBy = "grups", fetch = FetchType.EAGER)
	protected List<MetaExpedientResourceEntity> metaExpedients = new ArrayList<>();

}
