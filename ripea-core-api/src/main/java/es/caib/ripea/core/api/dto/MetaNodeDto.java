/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

import lombok.Getter;
import lombok.Setter;

/**
 * Informació d'un MetaNode.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class MetaNodeDto implements Serializable {

	protected Long id;
	private String codi;
	protected String nom;
	private String descripcio;
	private boolean actiu;

	private List<PermisDto> permisos;
	private boolean usuariActualCreate;
	private boolean usuariActualRead;
	private boolean usuariActualWrite;
	private boolean usuariActualDelete;

	public int getPermisosCount() {
		if (permisos == null)
			return 0;
		else
			return permisos.size();
	}

	public String getIdentificador() {
		return nom + " (" + codi + ")";
	}
	

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MetaNodeDto other = (MetaNodeDto)obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	private static final long serialVersionUID = -139254994389509932L;

}
