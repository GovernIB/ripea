/**
 * 
 */
package es.caib.ripea.back.interceptor;

import es.caib.ripea.back.helper.RolHelper;
import es.caib.ripea.service.intf.dto.UsuariDto;
import es.caib.ripea.service.intf.service.AplicacioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interceptor per controlar l'accés a funcionalitat desde el rol d'admin d'entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class AccesAdminEntitatInterceptor implements AsyncHandlerInterceptor {

	@Autowired
	private AplicacioService aplicacioService;

	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		if (!RolHelper.isRolActualAdministrador(request) && !RolHelper.isRolActualDissenyadorOrgan(request)) {
			UsuariDto usuariActual = aplicacioService.getUsuariActual();
			throw new SecurityException("Es necessari ser administrador d'entitat per accedir a aquesta página. " +
					"L'usuari actual " + usuariActual.getCodi() + " no té cap rol requerit.", null);
		}
		return true;
	}

}
