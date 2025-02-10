/**
 * 
 */
package es.caib.ripea.service.intf.dto;

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
