package es.caib.ripea.service.intf.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.security.crypto.codec.Base64;

import es.caib.ripea.service.intf.dto.FitxerDto;
import es.caib.ripea.service.intf.dto.InteressatTipusEnum;
import es.caib.ripea.service.intf.dto.PaginacioParamsDto;

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

    /**
     * <p>Comprova si un string té un valor valid.</p>
     *
     * <pre>
     * Utils.hasValue(null)      = false
     * Utils.hasValue("")        = false
     * Utils.hasValue(" ")       = false
     * Utils.hasValue("bob")     = true
     * Utils.hasValue("  bob  ") = true
     * </pre>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if the String is not empty and not null
     */
    public static boolean hasValue(String str) {
    	if (str==null || "".equals(str.trim())) {
    		return false;
    	} else {
    		return true;
    	}
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
	
	public static String addSuffixToFileName(String originalName, String suffix) {
		String fileNameWithoutExtension = originalName.substring(0, originalName.lastIndexOf('.'));
		String fileExtension = originalName.substring(originalName.lastIndexOf('.'));
		return fileNameWithoutExtension + suffix + fileExtension;
	}
	
	public static <T> T[] addElementToArray(T[] objects, T newElement) {
		if (objects!=null) {
			objects = Arrays.copyOf(objects, objects.length + 1);
			objects[objects.length-1]=newElement;
		}
		return objects;
	}
	
	public static Long[] removeElementFromArray(Long[] oldArray, Long oldElement) {
		Long[] newArray = null;
		if (oldArray!=null && oldArray.length>0) {
			newArray = new Long[oldArray.length - 1];
			for (int i = 0, j = 0; i < oldArray.length; i++) {
				if (!oldArray[i].equals(oldElement)) {
					newArray[j++] = oldArray[i];
				}
			}
		}
		return newArray;
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
			LocalDateTime date,
			String format) {
		String str = "";
		if (date != null) {
			str = date.format(DateTimeFormatter.ofPattern(format));
		}
		return str;
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
	
	public static boolean isNumeric(String str) {
		try {
			if (str==null) return false;
			Long.parseLong(str);
			return true;
		} catch (Exception ex) {
			return false;
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
				if (clave.endsWith("endpointName")) {
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
	
	public static String getFileNames(List<FitxerDto> fitxersPerFirmar) {
		String resultat = "";
		if (fitxersPerFirmar!=null) {
			for (FitxerDto fitxerDto: fitxersPerFirmar) {
				if (resultat == "") {
					resultat+=fitxerDto.getNom();
				} else {
					resultat+=", "+fitxerDto.getNom();
				}
			}
		}
		return resultat;
	}
	
	public static Map<Integer, String> peticioRest(String endpoint, String user, String pass)  {
		
		Map<Integer, String> resultat = new HashMap<Integer, String>();
		BufferedReader in = null;
				
		try {
			
			URL url = new URL(endpoint);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setRequestMethod("GET");
	
	        // Añadir la autenticación básica
	        if (user!=null && pass!=null) {
		        String auth = user + ":" + pass;
		        byte[] encodedAuth = Base64.encode(auth.getBytes());
		        String authHeaderValue = "Basic " + new String(encodedAuth);
		        connection.setRequestProperty("Authorization", authHeaderValue);
	        }
	
	        int responseCode = connection.getResponseCode();
	        in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	        String inputLine;
	        StringBuffer response = new StringBuffer();
	        while ((inputLine = in.readLine()) != null) {
	            response.append(inputLine);
	        }
	        
	        resultat.put(responseCode, response.toString());
	        
		} catch (Exception ex) {
			resultat.put(500, ex.getMessage());
		} finally {
			try { if (in!=null) { in.close(); } } catch (Exception ex) {}
		}
		
		return resultat;
	}
	
    public static List<String> getIdsEnGruposMil(List<Long> ids) {
    	int maxSize = 1000;
    	if (ids!=null && ids.size()>0) {
    		List<String> result = new ArrayList<>();
    		for (int i = 0; i < ids.size(); i += maxSize) {
                List<Long> subList = ids.subList(i, Math.min(i + maxSize, ids.size()));
                String concatenated = subList.stream().map(String::valueOf).collect(Collectors.joining(","));
                result.add(concatenated);
            }
    		return result;
    	}
    	return null;
    }
    
    public static List<String> getCodisEnGruposMil(List<String> codis) {
    	int maxSize = 1000;
    	if (codis!=null && !codis.isEmpty()) {
    		List<String> result = new ArrayList<>();
    		for (int i = 0; i < codis.size(); i += maxSize) {
                List<String> subList = codis.subList(i, Math.min(i + maxSize, codis.size()));
                String concatenated = subList.stream().map(value->"'"+value+"'").collect(Collectors.joining(","));
                result.add(concatenated);
            }
    		return result;
    	}
    	return null;
    }
    
	public static String getCodiNom(InteressatTipusEnum tipus, String documentNum, String nom, String llinatge1, String llinatge2, String raoSocial, String organCodi) {
		String resultat = null;
        switch (tipus) {
            case InteressatPersonaFisicaEntity:
            	resultat = documentNum + " - " + getNomComplet(tipus, nom, llinatge1, llinatge2, raoSocial, organCodi);
            default:
            	resultat = getNomComplet(tipus, nom, llinatge1, llinatge2, raoSocial, organCodi);
        }
        return (" - ".equals(resultat)?null:resultat);
    }

	public static String getNomComplet(InteressatTipusEnum tipus, String nom, String llinatge1, String llinatge2, String raoSocial, String organCodi) {
		switch (tipus) {
		case InteressatPersonaFisicaEntity:
			StringBuilder sb = new StringBuilder();
			if (nom != null) {
				sb.append(nom);
			}
			if (llinatge1 != null) {
				sb.append(" ");
				sb.append(llinatge1);
				if (llinatge2 != null) {
					sb.append(" ");
					sb.append(llinatge2);
				}
			}
			return sb.toString();	
		case InteressatPersonaJuridicaEntity:
			return raoSocial;
		case InteressatAdministracioEntity:
			return organCodi;
		default:
			return null;
		}
	}
	
	public static String encripta(String data, String key) {
		try {
	        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
	        Cipher cipher = Cipher.getInstance("AES");
	        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
	        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
	        return java.util.Base64.getEncoder().encodeToString(encryptedBytes).replace("/", "_");
		} catch (Exception ex) {
			return data;
		}
    }
	
	public static List<String> eliminarDuplicados(List<String> lista) {
		if (lista==null) return null;
        return new ArrayList<>(new LinkedHashSet<>(lista)); // Set elimina duplicados y mantiene el orden
    }
	
	public static String desencripta(String encryptedData, String key) {
		try {
	        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
	        Cipher cipher = Cipher.getInstance("AES");
	        cipher.init(Cipher.DECRYPT_MODE, secretKey);
	        byte[] decryptedBytes = cipher.doFinal(java.util.Base64.getDecoder().decode(encryptedData.replace("_", "/")));
	        return new String(decryptedBytes);
		} catch (Exception ex) {
			return encryptedData;
		}	        
    }
	
	public static Map<String, String> namedQueriesToMap(String[] namedQueries) {
    	Map<String, String> mapaNamedQueries = new HashMap<String, String>();
    	if (namedQueries!=null && namedQueries.length>0) {
	        for (String namedQuery : namedQueries) {
	            String[] split = namedQuery.split("#");
	            int i = split.length;
	            if (i==1) {
	            	mapaNamedQueries.put(split[0], null);
	            } else if (i==2) {
	            	mapaNamedQueries.put(split[0], split[1]);
	            }
	        }
    	}
        return mapaNamedQueries;
	}
	
	public static void addSortDefault(PaginacioParamsDto paginacioParams, String camp) {
		boolean isOrderedByNom = false;
		if (paginacioParams.getOrdres() != null && !paginacioParams.getOrdres().isEmpty()) {
			for(PaginacioParamsDto.OrdreDto ordre : paginacioParams.getOrdres()) {
				if (camp.equals(ordre.getCamp())) {
					isOrderedByNom = true;
					break;
				}
			}
		}
		if (!isOrderedByNom) {
			paginacioParams.getOrdres().add(new PaginacioParamsDto.OrdreDto(camp, PaginacioParamsDto.OrdreDireccioDto.ASCENDENT));
		}
	}
	
	public static String nifMask(String nif) {
        if (nif == null || nif.length() < 5) return nif;
        // Buscar la última letra (asumimos que es la última posición del string)
        char ultimaLetra = nif.charAt(nif.length() - 1);
        // Extraer la parte numérica antes de la letra
        String cuerpo = nif.substring(0, nif.length() - 1);
        // Buscar dígitos de derecha a izquierda para encontrar los últimos 4
        StringBuilder resultado = new StringBuilder();
        int contador = 0;

        for (int i = cuerpo.length() - 1; i >= 0; i--) {
            char c = cuerpo.charAt(i);
            if (Character.isDigit(c) && contador < 4) {
                resultado.insert(0, '*');
                contador++;
            } else {
                resultado.insert(0, c);
            }
        }

        // Añadir de nuevo la letra
        resultado.append(ultimaLetra);

        return resultado.toString();
    }
}