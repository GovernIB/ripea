/**
 * 
 */
package es.caib.ripea.war.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.service.EntitatService;
import es.caib.ripea.war.helper.EntitatHelper;



public class BaseUserOAdminOOrganController extends BaseController {

	@Autowired
	private EntitatService entitatService;
	
	public EntitatDto getEntitatActualComprovantPermisos(
			HttpServletRequest request) {
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
		if (entitat == null) {
			throw new SecurityException("No te cap entitat assignada");
		}
		
		if (!entitat.isUsuariActualRead() && !entitat.isUsuariActualAdministration() && !entitat.isUsuariActualTeOrgans()) {
			throw new SecurityException("No te permisos per accedir a aquesta entitat com a usuari o administrador d'entitat o administrator de l'organ");
		}
		return entitat;
	}

}
