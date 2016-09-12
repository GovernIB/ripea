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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

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
@Table(name = "ipa_expedient")
@EntityListeners(AuditingEntityListener.class)
public class ExpedientEntity extends NodeEntity {

	@ManyToOne(
			optional = false,
			fetch = FetchType.EAGER)
	@JoinColumn(name = "arxiu_id")
	@ForeignKey(name = "ipa_arxiu_expedient_fk")
	protected ArxiuEntity arxiu;
	@ManyToMany(
			cascade = CascadeType.ALL,
			fetch = FetchType.LAZY)
	@JoinTable(
			name = "ipa_expedient_interessat",
			joinColumns = {@JoinColumn(name = "expedient_id")},
			inverseJoinColumns = {@JoinColumn(name = "interessat_id")})
	protected Set<InteressatEntity> interessats = new HashSet<InteressatEntity>();
	@Column(name = "estat", nullable = false)
	protected ExpedientEstatEnumDto estat;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "tancat_data")
	protected Date tancatData;
	@Column(name = "tancat_motiu", length = 1024)
	protected String tancatMotiu;
	@Column(name = "anio", nullable = false)
	protected int any;
	@Column(name = "sequencia", nullable = false)
	protected long sequencia;
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
	@Column(name = "sistra_publicat")
	protected boolean sistraPublicat;
	@Column(name = "sistra_unitat_adm")
	protected Integer sistraUnitatAdministrativa;
	@Column(name = "sistra_clau", length = 100)
	protected String sistraClau;
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
			joinColumns = {@JoinColumn(name = "expedient_id", referencedColumnName="id")},
			inverseJoinColumns = {@JoinColumn(name = "expedient_rel_id", referencedColumnName="id")})
	@ForeignKey(name="ipa_expedient_rel_exp_fk", inverseName="ipa_expedient_rel_rel_fk")	
	protected List<ExpedientEntity> expedientsRelacionatsAmb;
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
					@JoinColumn(name = "expedient_rel_id", referencedColumnName="id")
			},
			inverseJoinColumns = {
					@JoinColumn(name = "expedient_id", referencedColumnName="id")
			})
	@ForeignKey(name="ipa_expedient_rel_rel_fk", inverseName="ipa_expedient_rel_exp_fk")	
	protected List<ExpedientEntity> expedientsRelacionatsPer;

	public ArxiuEntity getArxiu() {
		return arxiu;
	}
	public Set<InteressatEntity> getInteressats() {
		return interessats;
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
	public boolean isSistraPublicat() {
		return sistraPublicat;
	}
	public Integer getSistraUnitatAdministrativa() {
		return sistraUnitatAdministrativa;
	}
	public String getSistraClau() {
		return sistraClau;
	}
	public MetaExpedientEntity getMetaExpedient() {
		return (MetaExpedientEntity)getMetaNode();
	}

	public void update(
			String nom,
			MetaExpedientEntity metaExpedient,
			ArxiuEntity arxiu) {
		this.nom = nom;
		this.metaNode = metaExpedient;
		this.arxiu = arxiu;
	}
	public void updateAnySequencia(
			int any,
			long sequencia) {
		this.any = any;
		this.sequencia = sequencia;
	}
	public void updateNtiIdentificador(
			String ntiIdentificador) {
		this.ntiIdentificador = ntiIdentificador;
	}
	public void updateEstat(
			ExpedientEstatEnumDto estat,
			String tancatMotiu) {
		this.estat = estat;
		this.tancatMotiu = tancatMotiu;
		if (ExpedientEstatEnumDto.TANCAT.equals(estat))
			this.tancatData = new Date();
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

	public List<ExpedientEntity> getExpedientsRelacionatsAmb() {
		return expedientsRelacionatsAmb;
	}
	public void setExpedientsRelacionatsAmb(List<ExpedientEntity> expedientsRelacionatsAmb) {
		this.expedientsRelacionatsAmb = expedientsRelacionatsAmb;
	}

	public List<ExpedientEntity> getExpedientsRelacionatsPer() {
		return expedientsRelacionatsPer;
	}
	public void setExpedientsRelacionatsPer(List<ExpedientEntity> expedientsRelacionatsPer) {
		this.expedientsRelacionatsPer = expedientsRelacionatsPer;
	}

	@Transient
	public List<ExpedientEntity> getExpedientsRelacionats() {
		List<ExpedientEntity> relacionats = new ArrayList<ExpedientEntity>();
		relacionats.addAll(this.getExpedientsRelacionatsAmb());
		relacionats.addAll(this.getExpedientsRelacionatsPer());
		return relacionats;
	}

	public static Builder getBuilder(
			String nom,
			MetaNodeEntity metaNode,
			ArxiuEntity arxiu,
			ContingutEntity pare,
			EntitatEntity entitat,
			String ntiVersion,
			String ntiOrgano,
			Date ntiFechaApertura,
			String ntiClasificacionSia) {
		return new Builder(
				nom,
				metaNode,
				arxiu,
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
				MetaNodeEntity metaNode,
				ArxiuEntity arxiu,
				ContingutEntity pare,
				EntitatEntity entitat,
				String ntiVersion,
				String ntiOrgano,
				Date ntiFechaApertura,
				String ntiClasificacionSia) {
			built = new ExpedientEntity();
			built.nom = nom;
			built.metaNode = metaNode;
			built.arxiu = arxiu;
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
		public ExpedientEntity build() {
			return built;
		}
	}

	private static final long serialVersionUID = -2299453443943600172L;

}
