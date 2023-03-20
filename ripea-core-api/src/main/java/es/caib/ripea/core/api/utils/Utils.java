package es.caib.ripea.core.api.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class Utils {

	
	public static List<Long> geValueOrNull(List<Long> objects) { //TODO: remove and use getNullIfEmpty() instead
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
	
	public static String getTrimOrNull(String value) { // TODO: remove and replace by StringUtils.trim()
		return value == null || value.isEmpty() ? null : value.trim();
	}
	
	public static boolean isNotNullAndEqual(String object1, String object2) {
		return object1 != null && object2 != null && object1.equals(object2);
	}
	
	
	/**
	 * Hibernate doesn't support empty collection as parameter for "IN" operator [WHERE column_name IN ()]
	 * and if it is empty it throws org.hibernate.hql.ast.QuerySyntaxException: unexpected end of subtree
	 */
	public static <T> List<T> getNullIfEmpty(List<T> objects) {
		if (CollectionUtils.isEmpty(objects)) {
			return null;
		} else {
			return objects;
		}
	}
	
	
}
