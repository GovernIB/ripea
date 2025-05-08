package es.caib.ripea.service.intf.model;

import es.caib.ripea.service.intf.dto.ErrorsValidacioTipusEnumDto;
import es.caib.ripea.service.intf.dto.MultiplicitatEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Informació d'un error de validació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
@NoArgsConstructor
public class ValidacioErrorResource implements Serializable {

	private ErrorsValidacioTipusEnumDto tipusValidacio;
	private MetaDadaResource metaDada;
	private MetaDocumentResource metaDocument;
	private MultiplicitatEnumDto multiplicitat;
	private boolean documentsWithoutMetaDocument;
	private boolean withNotificacionsNoFinalitzades;
	private boolean expedientWithoutInteressats;

	public ValidacioErrorResource(
			MetaDadaResource metaDada,
			MultiplicitatEnumDto multiplicitat) {
		this.metaDada = metaDada;
		this.multiplicitat = multiplicitat;
		this.tipusValidacio = ErrorsValidacioTipusEnumDto.MULTIPLICITAT;
	}
	public ValidacioErrorResource(
			MetaDocumentResource metaDocument,
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

	private static final long serialVersionUID = -139254994389509932L;
}