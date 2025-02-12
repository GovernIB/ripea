/**
 * 
 */
package es.caib.ripea.persistence.entity;

import es.caib.ripea.service.intf.config.BaseConfig;
import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

/**
 * Classe del model de dades que representa un canvi de lloc
 * d'un contenidor.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "cont_mov")
@Inheritance(strategy=InheritanceType.JOINED)
@EntityListeners(AuditingEntityListener.class)
public class ContingutMovimentEntity extends RipeaAuditable<Long> {

//	@ManyToOne(optional = false, fetch = FetchType.LAZY)
//	@JoinColumn(name = "contingut_id")
//	@ForeignKey(name = BaseConfig.DB_PREFIX + "contingut_contmov_fk")
	@Column(name = "contingut_id")
	protected Long contingutId;
//	@ManyToOne(optional = true, fetch = FetchType.LAZY)
//	@JoinColumn(name = "origen_id")
//	@ForeignKey(name = BaseConfig.DB_PREFIX + "origen_contmov_fk")
	@Column(name = "origen_id")
	protected Long origenId;
//	@ManyToOne(optional = false, fetch = FetchType.LAZY)
//	@JoinColumn(name = "desti_id")
//	@ForeignKey(name = BaseConfig.DB_PREFIX + "desti_contmov_fk")
	@Column(name = "desti_id")
	protected Long destiId;
	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "remitent_codi")
	@ForeignKey(name = BaseConfig.DB_PREFIX + "remitent_contmov_fk")
	protected UsuariEntity remitent;
	@Column(name = "comentari", length = 256)
	protected String comentari;


	public Long getContingutId() {
		return contingutId;
	}
	public Long getOrigenId() {
		return origenId;
	}
	public Long getDestiId() {
		return destiId;
	}
	public UsuariEntity getRemitent() {
		return remitent;
	}
	public String getComentari() {
		return comentari;
	}

	public static Builder getBuilder(
			Long contenidor,
			Long origen,
			Long desti,
			UsuariEntity remitent,
			String comentari) {
		return new Builder(
				contenidor,
				origen,
				desti,
				remitent,
				comentari);
	}
	public static Builder getBuilder(
			Long contenidor,
			Long desti,
			UsuariEntity remitent,
			String comentari) {
		return new Builder(
				contenidor,
				null,
				desti,
				remitent,
				comentari);
	}
	public static class Builder {
		ContingutMovimentEntity built;
		Builder(
				Long contingutId,
				Long origenId,
				Long destiId,
				UsuariEntity remitent,
				String comentari) {
			built = new ContingutMovimentEntity();
			built.contingutId = contingutId;
			built.origenId = origenId;
			built.destiId = destiId;
			built.remitent = remitent;
			built.comentari = comentari;
		}
		public ContingutMovimentEntity build() {
			return built;
		}
	}

	private static final long serialVersionUID = -2299453443943600172L;

}
