/**
 * 
 */
package es.caib.ripea.core.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Classe de model de dades que conté la informació d'un usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "ipa_usuari")
public class UsuariEntity implements Serializable {

	@Id
	@Column(name = "codi", length = 64, nullable = false)
	private String codi;
	@Column(name = "nom", length = 200)
	private String nom;
	@Column(name = "nif", length = 9, nullable = false)
	private String nif;
	@Column(name = "email", length = 200)
	private String email;
	@Column(name="idioma", length = 2)
	private String idioma;
	@Column(name = "inicialitzat")
	private boolean inicialitzat = false;
	@ManyToMany(
			cascade = CascadeType.ALL,
			fetch = FetchType.EAGER)
	@JoinTable(
			name = "ipa_usuari_viafirma_ripea",
			joinColumns = {@JoinColumn(name = "ripea_user_codi")},
			inverseJoinColumns = {@JoinColumn(name = "viafirma_user_codi")})
	private Set<ViaFirmaUsuariEntity> viaFirmaUsuaris = new HashSet<ViaFirmaUsuariEntity>();
	
	@Version
	private long version = 0;



	public String getCodi() {
		return codi;
	}
	public String getNom() {
		return nom;
	}
	public String getNif() {
		return nif;
	}
	public String getEmail() {
		return email;
	}
	public String getIdioma() {
		return idioma;
	}
	public boolean isInicialitzat() {
		return inicialitzat;
	}
	public Set<ViaFirmaUsuariEntity> getViaFirmaUsuaris() {
		return viaFirmaUsuaris;
	}
	
	public void update(
			String nom,
			String nif,
			String email) {
		this.nom = nom;
		this.nif = nif;
		this.email = email;
		this.inicialitzat = true;
	}
	
	public void update(
			String idioma) {
		this.idioma = idioma;
	}

	/**
	 * Obté el Builder per a crear objectes de tipus Usuari.
	 * 
	 * @param codi
	 *            El codi de l'usuari.
	 * @param nom
	 *            El nom de l'usuari.
	 * @param nif
	 *            El nif de l'usuari.
	 * @param email
	 *            L'areça de correu electrònic de l'usuari.
	 * @return Una nova instància del Builder.
	 */
	public static Builder getBuilder(
			String codi,
			String nom,
			String nif,
			String email,
			String idioma) {
		return new Builder(
				codi,
				nom,
				nif,
				email,
				idioma);
	}

	/**
	 * Builder per a crear noves instàncies d'aquesta entitat.
	 * 
	 * @author Limit Tecnologies <limit@limit.es>
	 */
	public static class Builder {
		UsuariEntity built;
		Builder(String codi,
				String nom,
				String nif,
				String email,
				String idioma) {
			built = new UsuariEntity();
			built.codi = codi;
			built.nom = nom;
			built.nif = nif;
			built.email = email;
			built.idioma = idioma;
			built.inicialitzat = true;
		}
		public UsuariEntity build() {
			return built;
		}
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
		UsuariEntity other = (UsuariEntity) obj;
		if (codi == null) {
			if (other.codi != null)
				return false;
		} else if (!codi.equals(other.codi))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -6657066865382086237L;

}
