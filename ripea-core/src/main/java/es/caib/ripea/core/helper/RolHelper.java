/**
 * 
 */
package es.caib.ripea.core.helper;

import org.springframework.stereotype.Component;


@Component
public class RolHelper {
	
	public static boolean isAdminEntitat(String rolActual) {
		if (rolActual != null && rolActual.equals("IPA_ADMIN")) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean isAdminOrgan(String rolActual) {
		if (rolActual != null && rolActual.equals("IPA_ORGAN_ADMIN")) {
			return true;
		} else {
			return false;
		}
	}


}
