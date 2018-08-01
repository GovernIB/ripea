/**
 * 
 */
package es.caib.ripea.war.controller;

import io.milton.config.HttpManagerBuilder;
import io.milton.http.AuthenticationHandler;
import io.milton.http.AuthenticationService;
import io.milton.http.HttpManager;
import io.milton.servlet.ServletRequest;
import io.milton.servlet.ServletResponse;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.api.service.CarpetaService;
import es.caib.ripea.core.api.service.ContingutService;
import es.caib.ripea.core.api.service.DocumentService;
import es.caib.ripea.core.api.service.EntitatService;
import es.caib.ripea.war.webdav.RipeaResourceFactory;

/**
 * Controlador per a la navegació via WEBDAV.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class WebdavController extends BaseUserController {

	@Autowired
	private ServletContext servletContext;
	@Autowired
	private ContingutService contenidorService;
	@Autowired
	private DocumentService documentService;
	@Autowired
	private CarpetaService carpetaService;
	@Autowired
	private EntitatService entitatService;
	@Autowired
	private AplicacioService aplicacioService;

	private HttpManager httpManager;
	private RipeaResourceFactory resourceFactory;



	@RequestMapping(value = {"/webdav", "/webdav/**"})
	public void dispatch(
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		getHttpManager().process(
				new ServletRequest(request, servletContext),
				new ServletResponse(response));
	}



	private HttpManager getHttpManager() {
		if (httpManager == null) {
			HttpManagerBuilder builder = new HttpManagerBuilder();
			resourceFactory = new RipeaResourceFactory(
					contenidorService,
					documentService,
					carpetaService,
					entitatService,
					aplicacioService);
			builder.setResourceFactory(resourceFactory);
			List<AuthenticationHandler> authenticationHandlers = new ArrayList<AuthenticationHandler>();
			AuthenticationService authenticationService = new AuthenticationService(authenticationHandlers);
			builder.setAuthenticationService(authenticationService);
			httpManager = builder.buildHttpManager();
		}
		return httpManager;
	}

}
