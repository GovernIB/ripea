/**
 * 
 */
package es.caib.ripea.war.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.service.MetaExpedientService;
import es.caib.ripea.war.helper.EntitatHelper;
import es.caib.ripea.war.helper.RolHelper;


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
