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
 * Interceptor per controlar l'accés a funcionalitat desde el rol de superusuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AccesSuperInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private AplicacioService aplicacioService;


	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		UsuariDto usuariActual = aplicacioService.getUsuariActual();
		if (!RolHelper.isRolActualSuperusuari(request))
			throw new SecurityException("Es necessari el rol de superusuari per accedir a aquesta página. L'usuari actual " + usuariActual.getCodi() + " no té el rol.", null);
		
		return true;
	}

}

