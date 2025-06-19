package es.caib.ripea.service.intf.model.sse;

import java.io.Serializable;

import es.caib.ripea.service.intf.dto.FirmaResultatDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class FirmaFinalitzadaEvent implements Serializable {
	private static final long serialVersionUID = 3640806572292485023L;
	private final Long expedientId;
	private final FirmaResultatDto firmaResultat;
}
