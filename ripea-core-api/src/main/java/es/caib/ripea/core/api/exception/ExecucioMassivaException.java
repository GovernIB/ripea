/**
 * 
 */
package es.caib.ripea.core.api.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

import es.caib.ripea.core.api.dto.ContingutTipusEnumDto;

/**
 * Excepció que es llança si hi ha algun error durant l'execució
 * de les accions massives.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class ExecucioMassivaException extends RuntimeException {

	private String message = "";
	
	public ExecucioMassivaException(
			Long contingutId, 
			String contingutNom,
			ContingutTipusEnumDto contingutTipus, 
			Long execucioMassivaId,
			Long execucioMassivaContingutId,
			Throwable cause) {
		
		String finalMessage = "Error al executar la acció massiva (";
		if (contingutId != null)
			finalMessage += "contingutId: " + contingutId + ", ";
		if (contingutNom != null)
			finalMessage += "contingutNom: " + contingutNom + ", ";
		if (contingutTipus != null)
			finalMessage += "contingutTipus: " + contingutTipus + ", ";
		if (execucioMassivaId != null)
			finalMessage += "execucioMassivaId: " + execucioMassivaId + ", ";
		if (execucioMassivaContingutId != null)
			finalMessage += "execucioMassivaContingutId: " + execucioMassivaContingutId;
		finalMessage += ") ===> \r\n";
		StringWriter out = new StringWriter();
		cause.printStackTrace(new PrintWriter(out));
		finalMessage += out.toString();
		
		message = finalMessage;
	}

	public String getMessage() {
		return message;
	}

	
}
