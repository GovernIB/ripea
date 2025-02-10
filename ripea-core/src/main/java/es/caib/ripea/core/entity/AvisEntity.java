/**
 * 
 */
package es.caib.ripea.core.persistence;

import es.caib.ripea.core.api.dto.AvisNivellEnumDto;
import es.caib.ripea.core.audit.RipeaAuditable;
import lombok.Getter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * Classe del model de dades que representa una alerta d'error en segón pla.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(	name = "ipa_avis")
@Getter
@EntityListeners(AuditingEntityListener.class)
public class AvisEntity extends RipeaAuditable<Long> {
	
	@Column(name = "assumpte", length = 256, nullable = false)
	private String assumpte;
	@Column(name = "missatge", length = 2048, nullable = false)
	private String missatge;
	@Temporal(TemporalType.DATE)
	@Column(name = "data_inici", nullable = false)
	private Date dataInici;
	@Temporal(TemporalType.DATE)
	@Column(name = "data_final", nullable = false)
	private Date dataFinal;
	@Column(name = "actiu", nullable = false)
	private Boolean actiu;
	@Column(name = "avis_nivell", length = 2048, nullable = false)
	@Enumerated(EnumType.STRING)
	private AvisNivellEnumDto avisNivell;
	@Column(name = "avis_admin", nullable = false)
	private Boolean avisAdministrador;
	@Column(name = "entitat_id")
	private Long entitatId;
	
	
	public void update(
			String assumpte,
			String missatge,
			Date dataInici,
			Date dataFinal,
			AvisNivellEnumDto avisNivell) {
		this.assumpte = assumpte;
		this.missatge = missatge;
		this.dataInici = dataInici;
		this.dataFinal = dataFinal;
		this.avisNivell = avisNivell;
	}
	
	public void updateActiva(
			Boolean actiu) {
		this.actiu = actiu;
	}
	

	public static Builder getBuilder(
			String assumpte,
			String missatge,
			Date dataInici,
			Date dataFinal,
			AvisNivellEnumDto avisNivell,
			Boolean avisAdministrador,
			Long entitatId) {
		return new Builder(
				assumpte,
				missatge,
				dataInici,
				dataFinal,
				avisNivell,
				avisAdministrador,
				entitatId);
	}


	public static class Builder {
		AvisEntity built;
		Builder(
				String assumpte,
				String missatge,
				Date dataInici,
				Date dataFinal,
				AvisNivellEnumDto avisNivell,
				Boolean avisAdministrador,
				Long entitatId) {
			built = new AvisEntity();
			built.assumpte = assumpte;
			built.missatge = missatge;
			built.dataInici = dataInici;
			built.dataFinal = dataFinal;
			built.actiu = true;
			built.avisNivell = avisNivell;
			built.avisAdministrador = avisAdministrador != null ? avisAdministrador : false;
			built.entitatId = entitatId;
		}
		public AvisEntity build() {
			return built;
		}
	}
	

	private static final long serialVersionUID = -2299453443943600172L;
	
}
