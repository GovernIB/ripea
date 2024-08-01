package es.caib.ripea.war.interceptor;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.api.service.EntitatService;
import es.caib.ripea.core.api.service.OrganGestorService;
import es.caib.ripea.war.helper.ContingutEstaticHelper;
import es.caib.ripea.war.helper.EntitatHelper;
import es.caib.ripea.war.helper.OrganGestorHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interceptor per a gestionar la informació comuna de totes les pagines relacionades amb 
 * l'administració distribuida.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class LlistaEntitatsInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private EntitatService entitatService;
    @Autowired
    private OrganGestorService organGestorService;
    @Autowired
    private AplicacioService aplicacioService;
    
    @Override
    public boolean preHandle(
    		HttpServletRequest request,
    		HttpServletResponse response,
    		Object handler) throws Exception {
        if (!ContingutEstaticHelper.isContingutEstatic(request)) {
            EntitatHelper.processarCanviEntitats(request, entitatService, aplicacioService);
			OrganGestorHelper.findOrganismesEntitatAmbPermisCache(request, organGestorService);
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
