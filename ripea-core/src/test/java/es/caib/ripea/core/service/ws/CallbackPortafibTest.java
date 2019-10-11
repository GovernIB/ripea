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

import es.caib.portafib.ws.callback.api.v1.PortaFIBCallBackWs;
import es.caib.portafib.ws.callback.api.v1.PortaFIBEvent;
import es.caib.portafib.ws.callback.api.v1.SigningRequest;

/**
 * Classe de proves pel servei web de callback de portafirmes amb interfície
 * PortaFIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CallbackPortafibTest {

	private static final String ENDPOINT_ADDRESS = "http://localhost:8080/ripea/ws/portafibCallback";
	//private static final String USERNAME = "XXX";
	//private static final String PASSWORD = "XXX";

	public static void main(String[] args) {
		try {
			// Estats:
			//   0  - DOCUMENT_PENDENT;
			//   50 - DOCUMENT_PENDENT;
			//   60 - DOCUMENT_FIRMAT;
			//   70 - DOCUMENT_REBUTJAT;
			//   80 - DOCUMENT_PAUSAT;
			new CallbackPortafibTest().test(
					83488,
					60);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void test(
			long documentId,
			int estat) throws Exception {
		PortaFIBEvent event = new PortaFIBEvent();
		SigningRequest signingRequest = new SigningRequest();
		signingRequest.setID(documentId);
		event.setSigningRequest(signingRequest);
		event.setEventTypeID(estat);
		getCallbackService().event(event);
	}

	private PortaFIBCallBackWs getCallbackService() throws Exception {
		URL url = new URL(ENDPOINT_ADDRESS + "?wsdl");
		QName qname = new QName(
				"http://v1.server.callback.ws.portafib.caib.es/",
				"PortaFIBCallBackWsService");
		Service service = Service.create(url, qname);
		PortaFIBCallBackWs callback = service.getPort(PortaFIBCallBackWs.class);
		BindingProvider bp = (BindingProvider)callback;
		/*bp.getRequestContext().put(
				BindingProvider.USERNAME_PROPERTY,
				USERNAME);
		bp.getRequestContext().put(
				BindingProvider.PASSWORD_PROPERTY,
				PASSWORD);*/
		@SuppressWarnings("rawtypes")
		List<Handler> handlerChain = new ArrayList<Handler>();
		handlerChain.add(new LogMessageHandler());
		bp.getBinding().setHandlerChain(handlerChain);
		return callback;
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
