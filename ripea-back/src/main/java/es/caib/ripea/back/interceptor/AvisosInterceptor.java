/**
 * 
 */
package es.caib.ripea.back.interceptor;

import es.caib.ripea.back.helper.AvisHelper;
import es.caib.ripea.service.intf.service.AvisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
