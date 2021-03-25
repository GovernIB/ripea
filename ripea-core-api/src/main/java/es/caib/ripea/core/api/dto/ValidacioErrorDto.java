/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.io.Serializable;


/**
 * Informació d'un error de validació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ValidacioErrorDto implements Serializable {

	private MetaDadaDto metaDada;
	private MetaDocumentDto metaDocument;
	private MultiplicitatEnumDto multiplicitat;
	private boolean documentsWithoutMetaDocument;
	
	public ValidacioErrorDto(
			MetaDadaDto metaDada,
			MultiplicitatEnumDto multiplicitat) {
		this.metaDada = metaDada;
		this.multiplicitat = multiplicitat;
	}
	public ValidacioErrorDto(
			MetaDocumentDto metaDocument,
			MultiplicitatEnumDto multiplicitat) {
		this.metaDocument = metaDocument;
		this.multiplicitat = multiplicitat;
	}
	public ValidacioErrorDto(
			boolean documentsWithoutMetaDocument) {
		this.documentsWithoutMetaDocument = documentsWithoutMetaDocument;
	}

	public MetaDadaDto getMetaDada() {
		return metaDada;
	}
	public void setMetaDada(MetaDadaDto metaDada) {
		this.metaDada = metaDada;
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
	public boolean isDocumentsWithoutMetaDocument() {
		return documentsWithoutMetaDocument;
	}
	public boolean isErrorMetaDada() {
		return metaDada != null;
	}
	public boolean isErrorMetaDocument() {
		return metaDocument != null;
	}

	private static final long serialVersionUID = -139254994389509932L;

}
