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

/**
 * Controlador base que implementa funcionalitats comunes per als controladors
 * de les accions de l'administrador.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BaseAdminController extends BaseController {

	@Autowired
	public MetaExpedientService metaExpedientService;
	
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

	protected MetaExpedientDto comprovarAccesMetaExpedient(HttpServletRequest request, Long metaExpedientId) {
		EntitatDto entitat = EntitatHelper.getEntitatActual(request);
		if (!entitat.isUsuariActualAdministration()) {
				
		}	
		MetaExpedientDto metaExpedient = metaExpedientService.getAndCheckAdminPermission(entitat.getId(), metaExpedientId);
		return metaExpedient;
	}
}
