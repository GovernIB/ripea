/**
 * 
 */
package es.caib.ripea.back.interceptor;

import es.caib.ripea.back.helper.FluxFirmaHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interceptor per controlar l'accés a la gestió de fluxos de firma.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class AccesFluxosFirmaUsuariInterceptor implements AsyncHandlerInterceptor {

	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		if (! FluxFirmaHelper.isCreacioFluxUsuariActiu(request)) {
			throw new SecurityException("Es necessari activar la propietat 'es.caib.ripea.plugin.portafirmes.fluxos.usuaris' per accedir a la gestió de fluxos de firma", null);
		}
		return true;
	}

}
