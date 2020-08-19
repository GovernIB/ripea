/**
 * 
 */
package es.caib.ripea.war.controller;

import javax.servlet.http.HttpServletRequest;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.war.helper.EntitatHelper;

/**
 * Controlador base que implementa funcionalitats comunes per als controladors
 * de les accions de l'administrador.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BaseAdminController extends BaseController {

	public EntitatDto getEntitatActualComprovantPermisAdminEntitat(HttpServletRequest request) {
		EntitatDto entitat = EntitatHelper.getEntitatActual(request);
		if (entitat == null)
			throw new SecurityException("No te cap entitat assignada");
		if (!entitat.isUsuariActualAdministration())
			throw new SecurityException("No te permisos per accedir a aquesta entitat com a administrador");
		return entitat;
	}

	public EntitatDto getEntitatActualComprovantPermisAdminEntitatOrgan(HttpServletRequest request) {
		EntitatDto entitat = EntitatHelper.getEntitatActual(request);
		if (entitat == null) {
			throw new SecurityException("No te cap entitat assignada");
		}

		if (!entitat.isUsuariActualAdministrationOrgan()) {
			throw new SecurityException("No te permisos per accedir a aquesta entitat com a administrador");
		}
		return entitat;
	}

	public EntitatDto getEntitatActualComprovantPermisAdminEntitatOrPermisAdminEntitatOrgan(
			HttpServletRequest request) {
		EntitatDto entitat = EntitatHelper.getEntitatActual(request);
		if (entitat == null) {
			throw new SecurityException("No te cap entitat assignada");
		}
		if (!entitat.isUsuariActualAdministration() && !entitat.isUsuariActualAdministrationOrgan()) {
			throw new SecurityException("No te permisos per accedir a aquesta entitat com a administrador");
		}
		return entitat;
	}

}
