/**
 * 
 */
package es.caib.ripea.core.ejb.ws;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.jboss.annotation.security.SecurityDomain;
import org.jboss.wsf.spi.annotation.WebContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.ripea.core.api.registre.RegistreAnotacio;
import es.caib.ripea.core.api.service.ws.RipeaBackofficeResultatProces;
import es.caib.ripea.core.api.service.ws.RipeaBackofficeWsService;
import es.caib.ripea.core.helper.UsuariHelper;

/**
 * Implementació dels mètodes per al servei de bústies de RIPEA.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@WebService(
		name = "RipeaBackoffice",
		serviceName = "RipeaBackofficeService",
		portName = "RipeaBackofficeServicePort",
		targetNamespace = "http://www.caib.es/ripea/ws/backoffice")
@WebContext(
		contextRoot = "/ripea/ws",
		urlPattern = "/backoffice",
		authMethod = "WSBASIC",
		transportGuarantee = "NONE",
		secureWSDLAccess = false)
@RolesAllowed({"IPA_BACKWS"})
@SecurityDomain("seycon")
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class RipeaBackofficeWsServiceBean implements RipeaBackofficeWsService {

	@Resource
	private SessionContext sessionContext;
	@Autowired
	private UsuariHelper usuariHelper;



	@Override
	public RipeaBackofficeResultatProces processarAnotacio(
			RegistreAnotacio registreEntrada) {
		usuariHelper.generarUsuariAutenticatEjb(
				sessionContext,
				true);
		return new RipeaBackofficeResultatProces();
	}

}
