/**
 * 
 */
package es.caib.ripea.back.controller;

import es.caib.ripea.back.helper.EntitatHelper;
import es.caib.ripea.service.intf.dto.EntitatDto;
import es.caib.ripea.service.intf.service.MetaExpedientService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

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
