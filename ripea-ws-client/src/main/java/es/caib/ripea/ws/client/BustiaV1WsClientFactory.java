/**
 * 
 */
package es.caib.ripea.ws.client;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;

import es.caib.ripea.ws.v1.bustia.BustiaV1;

/**
 * Utilitat per a instanciar clients per al servei d'enviament
 * de contingut a bústies.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BustiaV1WsClientFactory {

	public static BustiaV1 getWsClient(
			URL wsdlResourceUrl,
			String endpoint,
			String userName,
			String password) throws MalformedURLException {
		return new WsClientHelper<BustiaV1>().generarClientWs(
				wsdlResourceUrl,
				endpoint,
				new QName(
						"http://www.caib.es/ripea/ws/v1/bustia",
						"BustiaV1Service"),
				userName,
				password,
				BustiaV1.class);
	}

	public static BustiaV1 getWsClient(
			String endpoint,
			String userName,
			String password) throws MalformedURLException {
		return new WsClientHelper<BustiaV1>().generarClientWs(
				endpoint,
				new QName(
						"http://www.caib.es/ripea/ws/v1/bustia",
						"BustiaV1Service"),
				userName,
				password,
				BustiaV1.class);
	}

}
