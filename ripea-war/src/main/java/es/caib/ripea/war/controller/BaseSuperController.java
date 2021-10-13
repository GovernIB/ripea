/**
 * 
 */
package es.caib.ripea.war.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.service.MetaExpedientService;
import es.caib.ripea.war.helper.EntitatHelper;

/**
 * Controlador base que implementa funcionalitats comunes per als controladors
 * de les accions de l'administrador.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BaseSuperController extends BaseController {

	@Autowired
	public MetaExpedientService metaExpedientService;
	

	public EntitatDto getEntitatActual(
			HttpServletRequest request) {
		EntitatDto entitat = EntitatHelper.getEntitatActual(request);
		if (entitat == null) {
			throw new SecurityException("No te cap entitat assignada");
		}

		return entitat;
	}
	

}
