/**
 * 
 */
package es.caib.ripea.core.api.exception;

import es.caib.ripea.core.api.dto.ContingutTipusEnumDto;

/**
 * Excepció que es llança si hi ha algun error durant l'execució
 * de les accions massives.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class ExecucioMassivaException extends RuntimeException {

	private Long execucioMassivaId;
	private Long execucioMassivaContingutId;
	
	public ExecucioMassivaException(
			Long contingutId, 
			String contingutNom,
			ContingutTipusEnumDto contingutTipus, 
			Long execucioMassivaId,
			Long execucioMassivaContingutId,
			String message,
			Throwable cause) {
		super(getCommonMessage(
				contingutId, 
				contingutNom, 
				contingutTipus, 
				execucioMassivaId, 
				execucioMassivaContingutId,
				message),
				cause);
		this.execucioMassivaId = execucioMassivaId;
		this.execucioMassivaContingutId = execucioMassivaContingutId;
	}
	
	private static String getCommonMessage (
			Long contingutId, 
			String contingutNom,
			ContingutTipusEnumDto contingutTipus, 
			Long execucioMassivaId,
			Long execucioMassivaContingutId,
			String message) {
		String finalMessage = " ===> ";
		finalMessage += (message != null ? message : "");
		if (contingutId != null)
			finalMessage += " contingutId: " + contingutId + ".";
		if (contingutNom != null)
			finalMessage += " contingutNom: " + contingutNom + ".";
		if (contingutTipus != null)
			finalMessage += " contingutTipus: " + contingutTipus + ".";
		if (execucioMassivaId != null)
			finalMessage += " execucioMassivaId: " + execucioMassivaId + ".";
		if (execucioMassivaContingutId != null)
			finalMessage += " execucioMassivaContingutId: " + execucioMassivaContingutId;
		
		return finalMessage;
	}

	public Long getExecucioMassivaId() {
		return execucioMassivaId;
	}
	
	public Long getExecucioMassivaContingutId() {
		return execucioMassivaContingutId;
	}
	
	
}
