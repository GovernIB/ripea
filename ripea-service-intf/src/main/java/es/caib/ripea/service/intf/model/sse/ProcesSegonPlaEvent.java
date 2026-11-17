package es.caib.ripea.service.intf.model.sse;

import java.io.Serializable;

import es.caib.ripea.service.intf.dto.ElementTipusEnumDto;
import es.caib.ripea.service.intf.dto.ProcesSegonPlaEstatEnumDto;
import es.caib.ripea.service.intf.dto.ProcesSegonPlaTipusEnumDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Event que avisa que un procés en segon pla ha començat o ha acabat sobre un element (p.ex. el tancament
 * d'un expedient).
 *
 * Viatja com a senyal: els clients que estan veient l'element el tornen a demanar, i és el servidor qui
 * recalcula el bloqueig (procesSegonPla, potModificar...). El progrés detallat no va per aquí: el consulta
 * l'usuari que ha llançat el procés (ProcesSegonPlaService).
 *
 * @author RIPEA
 */
@Builder
@Getter
@AllArgsConstructor
public class ProcesSegonPlaEvent implements Serializable {
	private static final long serialVersionUID = 4127580593360176734L;
	private final ElementTipusEnumDto elementTipus;
	private final Long elementId;
	private final String operacioId;
	private final ProcesSegonPlaTipusEnumDto tipus;
	private final ProcesSegonPlaEstatEnumDto estat;
}
