/**
 * 
 */
package es.caib.ripea.core.ejb.ws;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.jboss.wsf.spi.annotation.WebContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.portafib.ws.callback.api.v1.CallBackException;
import es.caib.portafib.ws.callback.api.v1.PortaFIBCallBackWs;
import es.caib.portafib.ws.callback.api.v1.PortaFIBEvent;
import es.caib.ripea.core.service.ws.callbackportafib.PortaFIBCallBackWsImpl;

/**
 * Implementació dels mètodes per al servei de recepció de
 * callbacks de portafirmes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@WebService(
		name = "PortaFIBCallBackWs",
		targetNamespace = "http://v1.server.callback.ws.portafib.caib.es/")
@WebContext(
		contextRoot = "/ripea/ws",
		urlPattern = "/portafibCallback",
		//authMethod = "WSBASIC",
		transportGuarantee = "NONE",
		secureWSDLAccess = false)
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class PortaFIBCallbackWsBean implements PortaFIBCallBackWs {

	@Autowired
	private PortaFIBCallBackWsImpl delegate;

	@Override
	public int getVersionWs() {
		return delegate.getVersionWs();
	}

	@Override
	public void event(PortaFIBEvent event) throws CallBackException {
		delegate.event(event);
	}

}
