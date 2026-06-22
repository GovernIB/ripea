/**
 * 
 */
package es.caib.ripea.plugin.notificacio;

import java.io.Serializable;

/**
 * Enumerat que indica l'estat del registre/assentament d'un enviament.
 * Equivalent a Notib es.caib.notib.client.domini.RegistreEstatEnum.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum RegistreEstat implements Serializable {
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
