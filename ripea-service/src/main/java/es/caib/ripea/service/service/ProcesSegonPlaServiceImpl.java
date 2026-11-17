package es.caib.ripea.service.service;

import org.springframework.stereotype.Service;

import es.caib.ripea.service.helper.ProcesSegonPlaHelper;
import es.caib.ripea.service.intf.dto.ProcesSegonPlaDto;
import es.caib.ripea.service.intf.service.ProcesSegonPlaService;
import lombok.RequiredArgsConstructor;

/**
 * Consulta del progrés dels processos en segon pla (veure {@link ProcesSegonPlaHelper}).
 *
 * L'estat és en memòria: no cal transacció.
 */
@Service
@RequiredArgsConstructor
public class ProcesSegonPlaServiceImpl implements ProcesSegonPlaService {

	private final ProcesSegonPlaHelper procesSegonPlaHelper;

	@Override
	public ProcesSegonPlaDto consultar(String operacioId) {
		return procesSegonPlaHelper.consultar(operacioId);
	}

}
