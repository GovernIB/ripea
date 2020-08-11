/**
 * 
 */
package es.caib.ripea.core.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.ripea.core.audit.RipeaAuditable;
import lombok.Getter;
import lombok.Setter;

/**
 * Classe del model de dades que representa una Entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name="ipa_entitat")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter
public class EntitatEntity extends RipeaAuditable<Long> {

	@Column(name = "codi", length = 64, nullable = false, unique = true)
	private String codi;
	@Column(name = "nom", length = 256, nullable = false)
	private String nom;
	@Column(name = "descripcio", length = 1024)
	private String descripcio;
	@Column(name = "cif", length = 9, nullable = false)
	private String cif;
	@Column(name = "unitat_arrel", length = 9, nullable = false)
	private String unitatArrel;
	@Column(name = "activa")
	private boolean activa = true;
	@OneToMany(mappedBy = "entitat", cascade = {CascadeType.ALL})
	private Set<MetaNodeEntity> metaNodes = new HashSet<MetaNodeEntity>();
	@Version
	private long version = 0;
	@Column(name = "logo_img")
	private byte[] logoImgBytes;
	@Column(name = "capsalera_color_fons", length = 7)
	private String capsaleraColorFons;
	@Column(name = "capsalera_color_lletra", length = 7)
	private String capsaleraColorLletra;
	
	public void update(
			String codi,
			String nom,
			String descripcio,
			String cif,
			String unitatArrel,
			String capsaleraColorFons,
			String capsaleraColorLletra) {
		this.codi = codi;
		this.nom = nom;
		this.descripcio = descripcio;
		this.cif = cif;
		this.unitatArrel = unitatArrel;
		this.capsaleraColorFons = capsaleraColorFons;
		this.capsaleraColorLletra = capsaleraColorLletra;
	}
	
	public void updateLogoImgBytes(
			byte[] logoImgBytes) {
		this.logoImgBytes = logoImgBytes;
	}
	
	public void updateActiva(
			boolean activa) {
		this.activa = activa;
	}

	/**
	 * Obté el Builder per a crear objectes de tipus Entitat.
	 * 
	 * @param codi
	 *            El valor de l'atribut codi.
	 * @param nom
	 *            El valor de l'atribut nom.
	 * @param descripcio
	 *            El valor de l'atribut descripcio.
	 * @param cif
	 *            El valor de l'atribut cif.
	 * @param unitatArrel
	 *            El valor de l'atribut unitatArrel.
	 * @return Una nova instància del Builder.
	 */
	public static Builder getBuilder(
			String codi,
			String nom,
			String descripcio,
			String cif,
			String unitatArrel) {
		return new Builder(
				codi,
				nom,
				descripcio,
				cif,
				unitatArrel);
	}

	/**
	 * Builder per a crear noves instàncies d'aquesta classe.
	 * 
	 * @author Josep Gayà
	 */
	public static class Builder {
		EntitatEntity built;
		Builder(
				String codi,
				String nom,
				String descripcio,
				String cif,
				String unitatArrel) {
			built = new EntitatEntity();
			built.codi = codi;
			built.nom = nom;
			built.descripcio = descripcio;
			built.cif = cif;
			built.unitatArrel = unitatArrel;
		}
		public EntitatEntity build() {
			return built;
		}
		
		public Builder logoImgBytes(byte[] logoImgBytes) {
			built.logoImgBytes = logoImgBytes;
			return this;
		}
		public Builder capsaleraColorFons(String capsaleraColorFons) {
			built.capsaleraColorFons = capsaleraColorFons;
			return this;
		}

		public Builder capsaleraColorLletra(String capsaleraColorLletra) {
			built.capsaleraColorLletra = capsaleraColorLletra;
			return this;
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
		EntitatEntity other = (EntitatEntity) obj;
		if (codi == null) {
			if (other.codi != null)
				return false;
		} else if (!codi.equals(other.codi))
			return false;
		return true;
	}

	private static final long serialVersionUID = -2299453443943600172L;

}
