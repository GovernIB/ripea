/**
 * 
 */
package es.caib.ripea.core.api.dto;


/**
 * Enumeració amb els possibles tipus d'accions de log.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum LogTipusEnumDto {
	CREACIO,
	CONSULTA,
	MODIFICACIO,
	ELIMINACIO,
	RECUPERACIO,
	ELIMINACIODEF,
	ACTIVACIO,
	DESACTIVACIO,
	AGAFAR,
	ALLIBERAR,
	COPIA,
	MOVIMENT,
	
	REENVIAMENT,
	PROCESSAMENT,
	TANCAMENT,
	REOBERTURA,
	ACUMULACIO,
	DISGREGACIO,
	PER_DEFECTE,
	
	// NOTIFICACIONS
	NOTIFICACIO_ENVIADA,
//	NOTIFICACIO_ENTREGADA,
	NOTIFICACIO_REBUTJADA,
	NOTIFICACIO_CERTIFICADA, // notificacio feta
//	NOTIFICACIO_REINTENT,
	
	// Portafirmes
	PFIRMA_ENVIAMENT,  
	PFIRMA_CANCELACIO,
	PFIRMA_CALLBACK,
	PFIRMA_FIRMA,
	PFIRMA_REBUIG,
	PFIRMA_REINTENT,

	// Via firma
	VFIRMA_ENVIAMENT,
	VFIRMA_CANCELACIO,
	VFIRMA_CALLBACK,
	VFIRMA_FIRMA,
	VFIRMA_REBUIG,
	VFIRMA_ERROR,
	VFIRMA_WAITING_CHECK,
	VFIRMA_EXPIRED,
	VFIRMA_REINTENT,
	
	// Firma servidor
	SFIRMA_FIRMA, 

	// Firma Applet - navegador (pasarela)
	FIRMA_CLIENT,
	
	ARXIU_CSV,
	ARXIU_CUSTODIAT,
	CUSTODIA_CANCELACIO,

	// Log una vegada s'ha validat la firma
	DOC_FIRMAT,

	CANVI_ESTAT
}
