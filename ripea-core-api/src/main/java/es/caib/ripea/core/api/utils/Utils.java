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
    
    /**
     * <p>Checks if a String is not empty ("") and not null.</p>
     *
     * <pre>
     * StringUtils.isNotEmpty(null)      = false
     * StringUtils.isNotEmpty("")        = false
     * StringUtils.isNotEmpty(" ")       = true
     * StringUtils.isNotEmpty("bob")     = true
     * StringUtils.isNotEmpty("  bob  ") = true
     * </pre>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if the String is not empty and not null
     */
	public static boolean isNotEmpty(final String st) {
		return StringUtils.isNotEmpty(st);
	}
	
    /**
     * <p>Checks if a String is empty ("") or null.</p>
     *
     * <pre>
     * StringUtils.isEmpty(null)      = true
     * StringUtils.isEmpty("")        = true
     * StringUtils.isEmpty(" ")       = false
     * StringUtils.isEmpty("bob")     = false
     * StringUtils.isEmpty("  bob  ") = false
     * </pre>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if the String is empty or null
     */
	public static boolean isEmpty(final String st) {
		return StringUtils.isEmpty(st);
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
