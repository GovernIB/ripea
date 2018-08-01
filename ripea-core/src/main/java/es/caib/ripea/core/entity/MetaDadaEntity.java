/**
 * 
 */
package es.caib.ripea.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.ripea.core.api.dto.MetaDadaTipusEnumDto;
import es.caib.ripea.core.api.dto.MultiplicitatEnumDto;
import es.caib.ripea.core.audit.RipeaAuditable;

/**
 * Classe del model de dades que representa una meta-dada.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(	name = "ipa_metadada",
		uniqueConstraints = {
				@UniqueConstraint(columnNames = {
						"entitat_id",
						"codi"})})
@EntityListeners(AuditingEntityListener.class)
public class MetaDadaEntity extends RipeaAuditable<Long> {

	@Column(name = "codi", length = 64, nullable = false)
	private String codi;
	@Column(name = "nom", length = 256, nullable = false)
	private String nom;
	@Column(name = "descripcio", length = 1024)
	private String descripcio;
	@Column(name = "tipus", nullable = false)
	@Enumerated(EnumType.STRING)
	private MetaDadaTipusEnumDto tipus;
	@Column(name = "global_expedient")
	private boolean globalExpedient;
	@Column(name = "global_document")
	private boolean globalDocument;
	@Column(name = "global_multiplicitat")
	@Enumerated(EnumType.STRING)
	private MultiplicitatEnumDto globalMultiplicitat;
	@Column(name = "global_readonly")
	private boolean globalReadOnly;
	@Column(name = "activa")
	private boolean activa;
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "entitat_id")
	@ForeignKey(name = "ipa_entitat_metadada_fk")
	private EntitatEntity entitat;
	@Version
	private long version = 0;



	public String getCodi() {
		return codi;
	}
	public String getNom() {
		return nom;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public MetaDadaTipusEnumDto getTipus() {
		return tipus;
	}
	public boolean isGlobalExpedient() {
		return globalExpedient;
	}
	public boolean isGlobalDocument() {
		return globalDocument;
	}
	public MultiplicitatEnumDto getGlobalMultiplicitat() {
		return globalMultiplicitat;
	}
	public boolean isGlobalReadOnly() {
		return globalReadOnly;
	}
	public boolean isActiva() {
		return activa;
	}
	public EntitatEntity getEntitat() {
		return entitat;
	}
	public long getVersion() {
		return version;
	}

	public void update(
			String codi,
			String nom,
			String descripcio,
			MetaDadaTipusEnumDto tipus,
			boolean globalExpedient,
			boolean globalDocument,
			MultiplicitatEnumDto globalMultiplicitat,
			boolean globalReadOnly,
			EntitatEntity entitat) {
		this.codi = codi;
		this.nom = nom;
		this.descripcio = descripcio;
		this.tipus = tipus;
		this.globalExpedient = globalExpedient;
		this.globalDocument = globalDocument;
		this.globalMultiplicitat = globalMultiplicitat;
		this.globalReadOnly = globalReadOnly;
		this.entitat = entitat;
	}

	public void updateActiva(boolean activa) {
		this.activa = activa;
	}

	/**
	 * Obté el Builder per a crear objectes de tipus meta-dada.
	 * 
	 * @param codi
	 *            El valor de l'atribut codi.
	 * @param nom
	 *            El valor de l'atribut nom.
	 * @param descripcio
	 *            El valor de l'atribut descripcio.
	 * @param tipus
	 *            El valor de l'atribut tipus.
	 * @param globalExpedient
	 *            El valor de l'atribut globalExpedient.
	 * @param globalDocument
	 *            El valor de l'atribut globalDocument.
	 * @param globalMultiplicitat
	 *            El valor de l'atribut globalMultiplicitat.
	 * @param globalReadOnly
	 *            El valor de l'atribut globalReadOnly.
	 * @param entitat
	 *            L'entitat a la qual pertany aquesta meta-dada.
	 * @return Una nova instància del Builder.
	 */
	public static Builder getBuilder(
			String codi,
			String nom,
			String descripcio,
			MetaDadaTipusEnumDto tipus,
			boolean globalExpedient,
			boolean globalDocument,
			MultiplicitatEnumDto globalMultiplicitat,
			boolean globalReadOnly,
			EntitatEntity entitat) {
		return new Builder(
				codi,
				nom,
				descripcio,
				tipus,
				globalExpedient,
				globalDocument,
				globalMultiplicitat,
				globalReadOnly,
				entitat);
	}

	/**
	 * Builder per a crear noves instàncies d'aquesta classe.
	 * 
	 * @author Limit Tecnologies <limit@limit.es>
	 */
	public static class Builder {
		MetaDadaEntity built;
		Builder(
				String codi,
				String nom,
				String descripcio,
				MetaDadaTipusEnumDto tipus,
				boolean globalExpedient,
				boolean globalDocument,
				MultiplicitatEnumDto globalMultiplicitat,
				boolean globalReadOnly,
				EntitatEntity entitat) {
			built = new MetaDadaEntity();
			built.codi = codi;
			built.nom = nom;
			built.descripcio = descripcio;
			built.tipus = tipus;
			built.globalExpedient = globalExpedient;
			built.globalDocument = globalDocument;
			built.globalMultiplicitat = globalMultiplicitat;
			built.globalReadOnly = globalReadOnly;
			built.entitat = entitat;
			built.activa = true;
		}
		public MetaDadaEntity build() {
			return built;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((codi == null) ? 0 : codi.hashCode());
		result = prime * result + ((entitat == null) ? 0 : entitat.hashCode());
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
		MetaDadaEntity other = (MetaDadaEntity) obj;
		if (codi == null) {
			if (other.codi != null)
				return false;
		} else if (!codi.equals(other.codi))
			return false;
		if (entitat == null) {
			if (other.entitat != null)
				return false;
		} else if (!entitat.equals(other.entitat))
			return false;
		return true;
	}

	private static final long serialVersionUID = -2299453443943600172L;

}
