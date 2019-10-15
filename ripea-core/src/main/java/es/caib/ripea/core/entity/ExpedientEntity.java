/**
 * 
 */
package es.caib.ripea.core.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.ripea.core.api.dto.ContingutTipusEnumDto;
import es.caib.ripea.core.api.dto.ExpedientEstatEnumDto;

/**
 * Classe del model de dades que representa un expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "ipa_expedient", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"metaexpedient_id", "anio", "sequencia"})
})
@EntityListeners(AuditingEntityListener.class)
public class ExpedientEntity extends NodeEntity {

	@Column(name = "estat", nullable = false)
	protected ExpedientEstatEnumDto estat;
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "metaexpedient_id")
	@ForeignKey(name = "ipa_metaexp_expedient_fk")
	private MetaExpedientEntity metaExpedient;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "tancat_data")
	protected Date tancatData;
	@Column(name = "tancat_motiu", length = 1024)
	protected String tancatMotiu;
	@Column(name = "anio", nullable = false)
	protected int any;
	@Column(name = "sequencia", nullable = false)
	protected long sequencia;
	@Column(name = "codi", nullable = false)
	protected String codi;
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
	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "agafat_per_codi")
	@ForeignKey(name = "ipa_agafatper_expedient_fk")
	protected UsuariEntity agafatPer;
	@OneToMany(
			mappedBy = "expedient",
			fetch = FetchType.LAZY,
			orphanRemoval = true)
	protected Set<InteressatEntity> interessats = new HashSet<InteressatEntity>();
	@ManyToMany(
			cascade = {
					CascadeType.DETACH,
					CascadeType.MERGE,
					CascadeType.REFRESH,
					CascadeType.PERSIST
			},
			fetch = FetchType.LAZY)
	@JoinTable(
			name = "ipa_expedient_rel",
			joinColumns = {
					@JoinColumn(name = "expedient_id", referencedColumnName = "id")},
			inverseJoinColumns = {
					@JoinColumn(name = "expedient_rel_id", referencedColumnName = "id")})
	@ForeignKey(
			name = "ipa_exprel_exprel_fk",
			inverseName = "ipa_expedient_exprel_fk")
	protected List<ExpedientEntity> relacionatsAmb = new ArrayList<ExpedientEntity>();
	@ManyToMany(
			cascade = {
					CascadeType.DETACH,
					CascadeType.MERGE,
					CascadeType.REFRESH,
					CascadeType.PERSIST
			},
			fetch = FetchType.LAZY)
	@JoinTable(
			name = "ipa_expedient_rel",
			joinColumns = {
					@JoinColumn(name = "expedient_rel_id", referencedColumnName="id")},
			inverseJoinColumns = {
					@JoinColumn(name = "expedient_id", referencedColumnName="id")})
	@ForeignKey(
			name = "ipa_expedient_rel_rel_fk",
			inverseName = "ipa_expedient_rel_exp_fk")
	protected List<ExpedientEntity> relacionatsPer = new ArrayList<ExpedientEntity>();
	
	@OneToMany(
			mappedBy = "expedient",
			cascade = CascadeType.ALL,
			orphanRemoval = true)
	private List<ExpedientPeticioEntity> peticions = new ArrayList<ExpedientPeticioEntity>();
	
	@OneToMany(
			mappedBy = "expedient",
			cascade = CascadeType.ALL,
			orphanRemoval = true)
	private List<ExpedientTascaEntity> tasques = new ArrayList<ExpedientTascaEntity>();
	
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "expedient_estat_id")
	@ForeignKey(name = "ipa_expestat_expedient_fk")
	private ExpedientEstatEntity expedientEstat;
	
	
	public List<ExpedientTascaEntity> getTasques() {
		return tasques;
	}
	public void updateTasques(List<ExpedientTascaEntity> tasques) {
		this.tasques = tasques;
	}
	public List<ExpedientPeticioEntity> getPeticions() {
		return peticions;
	}
	public void addExpedientPeticio(ExpedientPeticioEntity expedientPeticioEntity) {
		this.peticions.add(expedientPeticioEntity);
	}
	public ExpedientEstatEnumDto getEstat() {
		return estat;
	}
	public Date getTancatData() {
		return tancatData;
	}
	public String getTancatMotiu() {
		return tancatMotiu;
	}
	public int getAny() {
		return any;
	}
	public long getSequencia() {
		return sequencia;
	}
	public String getCodi() {
		return codi;
	}
	public ExpedientEstatEntity getExpedientEstat() {
		return expedientEstat;
	}
	public String getNtiVersion() {
		return ntiVersion;
	}
	public String getNtiIdentificador() {
		return ntiIdentificador;
	}
	public String getNtiOrgano() {
		return ntiOrgano;
	}
	public Date getNtiFechaApertura() {
		return ntiFechaApertura;
	}
	public String getNtiClasificacionSia() {
		return ntiClasificacionSia;
	}
	public String getSistraBantelNum() {
		return sistraBantelNum;
	}
	public boolean isSistraPublicat() {
		return sistraPublicat;
	}
	public String getSistraUnitatAdministrativa() {
		return sistraUnitatAdministrativa;
	}
	public String getSistraClau() {
		return sistraClau;
	}
	public UsuariEntity getAgafatPer() {
		return agafatPer;
	}
	public Set<InteressatEntity> getInteressats() {
		return interessats;
	}
	public List<ExpedientEntity> getRelacionatsAmb() {
		return relacionatsAmb;
	}
	public List<ExpedientEntity> getRelacionatsPer() {
		return relacionatsPer;
	}
	public MetaExpedientEntity getMetaExpedient() {
		return (MetaExpedientEntity)getMetaNode();
	}

	public void update(
			String nom) {
		this.nom = nom;
	}
	
	public void updateAny(
			int any) {
		this.any = any;
	}
	public void updateAnySequenciaCodi(
			int any,
			long sequencia,
			String codi) {
		this.any = any;
		this.sequencia = sequencia;
		this.codi = codi;
	}
	public void updateNtiIdentificador(
			String ntiIdentificador) {
		this.ntiIdentificador = ntiIdentificador;
	}
	public void updateNti(
			String ntiVersion,
			String ntiIdentificador,
			String ntiOrgano,
			Date ntiFechaApertura,
			String ntiClasificacionSia) {
		this.ntiVersion = ntiVersion;
		this.ntiIdentificador = ntiIdentificador;
		this.ntiOrgano = ntiOrgano;
		this.ntiFechaApertura = ntiFechaApertura;
		this.ntiClasificacionSia = ntiClasificacionSia;
	}
	public void updateEstat(
			ExpedientEstatEnumDto estat,
			String tancatMotiu) {
		this.estat = estat;
		this.tancatMotiu = tancatMotiu;
		if (ExpedientEstatEnumDto.TANCAT.equals(estat))
			this.tancatData = new Date();
	}
	public void updateExpedientEstat(
			ExpedientEstatEntity expedientEstat) {
		this.expedientEstat = expedientEstat;
	}
	
	public void updateAgafatPer(
			UsuariEntity usuari) {
		this.agafatPer = usuari;
	}

	public void updateSistra(
			boolean sistraPublicat,
			String sistraUnitatAdministrativa,
			String sistraClau) {
		this.sistraPublicat = sistraPublicat;
		this.sistraUnitatAdministrativa = sistraUnitatAdministrativa;
		this.sistraClau = sistraClau;
	}

	public void addInteressat(InteressatEntity interessat) {
		interessats.add(interessat);
	}
	public void deleteInteressat(InteressatEntity interessat) {
		Iterator<InteressatEntity> it = interessats.iterator();
		while (it.hasNext()) {
			InteressatEntity ie = it.next();
			if (ie.equals(interessat))
				it.remove();
		}
	}

	public void addRelacionat(ExpedientEntity relacionat) {
		relacionatsAmb.add(relacionat);
	}
	public void removeRelacionat(ExpedientEntity relacionat) {
		relacionatsAmb.remove(relacionat);
	}

	public static Builder getBuilder(
			String nom,
			MetaExpedientEntity metaExpedient,
			ContingutEntity pare,
			EntitatEntity entitat,
			String ntiVersion,
			String ntiOrgano,
			Date ntiFechaApertura,
			String ntiClasificacionSia) {
		return new Builder(
				nom,
				metaExpedient,
				pare,
				entitat,
				ntiVersion,
				ntiOrgano,
				ntiFechaApertura,
				ntiClasificacionSia);
	}

	public static class Builder {
		ExpedientEntity built;
		Builder(
				String nom,
				MetaExpedientEntity metaExpedient,
				ContingutEntity pare,
				EntitatEntity entitat,
				String ntiVersion,
				String ntiOrgano,
				Date ntiFechaApertura,
				String ntiClasificacionSia) {
			built = new ExpedientEntity();
			built.nom = nom;
			built.metaNode = metaExpedient;
			built.metaExpedient = metaExpedient;
			built.pare = pare;
			built.entitat = entitat;
			built.ntiVersion = ntiVersion;
			built.ntiIdentificador = new Long(System.currentTimeMillis()).toString();
			built.ntiOrgano = ntiOrgano;
			built.ntiFechaApertura = ntiFechaApertura;
			built.ntiClasificacionSia = ntiClasificacionSia;
			built.estat = ExpedientEstatEnumDto.OBERT;
			built.tipus = ContingutTipusEnumDto.EXPEDIENT;
		}
		public Builder agafatPer(UsuariEntity agafatPer) {
			built.agafatPer = agafatPer;
			return this;
		}
		public ExpedientEntity build() {
			return built;
		}
	}

	private static final long serialVersionUID = -2299453443943600172L;

}