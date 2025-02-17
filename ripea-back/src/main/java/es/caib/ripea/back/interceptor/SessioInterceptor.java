package es.caib.ripea.back.interceptor;

import es.caib.ripea.back.helper.SessioHelper;
import es.caib.ripea.service.intf.service.AplicacioService;
import es.caib.ripea.service.intf.service.EntitatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class SessioInterceptor implements AsyncHandlerInterceptor {

	@Autowired private AplicacioService aplicacioService;
    @Autowired private EntitatService entitatService;

	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		
		String redireccio = SessioHelper.processarAutenticacio(request, response, aplicacioService, entitatService);
		
		if (redireccio==null) {
			return true;
		} else {
			response.sendRedirect(redireccio);
			return false;
		}
	}
}