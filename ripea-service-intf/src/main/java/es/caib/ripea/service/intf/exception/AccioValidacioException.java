package es.caib.ripea.service.intf.exception;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import es.caib.ripea.service.intf.base.exception.ActionExecutionException;

/**
 * Error en l'execució d'una acció per dades del formulari que no són vàlides i que només es poden detectar en executar
 * l'acció (per exemple, la validació d'un sistema extern). L'API REST la retorna com un error de validació (HTTP 422) amb
 * els errors per camp (RipeaResourceExceptionHandler), de manera que el formulari REACT marca els camps.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class AccioValidacioException extends ActionExecutionException {

	/** Missatges d'error per nom del camp del formulari de l'acció. */
	private final LinkedHashMap<String, List<String>> errorsCamps = new LinkedHashMap<>();

	public AccioValidacioException(
			Class<?> resourceClass,
			Serializable id,
			String code,
			String message,
			Map<String, List<String>> errorsCamps) {
		super(resourceClass, id, code, message);
		if (errorsCamps != null) {
			errorsCamps.forEach((camp, errors) -> this.errorsCamps.put(camp, new ArrayList<>(errors)));
		}
	}

	public Map<String, List<String>> getErrorsCamps() {
		return errorsCamps;
	}

}
