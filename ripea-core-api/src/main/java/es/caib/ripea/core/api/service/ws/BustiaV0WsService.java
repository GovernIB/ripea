/**
 * 
 */
package es.caib.ripea.core.api.service.ws;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;


/**
 * Declaració dels mètodes per al servei d'enviament de contingut a
 * bústies de RIPEA v0.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@WebService(
		name = "BustiaV0",
		targetNamespace = "http://www.caib.es/ripea/ws/v0/bustia")
public interface BustiaV0WsService {

	public enum BustiaContingutTipus {
		EXPEDIENT,
		DOCUMENT,
		REGISTRE_ENTRADA
	}

	/**
	 * Envia un contingut a la bústia per defecte de la unitat administrativa.
	 * 
	 * @param entitat
	 *            Codi de l'entitat.
	 * @param unitatAdministrativa
	 *            Codi de la unitat administrativa.
	 * @param tipus
	 *            Codi de l'usuari a cercar.
	 * @param referencia
	 *            Referència per a consultar el contingut i posar-lo dins la bústia.
	 */
	//@PreAuthorize("hasRole('IPA_BSTWS')")
	public void enviarContingut(
			@WebParam(name="entitat") @XmlElement(required=true) String entitat,
			@WebParam(name="unitatAdministrativa") @XmlElement(required=true) String unitatAdministrativa,
			@WebParam(name="tipus") @XmlElement(required=true) BustiaContingutTipus tipus,
			@WebParam(name="referencia") @XmlElement(required=true) String referencia);

}
