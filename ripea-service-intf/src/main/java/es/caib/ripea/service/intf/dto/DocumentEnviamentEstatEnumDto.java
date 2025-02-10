/**
 * 
 */
package es.caib.ripea.service.intf.dto;


/**
 * Enumeració amb els possibles estats d'un enviament d'un
 * document a un sistema extern.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum DocumentEnviamentEstatEnumDto {
	PENDENT,
	ENVIAT,
	PROCESSAT,
	REBUTJAT,
	CANCELAT
}
