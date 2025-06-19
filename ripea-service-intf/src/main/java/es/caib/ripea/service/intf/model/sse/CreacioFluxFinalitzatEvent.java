package es.caib.ripea.service.intf.model.sse;

import java.io.Serializable;

import es.caib.ripea.service.intf.dto.PortafirmesFluxRespostaDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class CreacioFluxFinalitzatEvent implements Serializable {
	private static final long serialVersionUID = -7159538246036007850L;
	private final Long expedientId;
	private final PortafirmesFluxRespostaDto fluxCreat;
}
