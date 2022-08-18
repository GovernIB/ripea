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
	PROCESSAT_NOTIFICAT, // this state is not longer required, instead of PROCESSAT_PENDENT and PROCESSAT_NOTIFICAT there can be only one state named PROCESSAT or ACCEPTAT because from now we have field ExpedientPeticioEntity.pendentCanviEstatDistribucio, that tells if the change of state was notified to distribucio
	REBUTJAT
}
