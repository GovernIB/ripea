/**
 * 
 */
package es.caib.ripea.service.reenviament;

import javax.jws.WebService;

/**
 * Declaració dels mètodes per al servei de reenviament d'anotacions
 * de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@WebService(
		name = "Reenviament",
		serviceName = "ReenviamentService",
		portName = "ReenviamentServicePort",
		targetNamespace = "http://www.caib.es/ripea/ws/reenviament")
public interface Reenviament {

	/**
	 * Informa del reenviament d'una anotació de registre.
	 * 
	 * @param numeroAnotacion
	 *            El número de l'anotació a reenviar.
	 * @param codigoUnidadTramitacionDestino
	 *            La unitat destí de l'anotació.
	 * @return El número de la nova anotació.
	 */
	public String reenviarAnotacio(
			String numeroAnotacion,
			String codigoUnidadTramitacionDestino);

}
