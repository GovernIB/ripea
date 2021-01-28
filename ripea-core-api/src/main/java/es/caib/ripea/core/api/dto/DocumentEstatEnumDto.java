/**
 * 
 */
package es.caib.ripea.core.api.dto;


/**
 * Enumeració amb els possibles estats dels documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum DocumentEstatEnumDto {
	REDACCIO,
	FIRMA_PENDENT,
	FIRMAT,
	CUSTODIAT,
	FIRMA_PENDENT_VIAFIRMA,
	DEFINITIU,
	FIRMA_PARCIAL,
	ADJUNT_FIRMAT
}
