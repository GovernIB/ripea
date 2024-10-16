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

	private ErrorsValidacioTipusEnumDto tipusValidacio;
	private MetaDadaDto metaDada;
	private MetaDocumentDto metaDocument;
	private MultiplicitatEnumDto multiplicitat;
	private boolean documentsWithoutMetaDocument;
	private boolean withNotificacionsNoFinalitzades;
	private boolean expedientWithoutInteressats;
	
	public ValidacioErrorDto(
			MetaDadaDto metaDada,
			MultiplicitatEnumDto multiplicitat) {
		this.metaDada = metaDada;
		this.multiplicitat = multiplicitat;
		this.tipusValidacio = ErrorsValidacioTipusEnumDto.MULTIPLICITAT;
	}
	public ValidacioErrorDto(
			MetaDocumentDto metaDocument,
			MultiplicitatEnumDto multiplicitat,
			ErrorsValidacioTipusEnumDto tipus) {
		this.tipusValidacio = tipus;
		switch (tipus) {
			case MULTIPLICITAT:
				this.metaDocument = metaDocument;
				this.multiplicitat = multiplicitat;
				break;
			case METADOCUMENT:
				this.documentsWithoutMetaDocument = true;
				break;
			case NOTIFICACIONS:
				this.withNotificacionsNoFinalitzades = true;
				break;
			case INTERESSATS:
				this.expedientWithoutInteressats = true;
				break;
		}
	}

	public ErrorsValidacioTipusEnumDto getTipusValidacio() {
		return tipusValidacio;
	}

	public void setTipusValidacio(ErrorsValidacioTipusEnumDto tipusValidacio) {
		this.tipusValidacio = tipusValidacio;
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
	public boolean isWithNotificacionsNoFinalitzades() {
		return withNotificacionsNoFinalitzades;
	}
	public boolean isExpedientWithoutInteressats() {
		return expedientWithoutInteressats;
	}

	private static final long serialVersionUID = -139254994389509932L;

}
