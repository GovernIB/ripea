package es.caib.ripea.back.interceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import es.caib.ripea.back.helper.EntitatHelper;
import es.caib.ripea.service.intf.dto.EntitatDto;
import es.caib.ripea.service.intf.dto.OrganGestorDto;
import es.caib.ripea.service.intf.service.AplicacioService;
import es.caib.ripea.service.intf.service.EntitatService;
import es.caib.ripea.service.intf.service.OrganGestorService;

@Component
public class LlistaEntitatsInterceptor implements AsyncHandlerInterceptor {

	@Autowired private EntitatService entitatService;
	@Autowired private OrganGestorService organGestorService;
	@Autowired private AplicacioService aplicacioService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		
		if (request.getUserPrincipal()!=null) {
			
			EntitatHelper.processarCanviEntitats(request, entitatService, aplicacioService);
			EntitatHelper.findOrganismesEntitatAmbPermisCache(request, organGestorService);
			EntitatHelper.processarCanviOrganGestor(request, aplicacioService);
			EntitatHelper.findEntitatsAccessibles(request, entitatService);
				
			EntitatDto entitatDto = EntitatHelper.getEntitatActual(request);
			if (entitatDto != null) {
				entitatService.setConfigEntitat(entitatDto);
			}
			OrganGestorDto organGestorDto = EntitatHelper.getOrganGestorActual(request);
			if (organGestorDto!=null) {
				aplicacioService.actualitzarOrganCodi(organGestorDto.getCodi());
			}
		}
		return true;
	}
}