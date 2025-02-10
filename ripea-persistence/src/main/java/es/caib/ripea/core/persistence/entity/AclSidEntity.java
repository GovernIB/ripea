/**
 * 
 */
package es.caib.ripea.core.persistence.entity;

import es.caib.ripea.service.intf.config.BaseConfig;
import lombok.Getter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Classe del model de dades que representa un SID d'una ACL.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Entity
@Table(name = BaseConfig.DB_PREFIX + "acl_sid")
public class AclSidEntity extends AbstractPersistable<Long> {

	@Column(name = "principal", nullable = false)
	private boolean principal;
	@Column(name = "sid", length = 100, nullable = false)
	private String sid;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (principal ? 1231 : 1237);
		result = prime * result + ((sid == null) ? 0 : sid.hashCode());
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
		AclSidEntity other = (AclSidEntity) obj;
		if (principal != other.principal)
			return false;
		if (sid == null) {
			if (other.sid != null)
				return false;
		} else if (!sid.equals(other.sid))
			return false;
		return true;
	}

	private static final long serialVersionUID = -2299453443943600172L;

}
