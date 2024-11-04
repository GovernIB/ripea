package es.caib.ripea.core.api.utils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

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
     * @param st  the String to check, may be null
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
     * @param st  the String to check, may be null
     * @return <code>true</code> if the String is empty or null
     */
	public static boolean isEmpty(final String st) {
		return StringUtils.isEmpty(st);
	}

	public static boolean isBlank(final String st) {
		return StringUtils.isBlank(st);
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
	
	public static String diesRestantsToString(Date dataLimit) {
		if (dataLimit!=null) {
			Calendar dFin = Calendar.getInstance();
			dFin.setTime(dataLimit);
			Calendar dAvui = Calendar.getInstance();
			if (dFin.before(dAvui)) {
				return "Data límit expirada.";
			} if (mateixDia(dFin, dAvui)) {
				return "La data límit és avui.";
			} else {
				long diffInMillies = dFin.getTimeInMillis()-dAvui.getTimeInMillis();
				long diasDiferencia = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS)+1;
				return "Falten "+diasDiferencia+" dies.";
			}
		}
		return null;
	}
	
	public static boolean mateixDia(Calendar data1, Calendar data2) {
		if (data1!=null && data2!=null) {
			if (data1.get(Calendar.DAY_OF_MONTH)==data2.get(Calendar.DAY_OF_MONTH) &&
				data1.get(Calendar.MONTH)==data2.get(Calendar.MONTH) &&
				data1.get(Calendar.YEAR)==data2.get(Calendar.YEAR)) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}
	
	public static void truncarDate(Calendar dataLimit) {
		if (dataLimit!=null) {
			dataLimit.set(Calendar.HOUR_OF_DAY, 0);
			dataLimit.set(Calendar.MINUTE, 0);
			dataLimit.set(Calendar.SECOND, 0);
			dataLimit.set(Calendar.MILLISECOND, 0);
		}
	}
	
	public static String duracioEnDiesToString(Integer dies) {
		
		if (dies!=null) {
		
			try {
			
				if (dies>0) {
					
					int weeks = dies / 7;
					int remainingDays = dies % 7;
	
					String retorn = "";
					if (weeks>0) {
						if (weeks==1) {
							retorn += weeks + " setmana";
						} else {
							retorn += weeks + " setmanes";
						}
					}
					if (remainingDays>0) {
						if (remainingDays==1) {
							retorn += " i " + remainingDays + " dia";
						} else {
							retorn += " i " + remainingDays + " dies";
						}
					}
					
					if (retorn.startsWith(" i ")) {
						return retorn.substring(3, retorn.length())+".";
					} else {
						return retorn+".";
					}			
					
				} else {
					return "El mateix dia.";
				}
			} catch (Exception ex) {
				return dies + " dies.";
			}

		} else {
			return "";
		}
	}
	
	public static boolean sonValorsDiferentsControlantNulls(Object valor1, Object valor2) {
		if (valor1 == null && valor2 == null) {
			return false; // Ambos son nulos, no ha cambiado
		}

		if (valor1 == null || valor2 == null) {
			return true; // Uno es nulo y el otro no, ha cambiado
		}

		return !valor1.equals(valor2); // Comparación de valores no nulos
	}
	
	public static String extractNumbers(String str) {
		if (str==null) return null;
		StringBuilder numbers = new StringBuilder();
		for (char c : str.toCharArray()) {
			if (Character.isDigit(c)) {
				numbers.append(c);
			}
		}
		return numbers.toString();
	}
	
	public static String getEndpointNameFromProperties(Properties propiedades) {
		String valorEndpoint = null;
		String valorURL = null;
		String comodin = null;
		if (propiedades!=null) {
			Enumeration<?> nombres = propiedades.propertyNames();
			while (nombres.hasMoreElements()) {
				String clave = (String) nombres.nextElement();
				String valor = propiedades.getProperty(clave);
				if (clave.endsWith("endpoint")) {
					valorEndpoint = valor;
				} else if (clave.endsWith("url")) {
					valorURL = valor;
				} else if(valor.startsWith("http")) {
					comodin = valor;
				}
			}
		}
		//Si hem trobat alguna property que acabi amb "endpoint" la retornam
		if (Utils.isNotEmpty(valorEndpoint)) { return valorEndpoint; }
		//Si no, retornam la que haguem trobat que acabi amb URL
		if (Utils.isNotEmpty(valorURL)) { return valorURL; }
		//Com darrera opció, retornam la que començi per http 
		if (Utils.isNotEmpty(comodin)) { return comodin; }
		//Finalment si no hem trobat res, retornam null.
		return null;
	}
}