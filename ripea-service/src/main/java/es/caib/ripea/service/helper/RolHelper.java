package es.caib.ripea.service.helper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class RolHelper {
	
	@Autowired private CacheHelper cacheHelper;
	
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
	
	
	public boolean doesCurrentUserHasRol(
			String rolToCheck) {

		boolean hasRol = false;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		List<String> rols = cacheHelper.findRolsAmbCodi(auth.getName());
		if (rols != null) {
			for (String rol : rols) {
				if (rol.equals(rolToCheck)) {
					hasRol = true;
				}
			}
		}
		return hasRol;
	}

}
