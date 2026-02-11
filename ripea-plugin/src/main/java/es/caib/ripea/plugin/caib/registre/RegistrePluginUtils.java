package es.caib.ripea.plugin.caib.registre;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import es.caib.regweb3.ws.api.v3.RegWebAsientoRegistralWs;
import es.caib.regweb3.ws.api.v3.RegWebAsientoRegistralWsService;
import es.caib.ripea.plugin.RipeaAbstractPluginProperties;
import es.caib.ripea.service.intf.config.PropertyConfig;

public class RegistrePluginUtils extends RipeaAbstractPluginProperties {

	public static final String METODE_ASIENTO_REGISTRAL = "RegWebAsientoRegistral";

	public RegistrePluginUtils() {
		super();
	}
	
	public RegistrePluginUtils(String propertyKeyBase, Properties properties) {
		super(propertyKeyBase, properties);
	}
	
	protected RegWebAsientoRegistralWs getAsientoRegistralApi() throws Exception {
		
		final var endpoint = getServiceUrl() + METODE_ASIENTO_REGISTRAL;
		final var wsdl = new URL(endpoint + "?wsdl");
		var service = new RegWebAsientoRegistralWsService(wsdl);
		var api = service.getRegWebAsientoRegistralWs();
		configAddressUserPassword(getServiceUsername(), getServicePassword(), endpoint, api);
		return api;
	}
	
	@SuppressWarnings("rawtypes")
	private void configAddressUserPassword(String usr, String pwd, String endpoint, Object api) {

		var bp = (BindingProvider) api;
		var reqContext = bp.getRequestContext();
		reqContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);
		reqContext.put(BindingProvider.USERNAME_PROPERTY, usr);
		reqContext.put(BindingProvider.PASSWORD_PROPERTY, pwd);
		
		if (isLogMissatgesActiu()) {
            List<Handler> handlerChain = new ArrayList<Handler>();
            handlerChain.add(new LogMessageHandler());
            bp.getBinding().setHandlerChain(handlerChain);
        }
		
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

            try {
                SOAPMessage msg = messageContext.getMessage();
                Boolean outboundProperty = (Boolean) messageContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
                System.out.print(outboundProperty ? "Missatge SOAP petició: " : "Missatge SOAP resposta: ");
                msg.writeTo(System.out);
                System.out.println();
            } catch (SOAPException ex) {
                Logger.getLogger(LogMessageHandler.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(LogMessageHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

	
    protected String getServiceUrl() {
        String url = getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.REGISTRE_PLUGIN_URL));
		if (!url.endsWith("/")) {
			url = url + "/";
		}
        return url;
    }
    
    private String getServiceUsername() {
        return getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.REGISTRE_PLUGIN_USER));
    }

    private String getServicePassword() {
    	return getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.REGISTRE_PLUGIN_PASS));
    }
    
    private boolean isLogMissatgesActiu() {
    	return getAsBoolean(PropertyConfig.getPropertySuffix(PropertyConfig.REGISTRE_PLUGIN_DEBUG));
    }
    
}
