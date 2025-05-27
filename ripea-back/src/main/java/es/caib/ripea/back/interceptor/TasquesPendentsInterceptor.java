package es.caib.ripea.back.interceptor;

import es.caib.ripea.back.helper.TasquesPendentsHelper;
import es.caib.ripea.service.intf.service.ExpedientTascaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class TasquesPendentsInterceptor implements AsyncHandlerInterceptor {

	@Autowired
	private ExpedientTascaService expedientTascaService;

	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		if (request.getUserPrincipal()!=null) {
			TasquesPendentsHelper.countTasquesPendents(request, expedientTascaService);
		}
		return true;
	}
}