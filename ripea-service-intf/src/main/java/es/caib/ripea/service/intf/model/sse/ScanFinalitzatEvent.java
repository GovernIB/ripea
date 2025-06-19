package es.caib.ripea.service.intf.model.sse;

import java.io.Serializable;

import es.caib.ripea.service.intf.dto.DigitalitzacioResultatDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class ScanFinalitzatEvent implements Serializable {
	private static final long serialVersionUID = -1389296605968649223L;
	private final Long expedientId;
	private final DigitalitzacioResultatDto resposta;
}
