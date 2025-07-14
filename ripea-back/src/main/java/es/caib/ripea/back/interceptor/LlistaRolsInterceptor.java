package es.caib.ripea.back.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import es.caib.ripea.back.helper.RolHelper;
import es.caib.ripea.service.intf.service.AplicacioService;
import es.caib.ripea.service.intf.service.EventService;
import es.caib.ripea.service.intf.service.OrganGestorService;

@Component
public class LlistaRolsInterceptor implements AsyncHandlerInterceptor {

    @Autowired private AplicacioService aplicacioService;
    @Autowired private OrganGestorService organGestorService;
    @Autowired private EventService eventService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (request.getUserPrincipal()!=null) {
			RolHelper.processarCanviRols(request, aplicacioService, organGestorService, eventService);
			RolHelper.setRolActualFromDb(request, aplicacioService);
		}
		return true;
	}
}