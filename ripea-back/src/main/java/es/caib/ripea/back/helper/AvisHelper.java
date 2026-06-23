/**
 * 
 */
package es.caib.ripea.back.helper;

import es.caib.ripea.service.intf.dto.AvisDto;
import es.caib.ripea.service.intf.dto.EntitatDto;
import es.caib.ripea.service.intf.service.AvisService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utilitat per obtenir els avisos de sessió..
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AvisHelper {

	private static final String REQUEST_PARAMETER_AVISOS = "AvisHelper.findAvisos";


	@SuppressWarnings("unchecked")
	public static void findAvisos(
			HttpServletRequest request, 
			AvisService avisService) {
		
		List<AvisDto> avisos = (List<AvisDto>) request.getAttribute(REQUEST_PARAMETER_AVISOS);
		boolean canviRol = request.getParameter(RolHelper.getRequestParameterCanviRol()) != null;
		if ((avisos == null && !RequestHelper.isError(request) && avisService != null) || canviRol) {
			// Superusuaris (L'únic que no té entitat assignada)
			if (RolHelper.isRolActualSuperusuari(request)) {
				avisos = avisService.findActiveGlobal();
			} else {
				EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
				Long entitatId = (entitatActual != null) ? entitatActual.getId() : null;
				avisos = avisService.findActivePerEntitat(entitatId);
			}
			request.setAttribute(REQUEST_PARAMETER_AVISOS, avisos);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static List<AvisDto> getAvisos(
			HttpServletRequest request) {
		return (List<AvisDto>) request.getAttribute(REQUEST_PARAMETER_AVISOS);
	}
	

}
