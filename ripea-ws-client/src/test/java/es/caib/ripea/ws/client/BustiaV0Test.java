/**
 * 
 */
package es.caib.ripea.ws.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;

import org.junit.Test;

import es.caib.ripea.ws.v0.bustia.BustiaContingutTipus;
import es.caib.ripea.ws.v0.bustia.BustiaV0;
import es.caib.ripea.ws.v0.bustia.BustiaV0Service;

/**
 * Client de test per al servei bustia de RIPEA.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BustiaV0Test {

	private static final String ENDPOINT_ADDRESS = "http://localhost:8080/ripea/ws/v0/bustia";
	private static final String USERNAME = null;
	private static final String PASSWORD = null;



	@Test
	public void test() throws MalformedURLException {
		getBustiaServicePort().enviarContingut(
				"entitatCodi",
				"unitatCodi",
				BustiaContingutTipus.EXPEDIENT,
				"ref");
	}



	private BustiaV0 getBustiaServicePort() throws MalformedURLException {
		URL url = new URL(ENDPOINT_ADDRESS + "?wsdl");
		BustiaV0 bustia = new BustiaV0Service(url).getBustiaV0ServicePort();
		@SuppressWarnings("rawtypes")
		List<Handler> handlerChain = new ArrayList<Handler>();
		handlerChain.add(new LogMessageHandler());
		if (USERNAME != null) {
			BindingProvider bp = (BindingProvider)bustia;
			bp.getBinding().setHandlerChain(handlerChain);
			bp.getRequestContext().put(
					BindingProvider.USERNAME_PROPERTY,
					USERNAME);
			bp.getRequestContext().put(
					BindingProvider.PASSWORD_PROPERTY,
					PASSWORD);
		}
		return bustia;
	}

}
