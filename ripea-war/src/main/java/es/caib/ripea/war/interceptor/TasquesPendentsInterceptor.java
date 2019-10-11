package es.caib.ripea.war.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import es.caib.ripea.core.api.service.ExpedientTascaService;
import es.caib.ripea.war.helper.TasquesPendentsHelper;

public class TasquesPendentsInterceptor extends HandlerInterceptorAdapter {

	
	@Autowired
	private ExpedientTascaService expedientTascaService;



	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		TasquesPendentsHelper.countTasquesPendents(
				request,
				expedientTascaService);
		return true;
	}
	
}
