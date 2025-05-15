package es.caib.ripea.persistence.entity.resourceentity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.CrearReglaDistribucioEstatEnumDto;
import es.caib.ripea.service.intf.dto.MetaExpedientRevisioEstatEnumDto;
import es.caib.ripea.service.intf.dto.TipusClassificacioEnumDto;
import es.caib.ripea.service.intf.model.MetaExpedientResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "metaexpedient")
@Getter
@Setter
@NoArgsConstructor
@Inheritance(strategy=InheritanceType.JOINED)
public class MetaExpedientResourceEntity extends MetaNodeResourceEntity<MetaExpedientResource> {

	@Column(name = "tipus_classificacio", length = 3, nullable = false)
	@Enumerated(EnumType.STRING)
	private TipusClassificacioEnumDto tipusClassificacio;
	@Column(name = "clasif_sia", length = 30, nullable = false)
	private String classificacio;
	@Column(name = "serie_doc", length = 30, nullable = false)
	private String serieDocumental;
	@Column(name = "expressio_numero", length = 100)
	private String expressioNumero;
	@Column(name = "not_activa", nullable = false)
	private boolean notificacioActiva;

	@Column(name = "PERMET_METADOCS_GENERALS", nullable = false)
	private boolean permetMetadocsGenerals;
    @Column(name = "PERMIS_DIRECTE")
    private boolean permisDirecte = false;

	@OneToMany(mappedBy = "metaExpedient", cascade = { CascadeType.ALL })
	protected Set<ExpedientEstatResourceEntity> estats;

	@Column(name = "codi", length = 64, nullable = false)
	private String codiPropi;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "organ_gestor_id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "organ_gestor_metaexp_fk"))
	private OrganGestorResourceEntity organGestor;

	@Column(name = "gestio_amb_grups_activa", nullable = false)
	private boolean gestioAmbGrupsActiva;

	@OneToMany(mappedBy = "metaExpedient", cascade = { CascadeType.ALL })
	private Set<MetaExpedientOrganGestorResourceEntity> metaExpedientOrganGestors;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
			name = BaseConfig.DB_PREFIX + "metaexpedient_grup",
			joinColumns = {@JoinColumn(name = "metaexpedient_id", referencedColumnName="id")},
			inverseJoinColumns = {@JoinColumn(name = "grup_id")},
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "metaexp_metaexpgrup_fk"))
	private List<GrupResourceEntity> grups = new ArrayList<>();

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "grup_per_defecte",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "grup_metaexp_fk"))
	private GrupResourceEntity grupPerDefecte;

	@Column(name = "revisio_estat", length = 8)
	@Enumerated(EnumType.STRING)
	private MetaExpedientRevisioEstatEnumDto revisioEstat;
	@Column(name = "revisio_comentari", length = 1024)
	private String revisioComentari; // TODO to delete in next version

	@Column(name = "crear_regla_dist_estat", length = 10)
	@Enumerated(EnumType.STRING)
	private CrearReglaDistribucioEstatEnumDto crearReglaDistribucioEstat;
	@Column(name = "crear_regla_dist_error", length = 1024)
	private String crearReglaDistribucioError;

	@Column(name = "organ_no_sinc", nullable = false)
	private boolean organNoSincronitzat;

	@Column(name = "interessat_obligatori", nullable = false)
	private boolean interessatObligatori;

	public boolean isComu() {
		if (organGestor == null) {
			return true;
		} else {
			return false;
		}
	}
}
