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

import es.caib.ripea.core.api.registre.RegistreInteressat;
import es.caib.ripea.core.api.service.ws.BustiaV1WsService;


/** Classe base amb mètode comuns per provar la bústia. */
public class BustiaBaseTest {
	
	private static final String ENDPOINT_ADDRESS = "http://localhost:8080/ripea/ws/v1/bustia";
	
	/** Per obtenir el WS de la bústia */
	protected BustiaV1WsService getBustiaService() throws Exception {
		URL url = new URL(ENDPOINT_ADDRESS + "?wsdl");
		QName qname = new QName(
				"http://www.caib.es/ripea/ws/v1/bustia",
				"BustiaV1Service");
		Service service = Service.create(url, qname);
		BustiaV1WsService bustiaWs = service.getPort(BustiaV1WsService.class);
		BindingProvider bp = (BindingProvider)bustiaWs;
		@SuppressWarnings("rawtypes")
		List<Handler> handlerChain = new ArrayList<Handler>();
		handlerChain.add(new LogMessageHandler());
		bp.getBinding().setHandlerChain(handlerChain);

		return bustiaWs;
	}
	
	protected RegistreInteressat getInteressat(int i) {
		RegistreInteressat interessat = new RegistreInteressat();

		interessat.setAdresa("Addreça " + i);
		interessat.setCanalPreferent("02");
		interessat.setCodiPostal("07500");
		interessat.setDocumentNum("12345678Z");
		interessat.setDocumentTipus("N");
		interessat.setEmail("email" + i + "@mail.com");
		interessat.setEmailHabilitat("email" + i + "@mail.com");
		interessat.setNom("Nom_" + i);
		interessat.setLlinatge1("Llinatge1_" + i);
		interessat.setLlinatge2("Llinatge2_" + i);
		interessat.setMunicipi("Municipi"+i);
		interessat.setObservacions("Observacions " + i);
		interessat.setPais("ES");
		interessat.setProvincia("Prov" + i);
		interessat.setRaoSocial("?");
		interessat.setTelefon("971000000");
		interessat.setTipus("2");
		
		// Representant de l'interessat
		RegistreInteressat representant = new RegistreInteressat();
		representant.setAdresa("Addreça representant " + i);
		representant.setCanalPreferent("02");
		representant.setCodiPostal("07500");
		representant.setDocumentNum("12345678Z");
		representant.setDocumentTipus("N");
		representant.setEmail("representant" + i + "@mail.com");
		representant.setEmailHabilitat("representant" + i + "@mail.com");
		representant.setNom("Representant_" + i);
		representant.setLlinatge1("Llinatge1_" + i);
		representant.setLlinatge2("Llinatge2_" + i);
		representant.setMunicipi("Municipi"+i);
		representant.setObservacions("Observacions representant " + i);
		representant.setPais("ES");
		representant.setProvincia("Prov" + i);
		representant.setRaoSocial("?");
		representant.setTelefon("971000000");
		representant.setTipus("2");
		
		interessat.setRepresentant(representant);
		
		return interessat;
	}

	/** Handler per mostrar els missatges SOAP que s'envien i es reben.
	 * 
	 */
	protected class LogMessageHandler implements SOAPHandler<SOAPMessageContext> {
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
