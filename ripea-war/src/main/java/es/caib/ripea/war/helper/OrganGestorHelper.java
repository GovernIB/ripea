/**
 * 
 */
package es.caib.ripea.war.helper;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.OrganGestorDto;
import es.caib.ripea.core.api.service.OrganGestorService;


public class OrganGestorHelper {

	private static final String ORGANS_ACCESSIBLES= "organs.accessiblesUsuari";
	
	
	
	public static void findOrganismesEntitatAmbPermisCache(HttpServletRequest request, OrganGestorService organGestorService) {
		EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
		if (organGestorService != null && entitatActual != null) {
			List<OrganGestorDto> organs = organGestorService.findOrganismesEntitatAmbPermisCache(entitatActual.getId());
			request.setAttribute(ORGANS_ACCESSIBLES, organs);
		}
	}
	

}
