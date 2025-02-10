/**
 * 
 */
package es.caib.ripea.back.helper;

import es.caib.ripea.service.intf.dto.AvisDto;
import es.caib.ripea.service.intf.dto.EntitatDto;
import es.caib.ripea.service.intf.service.AvisService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
			if (RolHelper.isRolActualAdministrador(request)) {
				EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
				avisos = avisService.findActiveAdmin(entitatActual.getId());
			}
			else
				avisos = avisService.findActive();
			request.setAttribute(REQUEST_PARAMETER_AVISOS, avisos);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static List<AvisDto> getAvisos(
			HttpServletRequest request) {
		return (List<AvisDto>) request.getAttribute(REQUEST_PARAMETER_AVISOS);
	}
	

}
