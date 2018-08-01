/**
 * 
 */
package es.caib.ripea.core.api.service.ws;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;

import es.caib.ripea.core.api.registre.RegistreAnotacio;


/**
 * Declaració dels mètodes per al servei d'enviament de contingut a
 * bústies de RIPEA v1.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@WebService(
		name = "BustiaV1",
		targetNamespace = "http://www.caib.es/ripea/ws/v1/bustia")
public interface BustiaV1WsService {

	/**
	 * Envia una anotació de registre d'entrada a la bústia per defecte
	 * associada amb la unitat administrativa.
	 * 
	 * @param entitat
	 *            Codi de l'entitat.
	 * @param unitatAdministrativa
	 *            Codi de la unitat administrativa.
	 * @param registreEntrada
	 *            Dades de l'anotació al registre d'entrada.
	 */
	//@PreAuthorize("hasRole('IPA_BSTWS')")
	public void enviarAnotacioRegistreEntrada(
			@WebParam(name="entitat") @XmlElement(required=true) String entitat,
			@WebParam(name="unitatAdministrativa") @XmlElement(required=true) String unitatAdministrativa,
			@WebParam(name="registreEntrada") @XmlElement(required=true) RegistreAnotacio registreEntrada);

	/**
	 * Envia una anotació de registre d'entrada a la bústia per defecte
	 * associada amb la unitat administrativa.
	 * 
	 * @param entitat
	 *            Codi de l'entitat.
	 * @param unitatAdministrativa
	 *            Codi de la unitat administrativa.
	 * @param referenciaDocument
	 *            Referència per a consultar el document.
	 */
	//@PreAuthorize("hasRole('IPA_BSTWS')")
	public void enviarDocument(
			@WebParam(name="entitat") @XmlElement(required=true) String entitat,
			@WebParam(name="unitatAdministrativa") @XmlElement(required=true) String unitatAdministrativa,
			@WebParam(name="referenciaDocument") @XmlElement(required=true) String referenciaDocument);

	/**
	 * Envia una anotació de registre d'entrada a la bústia per defecte
	 * associada amb la unitat administrativa.
	 * 
	 * @param entitat
	 *            Codi de l'entitat.
	 * @param unitatAdministrativa
	 *            Codi de la unitat administrativa.
	 * @param referenciaDocument
	 *            Referència per a consultar l'expedient.
	 */
	//@PreAuthorize("hasRole('IPA_BSTWS')")
	public void enviarExpedient(
			@WebParam(name="entitat") @XmlElement(required=true) String entitat,
			@WebParam(name="unitatAdministrativa") @XmlElement(required=true) String unitatAdministrativa,
			@WebParam(name="referenciaExpedient") @XmlElement(required=true) String referenciaExpedient);

}
