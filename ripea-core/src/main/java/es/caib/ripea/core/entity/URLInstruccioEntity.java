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

import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.ripea.core.audit.RipeaAuditable;

/**
 * Classe del model de dades que representa una url d'instrucció.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "ipa_url_instruccio")
@EntityListeners(AuditingEntityListener.class)
public class URLInstruccioEntity extends RipeaAuditable<Long> {

	@Column(name = "codi")
	private String codi;
	@Column(name = "nom")
	private String nom;
	@Column(name = "descripcio")
	private String descripcio;
	@Column(name = "url")
	private String url;
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "entitat_id")
	@ForeignKey(name = "ipa_url_instruccion_ent_fk")
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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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
			String url) {
		this.codi = codi;
		this.nom = nom;
		this.descripcio = descripcio;
		this.url = url;
	}

	public static Builder getBuilder(
			String codi,
			String nom,
			String descripcio,
			String url,
			EntitatEntity entitat) {
		return new Builder(
				codi,
				nom,
				descripcio,
				url,
				entitat);
	}
	public static class Builder {
		URLInstruccioEntity built;
		Builder(
				String codi,
				String nom,
				String descripcio,
				String url,
				EntitatEntity entitat) {
			built = new URLInstruccioEntity();
			built.codi = codi;
			built.nom = nom;
			built.descripcio = descripcio;
			built.url = url;
			built.entitat = entitat;
		}
		public URLInstruccioEntity build() {
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
		URLInstruccioEntity other = (URLInstruccioEntity) obj;
		if (codi == null) {
			if (other.codi != null)
				return false;
		}
		return true;
	}

	private static final long serialVersionUID = 2014674987219776389L;

}