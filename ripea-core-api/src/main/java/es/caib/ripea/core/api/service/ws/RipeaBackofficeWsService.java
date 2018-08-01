/**
 * 
 */
package es.caib.ripea.core.api.service.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;

import es.caib.ripea.core.api.registre.RegistreAnotacio;


/**
 * Declaració dels mètodes per al servei per a processar anotacions
 * de registre mitjançant una aplicació externa.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@WebService(
		name = "RipeaBackoffice",
		targetNamespace = "http://www.caib.es/ripea/ws/backoffice")
public interface RipeaBackofficeWsService {

	/**
	 * Processa una anotació de registre d'entrada.
	 * 
	 * @param registreEntrada
	 *            Dades de l'anotació al registre d'entrada.
	 * @return el resultat de processar l'anotació.
	 */
	@WebMethod
	public RipeaBackofficeResultatProces processarAnotacio(
			@WebParam(name="registreEntrada") @XmlElement(required=true) RegistreAnotacio registreEntrada);

}
