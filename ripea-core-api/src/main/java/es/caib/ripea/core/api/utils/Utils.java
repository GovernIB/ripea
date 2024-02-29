package es.caib.ripea.core.api.utils;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

public class Utils {


	
	public static String trim(String value) { 
		return StringUtils.trimToNull(value);
	}
	
	public static String surroundWithParenthesis(String value) {
		String newValue = "";
		if (isNotEmpty(value)) {
			newValue = "(" + value + ")";
		}
		return newValue;
	}
	
	public static String abbreviate(String value, int maxWidth) { 
		return StringUtils.abbreviate(value, maxWidth);
	}
	
	public static boolean isNotNullAndEquals(String object1, String object2) {
		return object1 != null && object2 != null && object1.equals(object2);
	}
	

	public static boolean equals(String str1, String str2) {
		return StringUtils.equals(str1, str2);
	}
	
	public static boolean equalsIgnoreCase(String str1, String str2) {
		return StringUtils.equalsIgnoreCase(str1, str2);
	}
	
	public static boolean containsIgnoreCase(String str1, String str2) {
		return StringUtils.containsIgnoreCase(str1, str2);
	}
	
	public static boolean notEquals(String str1, String str2) {
		return !StringUtils.equals(str1, str2);
	}
	
    public static boolean isEmpty(final Collection<?> coll) {
        return CollectionUtils.isEmpty(coll);
     }

    public static boolean isNotEmpty(final Collection<?> coll) {
       return CollectionUtils.isNotEmpty(coll);
    }
    public static boolean isBiggerThan(final Collection<?> coll, int size) {
        return CollectionUtils.isNotEmpty(coll) && coll.size() > size;
    }
    
    public static boolean isBiggerThan( String string, int size) {
		return isNotEmpty(string) && string.length() > size;
    }
    
    public static List<Long> toListLong(List<Serializable> original) {
		List<Long> listLong = new ArrayList<Long>(original.size());
		for (Serializable s: original) { 
			listLong.add((Long)s); 
		}
		return listLong;
	}

	public static <T> List<T> getUniqueValues(List<T> objects) {
		if (objects != null) {
			return new ArrayList<T>(new HashSet<T>(objects));
		} else {
			return null;
		}
	}
	
	public static <T> T getFirst(List<T> objects) {
		if (isNotEmpty(objects)) {
			return objects.get(0);
		} else {
			return null;
		}
	}
	
	public static <T> T getLast(List<T> objects) {
		if (isNotEmpty(objects)) {
			return objects.get(objects.size() - 1);
		} else {
			return null;
		}
	}
	
	public static <S extends Enum<S>, D extends Enum<D>> D convertEnum(S source, Class<D> destinationClass) {
		try {
			if (source != null) {
				return Enum.valueOf(destinationClass, source.toString());
			} else {
				return null;
			}
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
	
	
    public static String toString(Object obj) {
        return String.valueOf(obj);
    }
    
	public static <T> int getSize(List<T> objects) {
		if (isNotEmpty(objects)) {
			return objects.size();
		} else {
			return 0;
		}
	}
	
	public static <T> void removeLast(List<T> objects) {
		if (isNotEmpty(objects)) {
			objects.remove(objects.size() - 1);
		}
	}

	public static boolean isNotEmpty(final byte[] array) {
		return ArrayUtils.isNotEmpty(array);
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
	
	public static boolean isEmpty(byte[] bytes) {
		return ArrayUtils.isEmpty(bytes);
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
	
	
	/**
	 * PostgreSQL doesn't allow null value for string parameter
	 */
	public static String getEmptyStringIfNull(String str) {
		if (str == null) {
			return "";
		} else {
			return str.trim();
		}
	}
	

	public static String getTamanyString(Long value) {
		String[] tamanyUnitats = {"B", "KB", "MB", "GB", "TB", "PB"};
		String valueStr = null;
		if (value != null) {
			double valor = value;
			int i = 0;
			while (value > Math.pow(1024, i + 1) 
					&& i < tamanyUnitats.length - 1) {
				valor = valor / 1024;
				i++;
			}
			DecimalFormat df = new DecimalFormat("#,###.##");
			return df.format(valor) + " " + tamanyUnitats[i];
		}
		
		return valueStr;
		
	}

	public static Throwable getRootCauseOrItself(Throwable e) {
		if (e != null) {
			return ExceptionUtils.getRootCause(e) != null ? ExceptionUtils.getRootCause(e) : e;
		} else {
			return null;
		}
	}
	
	public static String getRootMsg(Throwable e) {
		Throwable throwable = getRootCauseOrItself(e);
		String msg = "";
		if (throwable != null) {
			if (isNotEmpty(throwable.getMessage())) {
				msg = throwable.getMessage();
			} else {
				msg = throwable.getClass().toString();
			}
		}
		return msg;
	}

	
	public static Date convertStringToDate(String str, String format) {
        Date date = null;
        if (isNotEmpty(str)) {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
    		try {
    			date = sdf.parse(str);
    		} catch (ParseException e) {
    			throw new RuntimeException(e);
    		}
		} 
		return date;
	}
	
	
	public static String convertDateToString(
			Date date,
			String format) {
		String str = "";
		if (date != null) {
			SimpleDateFormat sdtTime = new SimpleDateFormat(format);
			str = sdtTime.format(date);
		}
		return str;
	}
	
}
