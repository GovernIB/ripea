package es.caib.ripea.back.interceptor;

import es.caib.ripea.back.helper.AvisHelper;
import es.caib.ripea.service.intf.service.AvisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AvisosInterceptor implements AsyncHandlerInterceptor {

	@Autowired private AvisService avisService;

	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {

		if (request.getUserPrincipal()!=null) {
			AvisHelper.findAvisos(request, avisService);
		}
		return true;
	}
}