package es.caib.ripea.service.intf.model.sse;

import java.io.Serializable;

import es.caib.ripea.service.intf.dto.DocumentNotificacioEstatEnumDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Event que notifica als clients subscrits a un expedient que ha canviat l'estat d'una de les
 * seves notificacions (consulta o callback de NOTIB).
 *
 * Viatja com a senyal, igual que {@link FirmaEstatCanviatEvent}: el client refresca les graelles
 * afectades (la columna notificacioEstat de les remeses i les icones de notificació del contingut)
 * i és el servidor qui recalcula els camps derivats (estat, error, errorDescripcio...).
 *
 * @author RIPEA
 */
@Builder
@Getter
@AllArgsConstructor
public class NotificacioEstatCanviatEvent implements Serializable {
	private static final long serialVersionUID = 7841203365498712334L;
	private final Long expedientId;
	private final Long documentId;
	private final Long notificacioId;
	private final DocumentNotificacioEstatEnumDto estat;
}
