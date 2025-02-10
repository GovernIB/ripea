package es.caib.ripea.back.interceptor;

import es.caib.ripea.back.helper.AnotacionsPendentsHelper;
import es.caib.ripea.service.intf.service.ExpedientPeticioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AnotacionsPendentsInterceptor extends HandlerInterceptorAdapter {

	
	@Autowired
	private ExpedientPeticioService expedientPeticioService;



	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		AnotacionsPendentsHelper.countAnotacionsPendents(
				request,
				expedientPeticioService);
		return true;
	}
	
}
