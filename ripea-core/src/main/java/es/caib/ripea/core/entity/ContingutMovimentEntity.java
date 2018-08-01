/**
 * 
 */
package es.caib.ripea.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.ripea.core.audit.RipeaAuditable;

/**
 * Classe del model de dades que representa un canvi de lloc
 * d'un contenidor.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "ipa_cont_mov")
@Inheritance(strategy=InheritanceType.JOINED)
@EntityListeners(AuditingEntityListener.class)
public class ContingutMovimentEntity extends RipeaAuditable<Long> {

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "contingut_id")
	@ForeignKey(name = "ipa_contingut_contmov_fk")
	protected ContingutEntity contingut;
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "origen_id")
	@ForeignKey(name = "ipa_origen_contmov_fk")
	protected ContingutEntity origen;
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "desti_id")
	@ForeignKey(name = "ipa_desti_contmov_fk")
	protected ContingutEntity desti;
	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "remitent_codi")
	@ForeignKey(name = "ipa_remitent_contmov_fk")
	protected UsuariEntity remitent;
	@Column(name = "comentari", length = 256)
	protected String comentari;


	public ContingutEntity getContingut() {
		return contingut;
	}
	public ContingutEntity getOrigen() {
		return origen;
	}
	public ContingutEntity getDesti() {
		return desti;
	}
	public UsuariEntity getRemitent() {
		return remitent;
	}
	public String getComentari() {
		return comentari;
	}

	public static Builder getBuilder(
			ContingutEntity contenidor,
			ContingutEntity origen,
			ContingutEntity desti,
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
			ContingutEntity contenidor,
			ContingutEntity desti,
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
				ContingutEntity contingut,
				ContingutEntity origen,
				ContingutEntity desti,
				UsuariEntity remitent,
				String comentari) {
			built = new ContingutMovimentEntity();
			built.contingut = contingut;
			built.origen = origen;
			built.desti = desti;
			built.remitent = remitent;
			built.comentari = comentari;
		}
		public ContingutMovimentEntity build() {
			return built;
		}
	}

	private static final long serialVersionUID = -2299453443943600172L;

}
