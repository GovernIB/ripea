/**
 * 
 */
package es.caib.ripea.core.helper;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ejb.CreateException;
import javax.management.InstanceNotFoundException;
import javax.management.MalformedObjectNameException;
import javax.naming.NamingException;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.jboss.mx.util.MBeanProxyCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.caib.loginModule.client.AuthenticationFailureException;

/**
 * Utilitat per a instanciar clients per al servei d'enviament
 * de contingut a bústies.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class WsClientHelper<T> {

	public T generarClientWs(
			URL wsdlResourceUrl,
			String endpoint,
			QName qname,
			String username,
			String password,
			String soapAction,
			Class<T> clazz,
			Handler<?>... handlers) throws MalformedURLException, InstanceNotFoundException, MalformedObjectNameException, MBeanProxyCreationException, RemoteException, NamingException, CreateException, AuthenticationFailureException {
		URL url = wsdlResourceUrl;
		if (url == null) {
			if (!endpoint.endsWith("?wsdl"))
				url = new URL(endpoint + "?wsdl");
			else
				url = new URL(endpoint);
		}
		Service service = Service.create(url, qname);
		T bustiaWs = service.getPort(clazz);
		BindingProvider bindingProvider = (BindingProvider)bustiaWs;
		// Configura l'adreça del servei
		String endpointAddress;
		if (!endpoint.endsWith("?wsdl"))
			endpointAddress = endpoint;
		else
			endpointAddress = endpoint.substring(0, endpoint.length() - "?wsdl".length());
		bindingProvider.getRequestContext().put(
				BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				endpointAddress);
		// Configura l'autenticació si és necessària
		if (username != null && !username.isEmpty()) {
			/*
			ControladorSesion controlador = new ControladorSesion();
			controlador.autenticar(userName, password);
			AuthorizationToken token = controlador.getToken();
			bp.getRequestContext().put(
					BindingProvider.USERNAME_PROPERTY,
					token.getUser());
			bp.getRequestContext().put(
					BindingProvider.PASSWORD_PROPERTY,
					token.getPassword());
			*/
			bindingProvider.getRequestContext().put(
					BindingProvider.USERNAME_PROPERTY,
					username);
			bindingProvider.getRequestContext().put(
					BindingProvider.PASSWORD_PROPERTY,
					password);
		}
		// Configura el log de les peticions
		@SuppressWarnings("rawtypes")
		List<Handler> handlerChain = new ArrayList<Handler>();
		handlerChain.add(new SOAPLoggingHandler());
		// Configura la capçalera HTTP SOAPAction
		if (soapAction != null) {
			bindingProvider.getRequestContext().put(
					BindingProvider.SOAPACTION_USE_PROPERTY,
					true);
			bindingProvider.getRequestContext().put(
					BindingProvider.SOAPACTION_URI_PROPERTY,
					soapAction);
		}
		// Configura handlers addicionals
		for (int i = 0; i < handlers.length; i++) {
			if (handlers[i] != null)
				handlerChain.add(handlers[i]);
		}
		bindingProvider.getBinding().setHandlerChain(handlerChain);
		return bustiaWs;
	}

	public T generarClientWs(
			String endpoint,
			QName qname,
			String userName,
			String password,
			String soapAction,
			Class<T> clazz,
			Handler<?>... handlers) throws MalformedURLException, InstanceNotFoundException, MalformedObjectNameException, MBeanProxyCreationException, RemoteException, NamingException, CreateException, AuthenticationFailureException {
		return this.generarClientWs(
				null,
				endpoint,
				qname,
				userName,
				password,
				soapAction,
				clazz,
				handlers);
	}

	private static class SOAPLoggingHandler implements SOAPHandler<SOAPMessageContext> {
		public Set<QName> getHeaders() {
			return null;
		}
		public boolean handleMessage(SOAPMessageContext smc) {
			logMessage(smc);
			return true;
		}
		public boolean handleFault(SOAPMessageContext smc) {
			logMessage(smc);
			return true;
		}
		public void close(MessageContext messageContext) {
		}
		private void logMessage(SOAPMessageContext smc) {
			if (true) {//LOGGER.isDebugEnabled()) {
				StringBuilder sb = new StringBuilder();
				Boolean outboundProperty = (Boolean)smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
				if (outboundProperty.booleanValue())
					sb.append("Missarge sortint: ");
				else
					sb.append("Missarge entrant: ");
				SOAPMessage message = smc.getMessage();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				try {
					message.writeTo(baos);
					sb.append(baos.toString());
				} catch (Exception ex) {
					sb.append("Error al imprimir el missatge XML: " + ex.getMessage());
				}
				System.out.println(">>> " + sb.toString());
				LOGGER.debug(sb.toString());
			}
		}
		private static final Logger LOGGER = LoggerFactory.getLogger(WsClientHelper.class);
	}

}
