/**
 * 
 */
package es.caib.ripea.war.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import es.caib.ripea.core.api.service.MetaExpedientService;
import es.caib.ripea.war.helper.MetaExpedientHelper;


public class MetaExpedientInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private MetaExpedientService metaExpedientService;

	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		
		MetaExpedientHelper.setRevisioActiva(
				request, 
				metaExpedientService);

		return true;
	}

}
