/**
 * 
 */
package es.caib.ripea.back.interceptor;

import es.caib.ripea.back.helper.ContingutEstaticHelper;
import es.caib.ripea.back.helper.MetaExpedientHelper;
import es.caib.ripea.service.intf.service.MetaExpedientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class MetaExpedientInterceptor implements AsyncHandlerInterceptor {

	@Autowired
	private MetaExpedientService metaExpedientService;

	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		if (request.getUserPrincipal()!=null && !ContingutEstaticHelper.isContingutEstatic(request)) {
			MetaExpedientHelper.setRevisioActiva(request,metaExpedientService);
		}
		return true;
	}

}
