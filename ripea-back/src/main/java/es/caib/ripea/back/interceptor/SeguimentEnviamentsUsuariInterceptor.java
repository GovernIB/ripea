package es.caib.ripea.back.interceptor;

import es.caib.ripea.back.helper.SeguimentEnviamentsUsuariHelper;
import es.caib.ripea.service.intf.service.AplicacioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SeguimentEnviamentsUsuariInterceptor extends HandlerInterceptorAdapter {

	
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
