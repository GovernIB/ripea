package es.caib.ripea.back.interceptor;

import es.caib.ripea.back.helper.SeguimentEnviamentsUsuariHelper;
import es.caib.ripea.service.intf.service.AplicacioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class SeguimentEnviamentsUsuariInterceptor implements AsyncHandlerInterceptor {

	@Autowired
	private AplicacioService aplicacioService;

	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		SeguimentEnviamentsUsuariHelper.setMostrarSeguimentEnviamentsUsuari(request, aplicacioService);
		return true;
	}
	
}
