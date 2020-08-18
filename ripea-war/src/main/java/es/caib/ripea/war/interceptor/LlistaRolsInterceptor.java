/**
 * 
 */
package es.caib.ripea.war.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import es.caib.ripea.core.api.service.MetaExpedientService;
import es.caib.ripea.war.helper.ContingutEstaticHelper;
import es.caib.ripea.war.helper.RolHelper;

/**
 * Interceptor per a gestionar la llista de rols a cada pàgina.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class LlistaRolsInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	MetaExpedientService metaExpedientService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
	                         Object handler) throws Exception {
		if (!ContingutEstaticHelper.isContingutEstatic(request)) {
			RolHelper.initialize(request, metaExpedientService);
			RolHelper.processarCanviRols(request);
		}
		return true;
	}

}
