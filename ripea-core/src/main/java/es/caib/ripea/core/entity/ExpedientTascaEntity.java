/**
 * 
 */
package es.caib.ripea.core.entity;

import es.caib.ripea.core.api.dto.TascaEstatEnumDto;
import es.caib.ripea.core.audit.RipeaAuditable;
import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Classe del model de dades que representa una tasca del expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(	name = "ipa_expedient_tasca")
@EntityListeners(AuditingEntityListener.class)
@SuppressWarnings("serial")
public class ExpedientTascaEntity extends RipeaAuditable<Long> {

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "expedient_id")
	@ForeignKey(name = "ipa_exp_exptasc_fk")
	private ExpedientEntity expedient;
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "metaexp_tasca_id")
	@ForeignKey(name = "ipa_metaexptasca_exptasc_fk")
	private MetaExpedientTascaEntity metaExpedientTasca;
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "responsable_actual_codi")
	@ForeignKey(name = "ipa_usuari_exptasc_fk")
	private UsuariEntity responsableActual;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "ipa_expedient_tasca_resp",
			joinColumns = {@JoinColumn(name = "tasca_id", referencedColumnName="id")},
			inverseJoinColumns = {@JoinColumn(name = "responsable_codi")})
	@ForeignKey(
			name = "ipa_expedient_tasca_fk",
			inverseName = "ipa_expedient_tascaresp_fk")
	private List<UsuariEntity> responsables = new ArrayList<UsuariEntity>();
	
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_inici", nullable = false)
	private Date dataInici;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_fi")
	private Date dataFi;

	@Column(name = "estat", length = 20, nullable = false)
	@Enumerated(EnumType.STRING)
	private TascaEstatEnumDto estat;
	
	@Column(name = "motiu_rebuig", length = 1024)
	@Enumerated(EnumType.STRING)
	private String motiuRebuig;

	@Temporal(TemporalType.DATE)
	@Column(name = "data_limit")
	private Date dataLimit;
	
//	@Column(name = "comentari", length = 1024)
//	private String comentari;

	@OneToMany(
			mappedBy = "expedientTasca",
			cascade = CascadeType.ALL,
			orphanRemoval = true)
	@OrderBy("createdDate")
	private List<ExpedientTascaComentariEntity> comentaris = new ArrayList<>();
	
	@Column(name = "titol", length = 255)
	private String titol;
	
	@Column(name = "observacions", length = 1024)
	private String observacions;
	
	public static Builder getBuilder(
			ExpedientEntity expedient,
			MetaExpedientTascaEntity metaExpedientTasca,
			List<UsuariEntity> responsables,
			Date dataLimit,
			String titol,
			String observacions) {
		return new Builder(
				expedient,
				metaExpedientTasca,
				responsables,
				dataLimit,
				titol,
				observacions);
	}
	
	public static class Builder {
		ExpedientTascaEntity built;
		Builder(
				ExpedientEntity expedient,
				MetaExpedientTascaEntity metaExpedientTasca,
				List<UsuariEntity> responsables,
				Date dataLimit,
				String titol,
				String observacions) {
			built = new ExpedientTascaEntity();
			built.expedient = expedient;
			built.metaExpedientTasca = metaExpedientTasca;
			built.responsables = responsables;
			built.dataInici = new Date();
			built.estat = TascaEstatEnumDto.PENDENT;
			built.dataLimit = dataLimit;
			built.titol = titol;
			built.observacions = observacions;
		}
		public ExpedientTascaEntity build() {
			return built;
		}
	}

	public void updateResponsableActual(UsuariEntity responsableActual) {
		this.responsableActual = responsableActual;
	}
	
	public void updateEstat(TascaEstatEnumDto estat) {
		this.estat = estat;
		if (estat == TascaEstatEnumDto.FINALITZADA) {
			dataFi = new Date();
		}
	}
	public void updateDataFi(Date dataFi) {
		this.dataFi = dataFi;
	}
	public void updateDataLimit(Date dataLimit) {
		this.dataLimit = dataLimit;
	}
	public void updateRebutjar(String motiuRebuig) {
		this.motiuRebuig = motiuRebuig;
		this.estat = TascaEstatEnumDto.REBUTJADA;
	}
	
	public ExpedientEntity getExpedient() {
		return expedient;
	}

	public MetaExpedientTascaEntity getMetaTasca() {
		return metaExpedientTasca;
	}

	public List<UsuariEntity> getResponsables() {
		return responsables;
	}
	public UsuariEntity getResponsableActual() {
		return responsableActual;
	}
	public void addResponsable(UsuariEntity responsable) {
		responsables.add(responsable);
	}
	public void removeResponsable(UsuariEntity responsable) {
		responsables.remove(responsable);
	}
	public void updateResponsables(List<UsuariEntity> responsables) {
		this.responsables = responsables;
	}
	public Date getDataInici() {
		return dataInici;
	}
	public Date getDataLimit() {
		return dataLimit;
	}
	public Date getDataFi() {
		return dataFi;
	}
	public TascaEstatEnumDto getEstat() {
		return estat;
	}
	public String getMotiuRebuig() {
		return motiuRebuig;
	}
	public List<ExpedientTascaComentariEntity> getComentaris() {
		return comentaris;
	}
	public void addComentari(ExpedientTascaComentariEntity comentari) {
		comentaris.add(comentari);
	}
	public void updateComentaris(List<ExpedientTascaComentariEntity> comentaris) {
		this.comentaris = comentaris;
	}
	public String getTitol() {
		return titol;
	}
	public String getObservacions() {
		return observacions;
	}
	public String getTextLastComentari() {
		String comentariText = null;
		if (this.getComentaris() != null && !this.getComentaris().isEmpty()) {
		 comentariText = this.getComentaris().get(this.getComentaris().size() - 1).getText();
		}
		return comentariText;
	}

}
