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
	
	
	// TODO it would be clearer to have only 3 states:
	// 1. SIN_FIRMA - equivalent to current state REDACCIO
	// 2. FIRMA_EN_PROGRESS - equivalent to current states: FIRMA_PENDENT, FIRMA_PENDENT_VIAFIRMA, FIRMA_PARCIAL
	// 3. FIRMAT - equivalent to current states: FIRMAT, ADJUNT_FIRMAT, CUSTODIAT, DEFINITIU
	// Information about if it is saved in arxiu and what is the state of document in arxiu is in field arxiuEstat
}
