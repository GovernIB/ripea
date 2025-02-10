/**
 * 
 */
package es.caib.ripea.back.controller;

import es.caib.ripea.back.helper.EntitatHelper;
import es.caib.ripea.back.helper.RolHelper;
import es.caib.ripea.service.intf.dto.EntitatDto;
import es.caib.ripea.service.intf.dto.MetaExpedientDto;
import es.caib.ripea.service.intf.service.MetaExpedientService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;


/**
 * Controlador base que implementa funcionalitats comunes per
 * als controladors de les accions que son tant de revisor com
 * d'administrador.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BaseAdminORevisorController extends BaseController {

	@Autowired
	public MetaExpedientService metaExpedientService;
	
	public EntitatDto getEntitatActualComprovantPermisos(
			HttpServletRequest request) {
		EntitatDto entitat = EntitatHelper.getEntitatActual(request);
		if (entitat == null) {
			throw new SecurityException("No te cap entitat assignada");
		}
		
		if (!entitat.isUsuariActualAdministration() && !RolHelper.isRolActualRevisor(request)) {
			throw new SecurityException("No te permisos per accedir a aquesta entitat com a administrador o revisor");
		}
		return entitat;
	}
	
	public MetaExpedientDto comprovarAccesMetaExpedient(HttpServletRequest request, Long metaExpedientId) {
		EntitatDto entitat = EntitatHelper.getEntitatActual(request);
		if (!entitat.isUsuariActualAdministration()) {
				
		}	
		MetaExpedientDto metaExpedient = metaExpedientService.findById(entitat.getId(), metaExpedientId);
		return metaExpedient;
	}

}
