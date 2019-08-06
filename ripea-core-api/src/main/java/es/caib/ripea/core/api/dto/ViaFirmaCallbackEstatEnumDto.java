/**
 * 
 */
package es.caib.ripea.core.api.dto;

/**
 * Enumeració amb els possibles estats retornats pel
 * callback de portafirmes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum ViaFirmaCallbackEstatEnumDto {
	RESPONSED,
	REJECTED,
	WAITING,
	WAITING_CHECK,
	ERROR,
	EXPIRED
}
