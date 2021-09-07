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
 * Interceptor per controlar l'accés a funcionalitat d'administradors.
 * Un administrador pot ser d'entitat, d'òrgan gestor
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AccesAdminInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private AplicacioService aplicacioService;


	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		if (!RolHelper.isRolActualAdministrador(request) && !RolHelper.isRolActualAdministradorOrgan(request)) {
			UsuariDto usuariActual = aplicacioService.getUsuariActual();
			throw new SecurityException("Es necessari ser administrador d'òrgan gestor o d'entitat per accedir a aquesta página. " +
					"L'usuari actual " + usuariActual.getCodi() + " no té cap rol requerit.", null);
		}
		return true;
	}

}
