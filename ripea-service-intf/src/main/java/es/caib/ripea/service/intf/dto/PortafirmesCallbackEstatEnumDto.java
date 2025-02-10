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
public enum PortafirmesCallbackEstatEnumDto {
	PAUSAT,
	INICIAT,
	FIRMAT,
	REBUTJAT,
	PARCIAL
}
