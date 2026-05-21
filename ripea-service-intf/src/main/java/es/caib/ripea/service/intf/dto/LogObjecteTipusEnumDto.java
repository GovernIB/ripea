/**
 * 
 */
package es.caib.ripea.service.intf.dto;


/**
 * Enumeració amb els possibles tipus d'objectes per a adjuntar
 * amb les accions de log.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum LogObjecteTipusEnumDto {
	//LogContingutTipusEnumDto = expedient
	CONTINGUT,
	EXPEDIENT,
	CARPETA,
	DOCUMENT, //També val per LogContingutTipusEnumDto = tasca
	DADA,
	BUSTIA,
	ARXIU,
	INTERESSAT,
	TASCA,
	REGISTRE,
	RELACIO,
	NOTIFICACIO,
	PUBLICACIO,
	//LogContingutTipusEnumDto = procediment
	METAEXPEDIENT,
	METADOCUMENT,
	METADADA,
	METAESTAT,
	METATASCA,
	GRUP,
	//Generics
	ALTRES,
}
