package es.caib.ripea.core.api.utils;

import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

public class Utils {

	
	
	public static String trim(String value) { 
		return StringUtils.trimToNull(value);
	}
	
	public static boolean isNotNullAndEquals(String object1, String object2) {
		return object1 != null && object2 != null && object1.equals(object2);
	}
	
	public static boolean equals(String str1, String str2) {
		return StringUtils.equals(str1, str2);
	}
	
	public static boolean notEquals(String str1, String str2) {
		return !StringUtils.equals(str1, str2);
	}
	
    public static boolean isNotEmpty(final Collection<?> coll) {
       return CollectionUtils.isNotEmpty(coll);
    }
    
    public static boolean isNotEmpty(final String st) {
        return StringUtils.isNotEmpty(st);
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
