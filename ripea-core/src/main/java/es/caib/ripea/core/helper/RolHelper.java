/**
 * 
 */
package es.caib.ripea.core.helper;

import org.springframework.stereotype.Component;


@Component
public class RolHelper {
	
	public static boolean isAdmin(String rolActual) {
		if (rolActual != null && rolActual.equals("IPA_ADMIN")) {
			return true;
		} else {
			return false;
		}
	}


}
