package es.caib.ripea.service.intf.model.sse;

import es.caib.ripea.service.intf.dto.PortafirmesFluxRespostaDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class CreacioFluxFinalitzatEvent {
	private final Long expedientId;
	private final PortafirmesFluxRespostaDto fluxCreat;
}
