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
@Table(name = "ipa_tipus_documental")
@EntityListeners(AuditingEntityListener.class)
public class TipusDocumentalEntity extends RipeaAuditable<Long> {

	@Column(name = "codi", length = 64, nullable = false)
	private String codi;
//	@Column(name = "codi_especific", length = 64)
//	private String codiEspecific;
	@Column(name = "nom", length = 256, nullable = false)
	private String nomEspanyol;
	@Column(name = "nom_catala", length = 256)
	private String nomCatala;
	
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "entitat_id")
	@ForeignKey(name = "ipa_entitat_tipus_doc_fk")
	protected EntitatEntity entitat;
	
	public String getCodi() {
		return codi;
	}

	public String getNomEspanyol() {
		return nomEspanyol;
	}

	public EntitatEntity getEntitat() {
		return entitat;
	}

	public String getNomCatala() {
		return nomCatala;
	}

//	public String getCodiEspecific() {
//		return codiEspecific;
//	}

	public void update(
			String codi,
			String nom, 
			String nomCatala, 
			String codiEspecific) {
		this.codi = codi;
		this.nomEspanyol = nom;
		this.nomCatala = nomCatala;
//		this.codiEspecific = codiEspecific;
	}

	public static Builder getBuilder(
			String codi,
			String nom,
			EntitatEntity entitat, 
			String nomCatala, 
			String codiEspecific) {
		return new Builder(
				codi,
				nom,
				entitat, 
				nomCatala, 
				codiEspecific);
	}
	public static class Builder {
		TipusDocumentalEntity built;
		Builder(
				String codi,
				String nom,
				EntitatEntity entitat,
				String nomCatala, 
				String codiEspecific) {
			built = new TipusDocumentalEntity();
			built.codi = codi;
			built.nomEspanyol = nom;
			built.entitat = entitat;
			built.nomCatala = nomCatala;
//			built.codiEspecific = codiEspecific;
			
		}
		public TipusDocumentalEntity build() {
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
		TipusDocumentalEntity other = (TipusDocumentalEntity) obj;
		if (codi == null) {
			if (other.codi != null)
				return false;
		}
		return true;
	}

	private static final long serialVersionUID = -2299453443943600172L;

}