/**
 * 
 */
package es.caib.ripea.war.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.war.helper.FluxFirmaHelper;

/**
 * Interceptor per a gestionar la creació de fluxos a nivell d'usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class FluxFirmaInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private AplicacioService aplicacioService;
	
	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		FluxFirmaHelper.setCreacioFluxUsuariActiu(
				request, 
				aplicacioService);
		return true;
	}

}
