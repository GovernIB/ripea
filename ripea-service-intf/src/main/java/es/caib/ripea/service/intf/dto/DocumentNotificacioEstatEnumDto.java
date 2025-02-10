package es.caib.ripea.service.intf.dto;
/**
 * Enumeració amb els possibles estats d'un enviament d'un document a un sistema extern.
 */
public enum DocumentNotificacioEstatEnumDto {
	PENDENT, 
	ENVIADA, 
	REGISTRADA, 
	FINALITZADA, 
	PROCESSADA,
	ENVIADA_AMB_ERRORS,
	FINALITZADA_AMB_ERRORS
}