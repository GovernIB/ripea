/**
 * 
 */
package es.caib.ripea.core.api.dto;


/**
 * Enumeració amb els possibles estats dels estat del expedient peticio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum ExpedientPeticioEstatEnumDto {
	CREAT,
	PENDENT,
	PROCESSAT_PENDENT,
	PROCESSAT_NOTIFICAT,
	REBUTJAT
}