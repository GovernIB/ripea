/**
 * 
 */
package es.caib.ripea.war.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import es.caib.ripea.war.helper.ExpedientHelper;

/**
 * Interceptor per controlar l'accés a la gestió d'urls d'instrucció.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AccesURLsInstruccioInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		if (! ExpedientHelper.isUrlsInstruccioActiu(request)) {
			throw new SecurityException("Es necessari activar la propietat 'es.caib.ripea.expedient.generar.urls.instruccio' per accedir a la gestió d'URLs d'instrucció", null);
		}
		return true;
	}

}
