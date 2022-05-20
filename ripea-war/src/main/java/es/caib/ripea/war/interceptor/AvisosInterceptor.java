/**
 * 
 */
package es.caib.ripea.war.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import es.caib.ripea.core.api.service.AvisService;
import es.caib.ripea.war.helper.AvisHelper;

/**
 * Interceptor per a comptar els elements pendents de les bústies
 * de l'usuari actual.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AvisosInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private AvisService avisService;



	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		
		AvisHelper.findAvisos(
				request,
				avisService);
		return true;
	}

}
