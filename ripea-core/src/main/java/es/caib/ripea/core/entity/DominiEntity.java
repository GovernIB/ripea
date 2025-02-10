/**
 *
 */
package es.caib.ripea.core.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.ripea.core.audit.RipeaAuditable;

/**
 * Classe del model de dades que representa un meta-document.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "ipa_domini")
@EntityListeners(AuditingEntityListener.class)
public class DominiEntity extends RipeaAuditable<Long> {

	@Column(name = "codi")
	private String codi;
	@Column(name = "nom")
	private String nom;
	@Column(name = "descripcio")
	private String descripcio;
	@Column(name = "consulta")
	private String consulta;
	@Column(name = "cadena")
	private String cadena;
	@Column(name = "contrasenya")
	private String contrasenya;

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "entitat_id")
	@ForeignKey(name = "ipa_entitat_tipus_doc_fk")
	protected EntitatEntity entitat;

	public String getCodi() {
		return codi;
	}

	public void setCodi(String codi) {
		this.codi = codi;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getDescripcio() {
		return descripcio;
	}

	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}

	public String getConsulta() {
		return consulta;
	}

	public void setConsulta(String consulta) {
		this.consulta = consulta;
	}

	public String getCadena() {
		return cadena;
	}

	public void setCadena(String cadena) {
		this.cadena = cadena;
	}

	public String getContrasenya() {
		return contrasenya;
	}

	public void setContrasenya(String contrasenya) {
		this.contrasenya = contrasenya;
	}

	public EntitatEntity getEntitat() {
		return entitat;
	}

	public void setEntitat(EntitatEntity entitat) {
		this.entitat = entitat;
	}

	public void update(
			String codi,
			String nom,
			String descripcio,
			String consulta,
			String cadena,
			String contrasenya) {
		this.codi = codi;
		this.nom = nom;
		this.descripcio = descripcio;
		this.consulta = consulta;
		this.cadena = cadena;
		this.contrasenya = contrasenya;
	}

	public static Builder getBuilder(
			String codi,
			String nom,
			String descripcio,
			String consulta,
			String cadena,
			String contrasenya,
			EntitatEntity entitat) {
		return new Builder(
				codi,
				nom,
				descripcio,
				consulta,
				cadena,
				contrasenya,
				entitat);
	}
	public static class Builder {
		DominiEntity built;
		Builder(
				String codi,
				String nom,
				String descripcio,
				String consulta,
				String cadena,
				String contrasenya,
				EntitatEntity entitat) {
			built = new DominiEntity();
			built.codi = codi;
			built.nom = nom;
			built.descripcio = descripcio;
			built.consulta = consulta;
			built.cadena = cadena;
			built.contrasenya = contrasenya;
			built.entitat = entitat;
		}
		public DominiEntity build() {
			return built;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((codi == null) ? 0 : codi.hashCode());
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
		DominiEntity other = (DominiEntity) obj;
		if (codi == null) {
			if (other.codi != null)
				return false;
		}
		return true;
	}

	private static final long serialVersionUID = 1168453230252786190L;

}