/**
 * 
 */
package es.caib.ripea.ejb.ws;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.service.ws.callback.CallbackRequest;
import es.caib.ripea.service.intf.service.ws.callback.CallbackResponse;
import es.caib.ripea.service.intf.service.ws.callback.MCGDws;
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
		name = "MCGDws",
		serviceName = "MCGDwsService",
		portName = "MCGDwsServicePort",
		targetNamespace = "http://www.indra.es/portafirmasmcgdws/mcgdws")
@WebContext(
		contextRoot = "/ripea/ws",
		urlPattern = "/MCGDws",
		transportGuarantee = "NONE",
		secureWSDLAccess = false)
public class MCGDServiceWs extends AbstractServiceEjb<MCGDws> implements MCGDws {

	@Delegate
	private MCGDws delegateService;

	protected void setDelegateService(MCGDws delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	public CallbackResponse callback(CallbackRequest callbackRequest) {
		return delegateService.callback(callbackRequest);
	}

}
