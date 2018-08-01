/**
 * 
 */
package es.caib.ripea.core.service.ws;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import es.caib.ripea.core.api.service.ws.BustiaV0WsService;
import es.caib.ripea.core.api.service.ws.BustiaV0WsService.BustiaContingutTipus;

/**
 * Classe de proves pel registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BustiaWsTest {

	private static final String ENDPOINT_ADDRESS = "http://localhost:8080/ripea/ws/bustia";
	private static final String USERNAME = "admin";
	private static final String PASSWORD = "admin15";

	public static void main(String[] args) {
		try {
			new BustiaWsTest().provaEnviamentContingut();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void provaEnviamentContingut() throws Exception {
		getBustiaService().enviarContingut(
				"ENTITAT_CODI",
				"UNITAT_ADM_CODI",
				BustiaContingutTipus.REGISTRE_ENTRADA,
				"REGISTRE_REF");
	}

	private BustiaV0WsService getBustiaService() throws Exception {
		URL url = new URL(ENDPOINT_ADDRESS + "?wsdl");
		QName qname = new QName(
				"http://www.caib.es/ripea/ws/bustia",
				"BustiaService");
		Service service = Service.create(url, qname);
		BustiaV0WsService bustiaWs = service.getPort(BustiaV0WsService.class);
		BindingProvider bp = (BindingProvider)bustiaWs;
		@SuppressWarnings("rawtypes")
		List<Handler> handlerChain = new ArrayList<Handler>();
		handlerChain.add(new LogMessageHandler());
		bp.getBinding().setHandlerChain(handlerChain);
		bp.getRequestContext().put(
				BindingProvider.USERNAME_PROPERTY,
				USERNAME);
		bp.getRequestContext().put(
				BindingProvider.PASSWORD_PROPERTY,
				PASSWORD);
		return bustiaWs;
	}

	private class LogMessageHandler implements SOAPHandler<SOAPMessageContext> {
		public boolean handleMessage(SOAPMessageContext messageContext) {
			log(messageContext);
			return true;
		}
		public Set<QName> getHeaders() {
			return Collections.emptySet();
		}
		public boolean handleFault(SOAPMessageContext messageContext) {
			log(messageContext);
			return true;
		}
		public void close(MessageContext context) {
		}
		private void log(SOAPMessageContext messageContext) {
			SOAPMessage msg = messageContext.getMessage();
			try {
				Boolean outboundProperty = (Boolean)messageContext.get(
						MessageContext.MESSAGE_OUTBOUND_PROPERTY);
				if (outboundProperty)
					System.out.print("Missatge SOAP petició: ");
				else
					System.out.print("Missatge SOAP resposta: ");
				msg.writeTo(System.out);
				System.out.println();
			} catch (SOAPException ex) {
				Logger.getLogger(LogMessageHandler.class.getName()).log(
						Level.SEVERE,
						null,
						ex);
			} catch (IOException ex) {
				Logger.getLogger(LogMessageHandler.class.getName()).log(
						Level.SEVERE,
						null,
						ex);
			}
		}
	}

}
