/**
 * 
 */
package es.caib.ripea.service.intf.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * Informació d'un meta-document d'un meta-expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class MetaExpedientMetaDocumentDto implements Serializable {

	private Long id;
	private MetaDocumentDto metaDocument;
	private MultiplicitatEnumDto multiplicitat;
	private boolean readOnly;
	private int ordre;



	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public MetaDocumentDto getMetaDocument() {
		return metaDocument;
	}
	public void setMetaDocument(MetaDocumentDto metaDocument) {
		this.metaDocument = metaDocument;
	}
	public MultiplicitatEnumDto getMultiplicitat() {
		return multiplicitat;
	}
	public void setMultiplicitat(MultiplicitatEnumDto multiplicitat) {
		this.multiplicitat = multiplicitat;
	}
	public boolean isReadOnly() {
		return readOnly;
	}
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	public int getOrdre() {
		return ordre;
	}
	public void setOrdre(int ordre) {
		this.ordre = ordre;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
