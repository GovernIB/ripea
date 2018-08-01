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

import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.ripea.core.api.dto.MultiplicitatEnumDto;
import es.caib.ripea.core.audit.RipeaAuditable;

/**
 * Classe del model de dades que representa una relació entre
 * meta-expedient i meta-document.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(	name="ipa_metaexpedient_metadocument",
		uniqueConstraints={@UniqueConstraint(columnNames = {"metaexpedient_id", "metadocument_id"})})
@EntityListeners(AuditingEntityListener.class)
public class MetaExpedientMetaDocumentEntity extends RipeaAuditable<Long> {

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "metaexpedient_id")
	@ForeignKey(name = "ipa_metaexp_metaexpdoc_fk")
	private MetaExpedientEntity metaExpedient;
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "metadocument_id")
	@ForeignKey(name = "ipa_metadoc_metaexpdoc_fk")
	private MetaDocumentEntity metaDocument;
	@Column(name = "multiplicitat")
	@Enumerated(EnumType.STRING)
	private MultiplicitatEnumDto multiplicitat;
	@Column(name = "readonly")
	private boolean readOnly;
	private int ordre;



	public MetaExpedientEntity getMetaExpedient() {
		return metaExpedient;
	}
	public MetaDocumentEntity getMetaDocument() {
		return metaDocument;
	}
	public MultiplicitatEnumDto getMultiplicitat() {
		return multiplicitat;
	}
	public boolean isReadOnly() {
		return readOnly;
	}
	public int getOrdre() {
		return ordre;
	}

	public void update(
			MultiplicitatEnumDto multiplicitat,
			boolean readOnly) {
		this.multiplicitat = multiplicitat;
		this.readOnly = readOnly;
	}
	public void updateOrdre(int ordre) {
		this.ordre = ordre;
	}

	/**
	 * Obté el Builder per a crear objectes de tipus meta-expedient - meta-document.
	 * 
	 * @param metaExpedient
	 *            El meta-expedient.
	 * @param metaDocument
	 *            El meta-document.
	 * @param multiplicitat
	 *            El valor de l'atribut multiplicitat.
	 * @param readOnly
	 *            El valor de l'atribut readOnly.
	 * @param ordre
	 *            El valor de l'atribut ordre.
	 * @return Una nova instància del Builder.
	 */
	public static Builder getBuilder(
			MetaExpedientEntity metaExpedient,
			MetaDocumentEntity metaDocument,
			MultiplicitatEnumDto multiplicitat,
			boolean readOnly,
			int ordre) {
		return new Builder(
				metaExpedient,
				metaDocument,
				multiplicitat,
				readOnly,
				ordre);
	}

	/**
	 * Builder per a crear noves instàncies d'aquesta classe.
	 * 
	 * @author Limit Tecnologies <limit@limit.es>
	 */
	public static class Builder {
		MetaExpedientMetaDocumentEntity built;
		Builder(
				MetaExpedientEntity metaExpedient,
				MetaDocumentEntity metaDocument,
				MultiplicitatEnumDto multiplicitat,
				boolean readOnly,
				int ordre) {
			built = new MetaExpedientMetaDocumentEntity();
			built.metaExpedient = metaExpedient;
			built.metaDocument = metaDocument;
			built.multiplicitat = multiplicitat;
			built.readOnly = readOnly;
			built.ordre = ordre;
		}
		public MetaExpedientMetaDocumentEntity build() {
			return built;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((metaDocument == null) ? 0 : metaDocument.hashCode());
		result = prime * result
				+ ((metaExpedient == null) ? 0 : metaExpedient.hashCode());
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
		MetaExpedientMetaDocumentEntity other = (MetaExpedientMetaDocumentEntity) obj;
		if (metaDocument == null) {
			if (other.metaDocument != null)
				return false;
		} else if (!metaDocument.equals(other.metaDocument))
			return false;
		if (metaExpedient == null) {
			if (other.metaExpedient != null)
				return false;
		} else if (!metaExpedient.equals(other.metaExpedient))
			return false;
		return true;
	}

	private static final long serialVersionUID = -2299453443943600172L;

}
