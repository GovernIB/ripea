package es.caib.ripea.back.interceptor;

import es.caib.ripea.back.helper.ContingutEstaticHelper;
import es.caib.ripea.back.helper.EntitatHelper;
import es.caib.ripea.service.intf.dto.EntitatDto;
import es.caib.ripea.service.intf.service.AplicacioService;
import es.caib.ripea.service.intf.service.EntitatService;
import es.caib.ripea.service.intf.service.OrganGestorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interceptor per a gestionar la informació comuna de totes les pagines relacionades amb 
 * l'administració distribuida.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class LlistaEntitatsInterceptor implements AsyncHandlerInterceptor {

	@Autowired private EntitatService entitatService;
	@Autowired private OrganGestorService organGestorService;
	@Autowired private AplicacioService aplicacioService;

	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		if (!ContingutEstaticHelper.isContingutEstatic(request)) {
			EntitatHelper.processarCanviEntitats(request, entitatService, aplicacioService);
			EntitatHelper.findOrganismesEntitatAmbPermisCache(request, organGestorService);
			EntitatHelper.processarCanviOrganGestor(request, aplicacioService);
			EntitatHelper.findEntitatsAccessibles(request, entitatService);
		}
		EntitatDto entitatDto = EntitatHelper.getEntitatActual(request);
		if (entitatDto != null) {
			entitatService.setConfigEntitat(entitatDto);
		}
		return true;
	}

}