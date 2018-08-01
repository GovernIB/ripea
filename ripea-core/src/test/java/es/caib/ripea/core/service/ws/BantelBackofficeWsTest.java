/**
 * 
 */
package es.caib.ripea.core.service.ws;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.junit.Test;

import es.caib.ripea.core.api.service.bantel.ws.v2.BantelBackofficeWs;
import es.caib.ripea.core.api.service.bantel.ws.v2.model.DocumentoBTE;
import es.caib.ripea.core.api.service.bantel.ws.v2.model.ObjectFactory;
import es.caib.ripea.core.api.service.bantel.ws.v2.model.ReferenciaEntrada;
import es.caib.ripea.core.api.service.bantel.ws.v2.model.ReferenciasEntrada;
import es.caib.ripea.core.api.service.bantel.ws.v2.model.TramiteBTE;

/**
 * Classe de proves per invocar el WS de backoffice que emula Sistra amb els mètodes per obtenir entrades, establir el
 * resultat del procés o obtenir les dades del procés.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BantelBackofficeWsTest {

	private static final String ENDPOINT_ADDRESS = "http://localhost:8080/ripea/ws/bantel";

	public static void main(String[] args) {
		BantelBackofficeWsTest test = new BantelBackofficeWsTest();
		// Prova per establir el resultat d'una anotació de registre
		try {
			test.provaEstablirResultatProces();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		// Prova d'obtenir les entrades amb el filtre per Sistra
		try {
			test.provaObtenirNumerosEntrades();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		// Prova d'obtenir una entrada
		try {
			test.provaObtenirEntrada();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Test
	public void provaEstablirResultatProces() throws Exception {
				
		ObjectFactory bantelObjectFactory = new ObjectFactory();
		// Envia l'anotació de registre.
		
		// 1709061137
		ReferenciaEntrada referencia = new ReferenciaEntrada();
		referencia.setNumeroEntrada("1709061137");
		referencia.setClaveAcceso(bantelObjectFactory.createReferenciaEntradaClaveAcceso("+xm77t+TautS+wOvxTtTBKg=="));
		String resultado = "N";
		String resultadoProcesamiento = "Entrada no processada";
		getBackofficeService().establecerResultadoProceso(
				referencia, 
				resultado, 
				resultadoProcesamiento);
		
		// 1709081343
		referencia.setNumeroEntrada("1709081343");
		referencia.setClaveAcceso(bantelObjectFactory.createReferenciaEntradaClaveAcceso("+xTaJc0W1Rq2gV3i0dJjTg=="));
		getBackofficeService().establecerResultadoProceso(
				referencia, 
				resultado, 
				resultadoProcesamiento);
	}

	/** Prova d'obtenir els números de registre amb el filtre de dades dels paràmetres.*/ 
	@Test
	public void provaObtenirNumerosEntrades() throws Exception {
		
		String identificadorProcedimiento = "TS0010REGT";
		String identificadorTramite = "TS0010REGT-2";
		String procesada = null; //"N";
		// Dates
		GregorianCalendar c = new GregorianCalendar();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		// desde
		//c.setTime(new Date(0));
		c.setTime(sdf.parse("2017/08/01"));
		XMLGregorianCalendar desde =  DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		desde = null;
		// fins
		//c.setTime(new Date());
		c.setTime(sdf.parse("2017/08/30"));
		XMLGregorianCalendar hasta = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		hasta = null;
		
		ReferenciasEntrada referencias = getBackofficeService().obtenerNumerosEntradas(
				identificadorProcedimiento, 
				identificadorTramite, 
				procesada, 
				desde, 
				hasta);
		
		System.out.println("Numeros entrades: ");
		for (ReferenciaEntrada referencia : referencias.getReferenciaEntrada())
			System.out.println("referencia.getNumeroEntrada: " + referencia.getNumeroEntrada());
	}
	
	/** Prova d'obtenir la informació d'una entrada en concret.
	 *  
	 * @throws Exception
	 */
	@Test
	public void provaObtenirEntrada() throws Exception {
				
		ObjectFactory bantelObjectFactory = new ObjectFactory();
		ReferenciaEntrada referencia = new ReferenciaEntrada();
		referencia.setNumeroEntrada("1709081343");
		referencia.setClaveAcceso(bantelObjectFactory.createReferenciaEntradaClaveAcceso("+xTaJc0W1Rq2gV3i0dJjTg=="));
		
		System.out.println("Obtenint la entrada " + referencia.getNumeroEntrada());
		
		TramiteBTE tramitBte = getBackofficeService().obtenerEntrada(referencia);
		
		System.out.println("Informació del tràmit BTE obtinguda: ");
		System.out.println("\t identificadorTramite: " + tramitBte.getIdentificadorTramite());
		System.out.println("\tDocuments annexos: ");
		for (DocumentoBTE document : tramitBte.getDocumentos().getDocumento()) {
			System.out.println("\t\tIdentificador: " + document.getIdentificador() + 
									", nom: " + document.getNombre() + 
									", nom fitxer: " + document.getPresentacionTelematica().getValue().getNombre() + 
									", extensió: " + document.getPresentacionTelematica().getValue().getExtension());
		}
		
	}

	private BantelBackofficeWs getBackofficeService() throws Exception {
		URL url = new URL(ENDPOINT_ADDRESS + "?wsdl");
		QName qname = new QName(
				"urn:es:caib:bantel:ws:v2:services",
				"BackofficeFacadeService");
		Service service = Service.create(url, qname);
		BantelBackofficeWs backofficeWs = service.getPort(BantelBackofficeWs.class);
		BindingProvider bp = (BindingProvider)backofficeWs;
		@SuppressWarnings("rawtypes")
		List<Handler> handlerChain = new ArrayList<Handler>();
		handlerChain.add(new LogMessageHandler());
		bp.getBinding().setHandlerChain(handlerChain);
		
//		// Autenticació
//		bp.getRequestContext().put(
//				BindingProvider.USERNAME_PROPERTY,
//				"admin");
//		bp.getRequestContext().put(
//				BindingProvider.PASSWORD_PROPERTY,
//				"admin15");
		 
		return backofficeWs;
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
