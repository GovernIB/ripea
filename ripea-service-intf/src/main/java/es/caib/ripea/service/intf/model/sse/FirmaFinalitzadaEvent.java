package es.caib.ripea.service.intf.model.sse;

import es.caib.ripea.service.intf.dto.FirmaResultatDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class FirmaFinalitzadaEvent {
	private final Long expedientId;
	private final FirmaResultatDto firmaResultat;
}
