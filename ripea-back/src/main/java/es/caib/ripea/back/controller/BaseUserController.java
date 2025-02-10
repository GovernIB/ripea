/**
 * 
 */
package es.caib.ripea.back.controller;

import es.caib.ripea.back.helper.EntitatHelper;
import es.caib.ripea.service.intf.dto.EntitatDto;
import es.caib.ripea.service.intf.service.EntitatService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;


/**
 * Controlador base que implementa funcionalitats comunes per
 * als controladors de les accions de l'usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BaseUserController extends BaseController {

	@Autowired private EntitatService entitatService;

	public EntitatDto getEntitatActualComprovantPermisos(
			HttpServletRequest request) {
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
		if (entitat == null) {
			throw new SecurityException("No te cap entitat assignada. Contactau amb l'administrador");
		}
		if (entitat.getCodi() == null) {
			throw new SecurityException("La entitat assignada no te codi. Contactau amb l'administrador");
		}
		if (!entitat.isUsuariActualRead()) {
			throw new SecurityException("No te permisos per accedir a aquesta entitat com a usuari. Contactau amb l'administrador");
		}
		return entitat;
	}
	
	
	
	
	
	

}
