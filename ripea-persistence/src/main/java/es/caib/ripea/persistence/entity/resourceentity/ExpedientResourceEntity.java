package es.caib.ripea.persistence.entity.resourceentity;

import es.caib.ripea.persistence.base.entity.ResourceEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.ExpedientEstatEnumDto;
import es.caib.ripea.service.intf.dto.PrioritatEnumDto;
import es.caib.ripea.service.intf.model.ExpedientResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "expedient")
@Getter
@Setter
@NoArgsConstructor
public class ExpedientResourceEntity extends NodeResourceEntity implements ResourceEntity<ExpedientResource, Long> {

	@Column(name = "estat", nullable = false)
	protected ExpedientEstatEnumDto estat;
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "metaexpedient_id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "metaexp_expedient_fk"))
	private MetaExpedientResourceEntity metaExpedient;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "tancat_data")
	protected Date tancatData;
	@Column(name = "tancat_motiu", length = 1024)
	protected String tancatMotiu;
	@Temporal(TemporalType.DATE)
	@Column(name = "tancat_programat")
	protected Date tancatProgramat;
	@Column(name = "anio", nullable = false)
	protected int any;
	@Column(name = "sequencia", nullable = false)
	protected long sequencia;
	@Column(name = "codi", nullable = false)
	protected String codi;
	@Column(name = "numero", length = 64, nullable = false)
	protected String numero;
	@Column(name = "nti_version", length = 5, nullable = false)
	protected String ntiVersion;
	@Column(name = "nti_identif", length = 52, nullable = false)
	protected String ntiIdentificador;
	@Column(name = "nti_organo", length = 9, nullable = false)
	protected String ntiOrgano;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "nti_fecha_ape", nullable = false)
	protected Date ntiFechaApertura;
	@Column(name = "nti_clasif_sia", length = 6, nullable = false)
	protected String ntiClasificacionSia;
	@Column(name = "sistra_bantel_num", length = 16)
	protected String sistraBantelNum;
	@Column(name = "sistra_publicat")
	protected boolean sistraPublicat;
	@Column(name = "sistra_unitat_adm", length = 9)
	protected String sistraUnitatAdministrativa;
	@Column(name = "sistra_clau", length = 100)
	protected String sistraClau;
	@Column(name = "registres_importats", length = 4000)
	protected String registresImportats;
	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(
			name = "agafat_per_codi",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "agafatper_expedient_fk"))
	protected UsuariResourceEntity agafatPer;
//	@OneToMany(
//			mappedBy = "expedient",
//			fetch = FetchType.LAZY,
//			orphanRemoval = true)
//	protected Set<InteressatEntity> interessats = new HashSet<InteressatEntity>();
	@ManyToMany(
			cascade = {
					CascadeType.DETACH,
					CascadeType.MERGE,
					CascadeType.REFRESH,
					CascadeType.PERSIST
			},
			fetch = FetchType.LAZY)
	@JoinTable(
			name = BaseConfig.DB_PREFIX + "expedient_rel",
			joinColumns = {
					@JoinColumn(name = "expedient_id", referencedColumnName = "id")},
			inverseJoinColumns = {
					@JoinColumn(name = "expedient_rel_id", referencedColumnName = "id")},
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "exprel_exprel_fk"))
	protected List<ExpedientResourceEntity> relacionatsAmb = new ArrayList<>();
	@ManyToMany(
			cascade = {
					CascadeType.DETACH,
					CascadeType.MERGE,
					CascadeType.REFRESH,
					CascadeType.PERSIST
			},
			fetch = FetchType.LAZY)
	@JoinTable(
			name = BaseConfig.DB_PREFIX + "expedient_rel",
			joinColumns = {
					@JoinColumn(name = "expedient_rel_id", referencedColumnName="id")},
			inverseJoinColumns = {
					@JoinColumn(name = "expedient_id", referencedColumnName="id")},
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "expedient_rel_rel_fk"))
	protected List<ExpedientResourceEntity> relacionatsPer = new ArrayList<>();

//	@OneToMany(mappedBy = "expedient")
//	private List<ExpedientPeticioEntity> peticions = new ArrayList<ExpedientPeticioEntity>();
//
//	@OneToMany(
//			mappedBy = "expedient",
//			cascade = CascadeType.ALL,
//			orphanRemoval = true)
//	private List<ExpedientTascaEntity> tasques = new ArrayList<ExpedientTascaEntity>();
//
//	@OneToMany(
//			mappedBy = "expedient",
//			cascade = CascadeType.ALL,
//			orphanRemoval = true)
//	private List<ExpedientComentariEntity> comentaris = new ArrayList<ExpedientComentariEntity>();

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "expedient_estat_id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "expestat_expedient_fk"))
	private ExpedientEstatResourceEntity estatAdditional;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = BaseConfig.DB_PREFIX + "expedient_seguidor",
			joinColumns = {
					@JoinColumn(name = "expedient_id", referencedColumnName = "id")},
			inverseJoinColumns = {
					@JoinColumn(name = "seguidor_codi", referencedColumnName = "codi")},
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "expedient_expseguidor_fk"))
	protected List<UsuariResourceEntity> seguidors = new ArrayList<>();

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "grup_id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "grup_expedient_fk"))
	private GrupResourceEntity grup;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "organ_gestor_id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "organ_gestor_exp_fk"))
	private OrganGestorResourceEntity organGestor;

//	@OneToMany(
//			mappedBy = "expedient",
//			cascade = CascadeType.ALL,
//			orphanRemoval = true)
//	private List<ExpedientOrganPareEntity> organGestorPares = new ArrayList<ExpedientOrganPareEntity>();
//
//	@ManyToMany(fetch = FetchType.LAZY)
//	@JoinTable(
//			name = BaseConfig.DB_PREFIX + "expedient_organpare",
//			joinColumns = @JoinColumn(name = "expedient_id"),
//			inverseJoinColumns = @JoinColumn(name = "meta_expedient_organ_id"))
//	Set<MetaExpedientOrganGestorEntity> metaexpedientOrganGestorPares;

	@Column(name = "prioritat")
	@Enumerated(EnumType.STRING)
	private PrioritatEnumDto prioritat;

	@Column(name = "prioritat_motiu")
	private String prioritatMotiu;

}
