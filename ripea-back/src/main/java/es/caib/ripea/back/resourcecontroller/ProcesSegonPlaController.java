package es.caib.ripea.back.resourcecontroller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.ProcesSegonPlaDto;
import es.caib.ripea.service.intf.service.ProcesSegonPlaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * Consulta del progrés dels processos en segon pla (p.ex. el tancament d'un expedient des de REACT).
 *
 * El diàleg de progrés del front la crida periòdicament fins que el procés acaba. És comuna a tots els tipus de
 * procés: l'acció de cada recurs llança el procés i retorna l'operacioId, i a partir d'aquí el seguiment és el mateix.
 *
 * @author RIPEA
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(BaseConfig.API_PATH + "/procesSegonPla")
@Tag(name = "Processos en segon pla", description = "Progrés dels processos en segon pla llançats per l'usuari")
public class ProcesSegonPlaController {

	private final ProcesSegonPlaService procesSegonPlaService;

	/**
	 * Estat complet (amb tots els passos) del procés. Respon 404 si no existeix, ja s'ha descartat de memòria o
	 * l'ha llançat un altre usuari (no es distingeix, per no revelar processos aliens).
	 */
	@GetMapping("/{operacioId}")
	public ResponseEntity<ProcesSegonPlaDto> consultar(@PathVariable String operacioId) {
		ProcesSegonPlaDto proces = procesSegonPlaService.consultar(operacioId);
		return proces != null ? ResponseEntity.ok(proces) : ResponseEntity.notFound().build();
	}

}
