package es.caib.ripea.persistence.entity.resourceentity;

import es.caib.ripea.persistence.base.entity.BaseAuditableEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.OrganEstatEnumDto;
import es.caib.ripea.service.intf.dto.TipusTransicioEnumDto;
import es.caib.ripea.service.intf.model.OrganGestorResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

/**
 * Entitat de base de dades que representa un node.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "organ_gestor")
@Getter
@Setter
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class OrganGestorResourceEntity extends BaseAuditableEntity<OrganGestorResource> {

	@Column(name = "codi", length = 64, nullable = false)
	private String codi;

	@Column(name = "nom", length = 1000)
	private String nom; // nomCatala

	@Column(name = "nom_es", length = 1000)
	private String nomEspanyol;

	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(
			name = "entitat_id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "entitat_organ_gestor_fk"))
	private EntitatResourceEntity entitat;

	@ToString.Exclude
	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "pare_id")
	private OrganGestorResourceEntity pare;

	@Column(name = "actiu")
	private boolean actiu;

	@Column(name = "cif", length = 10)
	private String cif;

	@Column(name = "utilitzar_cif_pinbal")
	private boolean utilitzarCifPinbal;
	@Column(name = "perm_env_postal")
	private boolean permetreEnviamentPostal;
	@Column(name = "perm_env_postal_desc")
	private boolean permetreEnviamentPostalDescendents;

	@Column(name = "estat", length = 1)
	@Enumerated(EnumType.STRING)
	private OrganEstatEnumDto estat;

	@ToString.Exclude
	@OneToMany(
			mappedBy = "organGestor",
			fetch = FetchType.LAZY)
	private List<MetaExpedientResourceEntity> metaExpedients;

	@ToString.Exclude
	@OneToMany(
			mappedBy = "pare",
			fetch = FetchType.LAZY,
			cascade= { CascadeType.PERSIST })
	private List<OrganGestorResourceEntity> fills;

	@JoinTable(name = BaseConfig.DB_PREFIX + "og_sinc_rel",
			joinColumns = { @JoinColumn(name = "antic_og", referencedColumnName = "id", nullable = false) },
			inverseJoinColumns = { @JoinColumn(name = "nou_og", referencedColumnName = "id", nullable = false) })
	@ToString.Exclude
	@ManyToMany
	private List<OrganGestorResourceEntity> nous = new ArrayList<>();

	@ToString.Exclude
	@ManyToMany(mappedBy = "nous")
	private List<OrganGestorResourceEntity> antics = new ArrayList<>();

	@Column(name = "tipus_transicio", length = 12)
	@Enumerated(EnumType.STRING)
	private TipusTransicioEnumDto tipusTransicio;


//	@PreRemove
//	private void preRemove() {
//		if (this.getFills() != null) {
//			for (OrganGestorResourceEntity fill : this.getFills()) {
//				fill.setPare(null);
//			}
//		}
//	}

	@Transient
	private String codiINom;
	public String getCodiINom() {
		return codi + " - " + nom;
	}

}
