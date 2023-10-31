package es.caib.ripea.core.entity;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

@Entity
@Table(	name = "ipa_viafirma_usuari")
public class ViaFirmaUsuariEntity implements Serializable {
	
	@Id
	@Column (name = "codi", length = 64)
	private String codi;
	
	@Column (name = "descripcio", length = 64)
	private String descripcio;
	
	@Column (name = "contrasenya", length = 64)
	private String contrasenya;

	@ManyToMany(mappedBy = "viaFirmaUsuaris", cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private Set<UsuariEntity> usuaris;
	
	public String getCodi() {
		return codi;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public String getContrasenya() {
		return contrasenya;
	}
	
	public Set<UsuariEntity> getUsuaris() {
		return usuaris;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codi == null) ? 0 : codi.hashCode());
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
		ViaFirmaUsuariEntity other = (ViaFirmaUsuariEntity) obj;
		if (codi == null) {
			if (other.codi != null)
				return false;
		} else if (!codi.equals(other.codi))
			return false;
		return true;
	}
	
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
	
	private static final long serialVersionUID = 8068272346835579789L;

}