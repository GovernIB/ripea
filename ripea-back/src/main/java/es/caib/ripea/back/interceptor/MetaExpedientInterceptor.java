package es.caib.ripea.back.interceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import es.caib.ripea.back.helper.MetaExpedientHelper;
import es.caib.ripea.service.intf.service.MetaExpedientService;

@Component
public class MetaExpedientInterceptor implements AsyncHandlerInterceptor {

	@Autowired
	private MetaExpedientService metaExpedientService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (request.getUserPrincipal()!=null) {
			MetaExpedientHelper.setRevisioActiva(request,metaExpedientService);
		}
		return true;
	}
}