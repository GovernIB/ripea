/**
 * 
 */
package es.caib.ripea.service.intf.dto;

/**
 * Estat del registre d'una notificació.
 * Equivalent a Notib NotificacioRegistreEstatEnumDto.
 */
public enum RegistreEstatEnumDto {
	VALID,
	RESERVA,
	PENDENT,
	OFICI_EXTERN,
	OFICI_INTERN,
	OFICI_ACCEPTAT,
	DISTRIBUIT,
	ANULAT,
	RECTIFICAT,
	REBUTJAT,
	REENVIAT,
	DISTRIBUINT,
	OFICI_SIR
}
