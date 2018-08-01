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

import es.caib.ripea.core.service.ws.callback.Application;
import es.caib.ripea.core.service.ws.callback.Attributes;
import es.caib.ripea.core.service.ws.callback.CallbackRequest;
import es.caib.ripea.core.service.ws.callback.CallbackResponse;
import es.caib.ripea.core.service.ws.callback.Document;
import es.caib.ripea.core.service.ws.callback.MCGDws;

/**
 * Classe de proves pel servei web de callback de portafirmes MCGDws.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CallbackMcgdWsTest {

	private static final String ENDPOINT_ADDRESS = "http://localhost:8080/ripea/ws/MCGDws";

	public static void main(String[] args) {
		try {
			double resposta = new CallbackMcgdWsTest().test(1);
			System.out.println(">>> Resposta: " + resposta);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private double test(int documentId) throws Exception {
		CallbackRequest callbackRequest = new CallbackRequest();
		Application application = new Application();
		Document document = new Document();
		document.setId(documentId);
		Attributes attributes = new Attributes();
		// 0 - DOCUMENT_PAUSAT;
		// 1 - DOCUMENT_PENDENT;
		// 2 - DOCUMENT_FIRMAT;
		// 3 - DOCUMENT_REBUTJAT;
		attributes.setState(new Integer(2));
		document.setAttributes(attributes);
		application.setDocument(document);
		callbackRequest.setApplication(application);
		CallbackResponse callbackResponse = getMCGDService().callback(
				callbackRequest);
		return callbackResponse.getReturn();
	}

	private MCGDws getMCGDService() throws Exception {
		URL url = new URL(ENDPOINT_ADDRESS + "?wsdl");
		QName qname = new QName(
				"http://www.indra.es/portafirmasmcgdws/mcgdws",
				"MCGDwsService");
		Service service = Service.create(url, qname);
		MCGDws mcgd = service.getPort(MCGDws.class);
		BindingProvider bp = (BindingProvider)mcgd;
		@SuppressWarnings("rawtypes")
		List<Handler> handlerChain = new ArrayList<Handler>();
		handlerChain.add(new LogMessageHandler());
		bp.getBinding().setHandlerChain(handlerChain);
		return mcgd;
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
