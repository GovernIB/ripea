package es.caib.ripea.war.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.api.service.EntitatService;
import es.caib.ripea.war.helper.SessioHelper;

public class SessioInterceptor extends HandlerInterceptorAdapter {

	@Autowired private AplicacioService aplicacioService;
    @Autowired private EntitatService entitatService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String redireccio = SessioHelper.processarAutenticacio(request, response, aplicacioService, entitatService);
		if (redireccio==null) {
			return true;
		} else {
			response.sendRedirect(redireccio);
			return false;
		}
	}
}