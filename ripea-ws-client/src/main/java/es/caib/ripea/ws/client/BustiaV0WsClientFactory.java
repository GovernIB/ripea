/**
 * 
 */
package es.caib.ripea.ws.client;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;

import es.caib.ripea.ws.v0.bustia.BustiaV0;

/**
 * Utilitat per a instanciar clients per al servei d'enviament
 * de contingut a bústies.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BustiaV0WsClientFactory {

	public static BustiaV0 getWsClient(
			URL wsdlResourceUrl,
			String endpoint,
			String userName,
			String password) throws MalformedURLException {
		return new WsClientHelper<BustiaV0>().generarClientWs(
				wsdlResourceUrl,
				endpoint,
				new QName(
						"http://www.caib.es/ripea/ws/v0/bustia",
						"BustiaV0Service"),
				userName,
				password,
				BustiaV0.class);
	}

	public static BustiaV0 getWsClient(
			String endpoint,
			String userName,
			String password) throws MalformedURLException {
		return new WsClientHelper<BustiaV0>().generarClientWs(
				endpoint,
				new QName(
						"http://www.caib.es/ripea/ws/v0/bustia",
						"BustiaV0Service"),
				userName,
				password,
				BustiaV0.class);
	}

}
