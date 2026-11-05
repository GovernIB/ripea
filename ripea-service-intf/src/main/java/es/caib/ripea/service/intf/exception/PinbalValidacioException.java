package es.caib.ripea.service.intf.exception;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * PINBAL ha rebutjat la petició per errors de validació que corresponen a camps del formulari de la consulta. Serveix
 * perquè la interfície REACT marqui els camps amb error en lloc de mostrar només un missatge.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class PinbalValidacioException extends PinbalException {

	/** Missatges d'error per nom del camp del formulari. */
	private final LinkedHashMap<String, List<String>> errorsCamps = new LinkedHashMap<>();
	/** Missatge de PINBAL i errors que no corresponen a cap camp del formulari. */
	private final String missatgeGeneral;

	/**
	 * @param message
	 *            descripció sencera de l'error (es registra a la consulta i al monitor d'integracions).
	 * @param missatgeGeneral
	 *            missatge de PINBAL i errors sense camp, per a l'avís a l'usuari.
	 * @param errorsCamps
	 *            missatges d'error per nom del camp del formulari.
	 */
	public PinbalValidacioException(String message, String missatgeGeneral, Map<String, List<String>> errorsCamps) {
		super(message);
		this.missatgeGeneral = missatgeGeneral;
		if (errorsCamps != null) {
			errorsCamps.forEach((camp, errors) -> this.errorsCamps.put(camp, new ArrayList<>(errors)));
		}
	}

	public Map<String, List<String>> getErrorsCamps() {
		return errorsCamps;
	}

	public String getMissatgeGeneral() {
		return missatgeGeneral;
	}

}
