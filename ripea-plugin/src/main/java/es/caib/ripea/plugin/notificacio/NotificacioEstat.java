/**
 * 
 */
package es.caib.ripea.plugin.notificacio;

import java.io.Serializable;

/**
 * Enumerat que indica l'estat de la notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum NotificacioEstat implements Serializable {
	PENDENT, 
	ENVIADA, 
	REGISTRADA, 
	FINALITZADA, 
	PROCESSADA
}
