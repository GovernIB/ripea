/**
 * 
 */
package es.caib.ripea.war.interceptor;

import es.caib.ripea.core.api.dto.UsuariDto;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.war.helper.RolHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interceptor per controlar l'accés a funcionalitat desde el rol d'admin d'entitat o d'admin d'organ o revisor.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AccesAdminEntitatOAdminOrganORevisorInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private AplicacioService aplicacioService;


	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		if (!RolHelper.isRolActualAdministrador(request) && !RolHelper.isRolActualAdministradorOrgan(request) && !RolHelper.isRolActualRevisor(request)) {
			UsuariDto usuariActual = aplicacioService.getUsuariActual();
			throw new SecurityException("Es necessari ser administrador d'entitat o administrador d'òrgan gestor o revisor de procediments per accedir a aquesta página. " +
					"L'usuari actual " + usuariActual.getCodi() + " no té cap rol requerit.", null);
		}
		return true;
	}

}