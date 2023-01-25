package es.caib.ripea.core.api.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class Utils {

	
	public static List<Long> geValueOrNull(List<Long> objects) {
		return objects == null || objects.isEmpty() ? null : objects;
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
	
	public static String getTrimOrNull(String value) {
		return value == null || value.isEmpty() ? null : value.trim();
	}
	
}
