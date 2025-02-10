package es.caib.ripea.back.interceptor;

import es.caib.ripea.back.helper.AnotacionsPendentsHelper;
import es.caib.ripea.service.intf.service.ExpedientPeticioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AnotacionsPendentsInterceptor implements AsyncHandlerInterceptor {

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
