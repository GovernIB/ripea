/**
 * 
 */
package es.caib.ripea.core.entity;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.*;

import es.caib.ripea.core.api.dto.PrioritatEnumDto;
import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.ripea.core.audit.RipeaAuditable;
import lombok.Getter;
import lombok.Setter;

/**
 * Classe del model de dades que representa una tasca d'un meta-expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(
		name = "ipa_metaexp_tasca",
		uniqueConstraints = {
				@UniqueConstraint(columnNames = {"codi", "meta_expedient_id"})
		})
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter
public class MetaExpedientTascaEntity extends RipeaAuditable<Long> {

	@Column(name = "codi", length = 64, nullable = false)
	private String codi;
	@Column(name = "nom", length = 256, nullable = false)
	private String nom;
	@Column(name = "descripcio", length = 1024, nullable = false)
	private String descripcio;
	@Column(name = "responsable", length = 64)
	private String responsable;
	@Column(name = "activa", nullable = false)
	private boolean activa;
	@Temporal(TemporalType.DATE)
	@Column(name = "data_limit")
	private Date dataLimit;
	@Column(name = "DURACIO")
	private Integer duracio;
	@Column(name = "PRIORITAT", length = 16)
	@Enumerated(EnumType.STRING)
	private PrioritatEnumDto prioritat;
	@ManyToOne
	@JoinColumn(name = "estat_crear_tasca_id")
	private ExpedientEstatEntity estatCrearTasca;
	@ManyToOne
	@JoinColumn(name = "estat_finalitzar_tasca_id")
	private ExpedientEstatEntity estatFinalitzarTasca;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "meta_expedient_id")
	@ForeignKey(name = "ipa_metaexp_metaexptas_fk")
	private MetaExpedientEntity metaExpedient;
	
	public void update(
			String codi,
			String nom,
			String descripcio,
			String responsable,
			Date dataLimit,
			Integer duracio,
			PrioritatEnumDto prioritat,
			ExpedientEstatEntity estatCrearTasca,
			ExpedientEstatEntity estatFinalitzarTasca) {
		this.codi = codi;
		this.nom = nom;
		this.descripcio = descripcio;
		this.responsable = responsable;
		this.dataLimit = dataLimit;
		this.duracio = duracio;
		if (duracio!=null) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_YEAR, duracio);
			this.dataLimit = cal.getTime();
		}
		this.prioritat = prioritat != null ? prioritat : PrioritatEnumDto.B_NORMAL;
		this.estatCrearTasca = estatCrearTasca;
		this.estatFinalitzarTasca = estatFinalitzarTasca;
	}
	public void updateActiva(
			boolean activa) {
		this.activa = activa;
	}

	public static Builder getBuilder(
			String codi,
			String nom,
			String descripcio,
			String responsable,
			MetaExpedientEntity metaExpedient,
			Date dataLimit,
			Integer duracio,
			PrioritatEnumDto prioritat,
			ExpedientEstatEntity estatCrearTasca,
			ExpedientEstatEntity estatFinalitzarTasca) {
		return new Builder(
				codi,
				nom,
				descripcio,
				responsable,
				metaExpedient,
				dataLimit,
				duracio,
				prioritat,
				estatCrearTasca,
				estatFinalitzarTasca);
	}
	public static class Builder {
		MetaExpedientTascaEntity built;
		Builder(
				String codi,
				String nom,
				String descripcio,
				String responsable,
				MetaExpedientEntity metaExpedient,
				Date dataLimit,
				Integer duracio,
				PrioritatEnumDto prioritat,
				ExpedientEstatEntity estatCrearTasca,
				ExpedientEstatEntity estatFinalitzarTasca) {
			built = new MetaExpedientTascaEntity();
			built.codi = codi;
			built.nom = nom;
			built.descripcio = descripcio;
			built.responsable = responsable;
			built.metaExpedient = metaExpedient;
			built.activa = true;
			built.duracio = duracio;
			if (duracio!=null) {
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DAY_OF_YEAR, duracio);
				built.dataLimit = cal.getTime();
			}
			built.prioritat = prioritat != null ? prioritat : PrioritatEnumDto.B_NORMAL;
			built.estatCrearTasca = estatCrearTasca;
			built.estatFinalitzarTasca = estatFinalitzarTasca;
		}
		public MetaExpedientTascaEntity build() {
			return built;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((codi == null) ? 0 : codi.hashCode());
		result = prime * result + ((metaExpedient == null) ? 0 : metaExpedient.hashCode());
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
		MetaExpedientTascaEntity other = (MetaExpedientTascaEntity) obj;
		if (codi == null) {
			if (other.codi != null)
				return false;
		} else if (!codi.equals(other.codi))
			return false;
		if (metaExpedient == null) {
			if (other.metaExpedient != null)
				return false;
		} else if (!metaExpedient.equals(other.metaExpedient))
			return false;
		return true;
	}

	private static final long serialVersionUID = -2299453443943600172L;

}
