package es.caib.ripea.service.intf.model.sse;

import es.caib.ripea.service.intf.dto.DigitalitzacioResultatDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class ScanFinalitzatEvent {
	private final Long expedientId;
	private final DigitalitzacioResultatDto resposta;
}
