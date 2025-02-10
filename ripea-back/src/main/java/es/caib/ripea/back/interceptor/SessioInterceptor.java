/**
 * 
 */
package es.caib.ripea.back.interceptor;

import es.caib.ripea.back.helper.SessioHelper;
import es.caib.ripea.service.intf.service.AplicacioService;
import es.caib.ripea.service.intf.service.EntitatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interceptor per a les accions de context de sessió.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class SessioInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private AplicacioService aplicacioService;
    @Autowired
    private EntitatService entitatService;

	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		SessioHelper.processarAutenticacio(request, response, aplicacioService, entitatService);
		return true;
	}

}
