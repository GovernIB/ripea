/**
 * 
 */
package es.caib.ripea.back.interceptor;

import es.caib.ripea.back.helper.FluxFirmaHelper;
import es.caib.ripea.service.intf.service.AplicacioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interceptor per a gestionar la creació de fluxos a nivell d'usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class FluxFirmaInterceptor implements AsyncHandlerInterceptor {

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
