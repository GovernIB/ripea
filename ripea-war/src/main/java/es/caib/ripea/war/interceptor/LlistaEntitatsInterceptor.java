package es.caib.ripea.war.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import es.caib.ripea.core.api.service.EntitatService;
import es.caib.ripea.war.helper.ContingutEstaticHelper;
import es.caib.ripea.war.helper.EntitatHelper;

/**
 * Interceptor per a gestionar la informació comuna de totes les pagines relacionades amb 
 * l'administració distribuida.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class LlistaEntitatsInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private EntitatService entitatService;
    
    @Override
    public boolean preHandle(
    		HttpServletRequest request,
    		HttpServletResponse response,
    		Object handler) throws Exception {
        if (!ContingutEstaticHelper.isContingutEstatic(request)) {
            EntitatHelper.processarCanviEntitats(request, entitatService);
            EntitatHelper.processarCanviOrganGestor(request);
            EntitatHelper.findEntitatsAccessibles(request, entitatService);
        }
        return true;
    }
}
