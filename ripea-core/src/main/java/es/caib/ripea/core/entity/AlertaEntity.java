/**
 * 
 */
package es.caib.ripea.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.ripea.core.audit.RipeaAuditable;

/**
 * Classe del model de dades que representa una alerta d'error en segón pla.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(	name = "ipa_alerta")
@EntityListeners(AuditingEntityListener.class)
public class AlertaEntity extends RipeaAuditable<Long> {

	private static final int ERROR_MAX_LENGTH = 2048;

	@Column(name = "text", length = 1024, nullable = false)
	private String text;
	@Column(name = "error", length = ERROR_MAX_LENGTH)
	private String error;
	@Column(name = "llegida", nullable = false)
	private Boolean llegida;
	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "contingut_id")
	@ForeignKey(name = "ipa_contingut_alerta_fk")
	protected ContingutEntity contingut;

	public String getText() {
		return text;
	}
	public String getError() {
		return error;
	}
	public Boolean getLlegida() {
		return llegida;
	}
	public ContingutEntity getContingut() {
		return contingut;
	}

	public void update(
			String text,
			String error,
			boolean llegida) {
		this.text = text;
		this.error = StringUtils.abbreviate(error, ERROR_MAX_LENGTH);
		this.llegida = new Boolean(llegida);
	}
	public void updateContingut(
			ContingutEntity contingut) {
		this.contingut = contingut;
	}

	public static Builder getBuilder(
			String text,
			String error,
			boolean llegida,
			ContingutEntity contingut) {
		return new Builder(
				text,
				error,
				llegida,
				contingut);
	}
	public static class Builder {
		AlertaEntity built;
		Builder(
				String text,
				String error,
				boolean llegida,
				ContingutEntity contingut) {
			built = new AlertaEntity();
			built.text = text;
			built.error = StringUtils.abbreviate(error, ERROR_MAX_LENGTH);
			built.llegida = new Boolean(llegida);
			built.contingut = contingut;
		}
		public AlertaEntity build() {
			return built;
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((text == null) ? 0 : text.hashCode());
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
		AlertaEntity other = (AlertaEntity) obj;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		if (llegida == null) {
			if (other.llegida != null)
				return false;
		} else if (!llegida.equals(other.llegida))
			return false;
		return true;
	}

	private static final long serialVersionUID = -2299453443943600172L;

}
