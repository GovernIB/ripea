package es.caib.ripea.service.intf.model.sse;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Event lleuger que transporta NOMÉS els ids dels expedients als quals se'ls ha buidat la cache de validacions
 * (no porta el resultat de la validació, a diferència de {@link ErrorsValidacioChangedEvent}).
 *
 * S'envia quan un canvi a nivell de procediment (meta-document o metadada del meta-expedient) afecta tots els
 * expedients oberts d'aquell procediment, que poden ser molts. Recalcular i notificar la validació de tots ells
 * no compensa, així que el productor (ValidacioCacheEvictHelper) només publica els ids i deixa que el consumidor
 * (SseResourceController) recalculi i notifiqui via SSE NOMÉS els expedients que algú està veient en aquell
 * moment (els que tenen un emisor SSE actiu).
 */
@Builder
@Getter
@AllArgsConstructor
public class ErrorsValidacioEvictEvent implements Serializable {
	private static final long serialVersionUID = 1L;
	private final List<Long> expedientIds;
}
