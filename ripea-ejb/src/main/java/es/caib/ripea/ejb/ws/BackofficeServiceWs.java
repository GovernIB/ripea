package es.caib.ripea.ejb.ws;

import es.caib.distribucio.ws.backoffice.AnotacioRegistreId;
import es.caib.distribucio.ws.backoffice.Backoffice;
import es.caib.ripea.ejb.base.AbstractServiceEjb;
import lombok.experimental.Delegate;
import org.jboss.ws.api.annotation.WebContext;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.jws.WebService;
import java.util.List;

/**
 * Implementació dels mètodes per al servei de bústies de DISTRIBUCIO.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@WebService(
		name = "Backoffice",
		serviceName = "BackofficeService",
		portName = "BackofficeServicePort",
		targetNamespace = "http://www.caib.es/distribucio/ws/backoffice")
@WebContext(
		contextRoot = "/ripea/ws",
		urlPattern = "/backoffice",
		authMethod = "BASIC",
		transportGuarantee = "NONE",
		secureWSDLAccess = false)
@RolesAllowed({ "DIS_BSTWS" })
public class BackofficeServiceWs extends AbstractServiceEjb<Backoffice> implements Backoffice {

	@Delegate
	private Backoffice delegateService;

	protected void setDelegateService(Backoffice delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	public void comunicarAnotacionsPendents(List<AnotacioRegistreId> ids) {
		delegateService.comunicarAnotacionsPendents(ids);
	}

}