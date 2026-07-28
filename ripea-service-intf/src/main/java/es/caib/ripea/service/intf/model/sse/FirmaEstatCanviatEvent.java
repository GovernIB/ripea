package es.caib.ripea.service.intf.model.sse;

import java.io.Serializable;

import es.caib.ripea.service.intf.dto.DocumentEstatEnumDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Event que notifica als clients subscrits a un expedient que ha canviat l'estat de firma
 * d'un dels seus documents (callback del portafirmes).
 *
 * Viatja com a senyal: només porta els identificadors i l'estat resultant. El client reacciona
 * refrescant la graella de contingut, de manera que és el servidor qui recalcula tots els camps
 * dels quals depenen les icones (estat, gesDocFirmatId, errorEnviamentPortafirmes,
 * validacioFirmaCorrecte...) i no cal duplicar-ne aquí el contracte.
 *
 * @author RIPEA
 */
@Builder
@Getter
@AllArgsConstructor
public class FirmaEstatCanviatEvent implements Serializable {
	private static final long serialVersionUID = -5623145897412360051L;
	private final Long expedientId;
	private final Long documentId;
	private final DocumentEstatEnumDto estat;
}
