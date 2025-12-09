package es.caib.ripea.service.intf.model.sse;

import java.io.Serializable;

import es.caib.ripea.service.intf.dto.FirmaResultatDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class FirmaNavegadorMassivaEvent implements Serializable {
	private static final long serialVersionUID = -1278411409359909420L;
	private final FirmaResultatDto firmaResultat;
}