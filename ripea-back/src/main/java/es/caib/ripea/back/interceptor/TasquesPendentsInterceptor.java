package es.caib.ripea.back.interceptor;

import es.caib.ripea.back.helper.TasquesPendentsHelper;
import es.caib.ripea.service.intf.service.ExpedientTascaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
