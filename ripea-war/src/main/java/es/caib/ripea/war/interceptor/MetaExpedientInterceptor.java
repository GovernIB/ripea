/**
 * 
 */
package es.caib.ripea.war.interceptor;

import es.caib.ripea.core.api.service.MetaExpedientService;
import es.caib.ripea.war.helper.MetaExpedientHelper;
import es.caib.ripea.war.helper.RolHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class MetaExpedientInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private MetaExpedientService metaExpedientService;

	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		
		MetaExpedientHelper.setRevisioActiva(
				request, 
				metaExpedientService);

		if (RolHelper.isRolActualAdministrador(request) || RolHelper.isRolActualAdministradorOrgan(request)) {
			MetaExpedientHelper.setOrgansNoSincronitzats(request, metaExpedientService);
		}
		return true;
	}

}
