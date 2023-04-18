/**
 * 
 */
package es.caib.ripea.core.helper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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

	
	public static List<String> getRolsCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		List<String> rolsCurrentUser = new ArrayList<String>();
		for (GrantedAuthority ga : auth.getAuthorities())
			rolsCurrentUser.add(ga.getAuthority());
		if (rolsCurrentUser.isEmpty()) {
			rolsCurrentUser = null; 
		}
		
		return rolsCurrentUser;
	}

}
