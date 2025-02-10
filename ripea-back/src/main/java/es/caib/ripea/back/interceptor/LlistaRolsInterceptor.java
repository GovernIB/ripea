package es.caib.ripea.back.interceptor;

import es.caib.ripea.back.helper.ContingutEstaticHelper;
import es.caib.ripea.back.helper.RolHelper;
import es.caib.ripea.service.intf.service.AplicacioService;
import es.caib.ripea.service.intf.service.OrganGestorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interceptor per a gestionar la llista de rols a cada pàgina.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class LlistaRolsInterceptor extends HandlerInterceptorAdapter {

    @Autowired private AplicacioService aplicacioService;
    @Autowired private OrganGestorService organGestorService;

	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		if (!ContingutEstaticHelper.isContingutEstatic(request)) {
			RolHelper.processarCanviRols(request, aplicacioService, organGestorService);
			RolHelper.setRolActualFromDb(request, aplicacioService);
		}
		return true;
	}

}
