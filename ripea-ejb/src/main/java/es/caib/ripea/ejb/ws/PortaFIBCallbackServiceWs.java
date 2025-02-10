/**
 * 
 */
package es.caib.ripea.ejb.ws;

import es.caib.portafib.ws.callback.api.v1.CallBackException;
import es.caib.portafib.ws.callback.api.v1.PortaFIBCallBackWs;
import es.caib.portafib.ws.callback.api.v1.PortaFIBEvent;
import es.caib.ripea.ejb.base.AbstractServiceEjb;
import lombok.experimental.Delegate;
import org.jboss.ws.api.annotation.WebContext;

import javax.ejb.Stateless;
import javax.jws.WebService;

/**
 * Implementació dels mètodes per al servei de recepció de
 * callbacks de portafirmes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@WebService(
		name = "PortaFIBCallBackWs",
		serviceName = "PortaFIBCallBackWsService",
		portName = "PortaFIBCallBackWs",
		targetNamespace = "http://v1.server.callback.ws.portafib.caib.es/")
@WebContext(
		contextRoot = "/ripea/ws",
		urlPattern = "/portafibCallback",
		transportGuarantee = "NONE",
		secureWSDLAccess = false)
public class PortaFIBCallbackServiceWs extends AbstractServiceEjb<PortaFIBCallBackWs> implements PortaFIBCallBackWs {

	@Delegate
	private PortaFIBCallBackWs delegateService;

	protected void setDelegateService(PortaFIBCallBackWs delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	public int getVersionWs() {
		return delegateService.getVersionWs();
	}

	@Override
	public void event(PortaFIBEvent event) throws CallBackException {
		delegateService.event(event);
	}

}
