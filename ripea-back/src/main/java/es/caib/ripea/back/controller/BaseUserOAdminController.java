/**
 * 
 */
package es.caib.ripea.back.controller;

import es.caib.ripea.back.helper.EntitatHelper;
import es.caib.ripea.service.intf.dto.EntitatDto;

import javax.servlet.http.HttpServletRequest;


/**
 * Controlador base que implementa funcionalitats comunes per
 * als controladors de les accions que son tant d'usuari com
 * d'administrador.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BaseUserOAdminController extends BaseController {

	public EntitatDto getEntitatActualComprovantPermisos(
			HttpServletRequest request) {
		EntitatDto entitat = EntitatHelper.getEntitatActual(request);
		if (entitat == null)
			throw new SecurityException("No te cap entitat assignada");
		if (!entitat.isUsuariActualAdministration() && !entitat.isUsuariActualRead())
			throw new SecurityException("No te permisos per accedir a aquesta entitat com a administrador o usuari");
		return entitat;
	}

}
