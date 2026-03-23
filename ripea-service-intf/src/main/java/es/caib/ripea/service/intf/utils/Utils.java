package es.caib.ripea.service.intf.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.security.crypto.codec.Base64;

import es.caib.ripea.service.intf.dto.DocumentFirmaTipusEnumDto;
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
	
	public static Date localDateTimeToDateJava(LocalDateTime localDateTime) {
		if (localDateTime==null) return null;
		ZoneId zone = ZoneId.systemDefault();
		return Date.from(localDateTime.atZone(zone).toInstant());
	}
	
	public static LocalDateTime dateJavaToLocalDateTime(Date javaIUtilDate) {
		return javaIUtilDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
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
	
	public static String getIdsSeparatsComa(List<Long> ids) {
		if (ids!=null) {
			return ids.stream()
	                .map(String::valueOf)   // convierte cada Long a String
	                .collect(Collectors.joining(",")); // une con comas
		} else {
			return "null";
		}
	}
	
	public static Long[] csvToLongArray(String csv) {
	    if (csv == null || csv.trim().isEmpty()) {
	        return new Long[0]; // Devuelve array vacío si la entrada es nula o vacía
	    }

	    return Arrays.stream(csv.split(","))
	                 .map(String::trim)          // elimina espacios
	                 .filter(s -> !s.isEmpty())  // evita valores vacíos
	                 .map(Long::valueOf)         // convierte a Long
	                 .toArray(Long[]::new);      // crea el array
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
    
	public static String getCodiNom(InteressatTipusEnum tipus, String documentNum, String nom, String llinatge1, String llinatge2, String raoSocial, String organNom) {
		String resultat = documentNum + " - " + getNomComplet(tipus, nom, llinatge1, llinatge2, raoSocial, organNom);
        return (" - ".equals(resultat)?null:resultat);
    }

	public static String getNomComplet(InteressatTipusEnum tipus, String nom, String llinatge1, String llinatge2, String raoSocial, String organNom) {
		if (tipus!=null) {
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
					return Utils.hasValue(nom)?nom:Utils.hasValue(organNom)?organNom:"Administració";
				default:
					return null;
			}
		} else {
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
	
	public static List<Long> eliminarDuplicadosLista(List<Long> lista) {
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
	
	public static String findKeyStartingWith(Map<String, String> mapaNamedQueries, String prefix) {
	    for (String key : mapaNamedQueries.keySet()) {
	        if (key.startsWith(prefix)) {
	            return key;
	        }
	    }
	    return null;
	}
	
	public static PaginacioParamsDto sensePaginacio() {
		PaginacioParamsDto sensePaginacio = new PaginacioParamsDto();
		sensePaginacio.setPaginaNum(0);
		sensePaginacio.setPaginaTamany(Integer.MAX_VALUE);
		addSortDefault(sensePaginacio, "id");
		return sensePaginacio;
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
	
	public static final String DNI_O_NIE_STRING_ASOCIATION = "TRWAGMYFPDXBNJZSQVHLCKE";
	public static String lletraDniONie(int dni) {
		return "" + DNI_O_NIE_STRING_ASOCIATION.charAt(dni % 23);
	}

	private static final Pattern dniPattern = Pattern.compile("[0-9]{8}[A-Z]");
	public static boolean validacioDni(String dni) {
		if (!dniPattern.matcher(dni).matches()) {
			return false;
		}
		String nums = dni.substring(0, 8);
		String lletra = dni.substring(8);
		return lletra.equals(lletraDniONie(new Integer(nums).intValue()));
	}

	private static final Pattern niePattern = Pattern.compile("[XYZ][0-9]{7}[A-Z]");
	public static boolean validacioNie(String nie) {
		if (!niePattern.matcher(nie).matches()) {
			return false;
		}
		String nums = (char)(nie.charAt(0) - 40) + nie.substring(1, 8);
		String lletra = nie.substring(8);
		return lletra.equals(lletraDniONie(new Integer(nums).intValue()));
	}
	
	private static final Pattern eidasPattern = Pattern.compile("^[A-Z]{2}/[A-Z]{2}/[A-Za-z0-9]{1,30}$");
	public static boolean validacioEidas(String eidas) {
		if (!eidasPattern.matcher(eidas).matches()) {
			return false;
		}
		return true;
	}
	
	// ========== Validació CIF ================
	// Només s'admeten nombres com a caràcter de control
	private static final String CONTROL_SOLO_NUMEROS = "ABEH";
	// Només s'admeten lletres com a caràcter de control
	private static final String CONTROL_SOLO_LETRAS = "KPQS";
	// Conversió de dígit a lletra de control
	private static final String CONTROL_NUMERO_A_LETRA = "JABCDEFGHI";
	private static final Pattern cifPattern = Pattern.compile("[[A-H][J-N][P-S]UVW][0-9]{7}[0-9A-J]");
	
	public static boolean validacioCif(String cif) {
		if (!cifPattern.matcher(cif).matches()) {
			return false;
		}
		int parA = 0;
		for (int i = 2; i < 8; i += 2) {
			final int digito = Character.digit(cif.charAt(i), 10);
			if (digito < 0) {
				return false;
			}
			parA += digito;
		}
		int nonB = 0;
		for (int i = 1; i < 9; i += 2) {
			final int digito = Character.digit(cif.charAt(i), 10);
			if (digito < 0) {
				return false;
			}
			int nn = 2 * digito;
			if (nn > 9) {
				nn = 1 + (nn - 10);
			}
			nonB += nn;
		}
		final int parcialC = parA + nonB;
		final int digitoE = parcialC % 10;
		final int digitoD = (digitoE > 0) ? (10 - digitoE) : 0;
		final char letraIni = cif.charAt(0);
		final char caracterFin = cif.charAt(8);
		final boolean esControlValido =
				// El caràcter de control es vàlid com a lletra?
				(CONTROL_SOLO_NUMEROS.indexOf(letraIni) < 0 && CONTROL_NUMERO_A_LETRA.charAt(digitoD) == caracterFin) ||
				// El caràcter de control es vàlid com a dígit?
				(CONTROL_SOLO_LETRAS.indexOf(letraIni) < 0 && digitoD == Character.digit(caracterFin, 10));
		return esControlValido;
	}
	
	public static boolean esAlfanumericoSeparadoPorComas(String texto) {
        if (texto == null || texto.isEmpty()) {
            return false;
        }

        // 1. Eliminar espacios alrededor y entre comas
        String normalizado = texto.replaceAll("\\s+", "");

        // 2. Validar que contiene solo alfanuméricos y comas (incluso si hay varias comas seguidas)
        return normalizado.matches("^([a-zA-Z0-9]+)(,[a-zA-Z0-9]+)*$");
    }
	
	public static String getValorCampFiltre(String camp, String springFilter) {
		if (hasValue(springFilter) && hasValue(camp)) {
	        String regex = camp + ":('.*?'|\\d+)";
	        Pattern pattern = Pattern.compile(regex);
	        Matcher matcher = pattern.matcher(springFilter);

	        if (matcher.find()) {
	            return matcher.group(1); // Devuelve el valor encontrado
	        }
	        return null; // Si no se encuentra, devuelve null
		}
		return null;
	}
	
	public static DocumentFirmaTipusEnumDto getDocumentFirmaTipus(boolean ambFirma, boolean firmaSeparada) {
		if (!ambFirma) {
			return DocumentFirmaTipusEnumDto.SENSE_FIRMA;
		} else if (!firmaSeparada) {
			return DocumentFirmaTipusEnumDto.FIRMA_ADJUNTA;
		} else {
			return DocumentFirmaTipusEnumDto.FIRMA_SEPARADA;
		}
	}
	
    public static String getFitxerContentType(String fitxerNom, String fitxerContentType) {
    	if (!Utils.hasValue(fitxerContentType)) {
    		//Alguns contentTypes coneguts:
    		if (Utils.hasValue(fitxerNom)) {
    			if (fitxerNom.endsWith(".xsig")) {
    				return "application/octet-stream";
    			} else if (fitxerNom.endsWith(".xml")) {
    				return "text/xml";
    			} else if (fitxerNom.endsWith(".pdf")) {
    				return "application/pdf";
    			} else if (fitxerNom.endsWith(".odt")) {
    				return "application/vnd.oasis.opendocument.text";
    			} else if (fitxerNom.endsWith(".zip")) {
    				return "application/x-zip-compressed";
    			} else if (fitxerNom.endsWith(".docx")) {
    				return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    			} else if (fitxerNom.endsWith(".txt")) {
    				return "text/plain";
    			} else if (fitxerNom.endsWith(".csv")) {
    				return "text/csv";
    			} else if (fitxerNom.endsWith(".xlsx")) {
    				return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";    				
    			} else if (fitxerNom.endsWith(".xls")) {
    				return "application/vnd.ms-excel";    				
    			} else if (fitxerNom.endsWith(".doc")) {
    				return "application/msword";
    			} else if (fitxerNom.endsWith(".jpg")) {
    				return "image/jpeg";
    			} else {
    				return "application/octet-stream";
    			}
    		}	
    	}
    	
    	return fitxerContentType;
    }
    
    public static String eliminaFiltreCurrentSpringFilter(String currentSpringFilter, String filtreEliminar) {
        
    	if (currentSpringFilter!=null && currentSpringFilter.contains(filtreEliminar)) {
        	
        	if (currentSpringFilter.contains("AND "+filtreEliminar)) {
        		currentSpringFilter = currentSpringFilter.replace("AND "+filtreEliminar, "");
        	} else if (currentSpringFilter.contains(filtreEliminar+" AND")) {
        		currentSpringFilter = currentSpringFilter.replace(filtreEliminar+" AND", "");
        	} else {
        		currentSpringFilter = currentSpringFilter.replace(filtreEliminar, "");
        	}
        	
        	//Elimina espais en blanc i tabulacions extres
        	currentSpringFilter = currentSpringFilter.replaceAll("\\s+", " ");
        	
        	if (currentSpringFilter.equals("()") || currentSpringFilter.equals("( )") || !Utils.hasValue(currentSpringFilter)) {
        		currentSpringFilter = null;
        	}
        }
    	
    	return currentSpringFilter;
    }
    
    public static Map<String, String> parseSpringFilter(String filter) {
        Map<String, String> result = new LinkedHashMap<>();

        if (filter == null || filter.isBlank()) {
            return result;
        }

        // Eliminar paréntesis externos y espacios
        String cleaned = filter.replaceAll("\\(", " ").replaceAll("\\)", " ").trim();

        // Regex que captura: campo + operador + valor (con o sin comillas)
        // Operadores soportados: >=, <=, >:, <:, >, <, ~, :
        Pattern pattern = Pattern.compile(
            "([\\w.]+)\\s*(>=|<=|>:|<:|>|<|~|:)\\s*'([^']*)'|" +
            "([\\w.]+)\\s*(>=|<=|>:|<:|>|<|~|:)\\s*(\\S+)"
        );

        Matcher matcher = pattern.matcher(cleaned);

        while (matcher.find()) {
            String field = matcher.group(1) != null ? matcher.group(1) : matcher.group(4);
            String operator = matcher.group(2) != null ? matcher.group(2) : matcher.group(5);
            String value = matcher.group(3) != null ? matcher.group(3) : matcher.group(6);

            String key = buildKey(field, operator);
            result.put(key, value);
        }

        return result;
    }
    
    private static String buildKey(String field, String operator) {
        switch (operator) {
            case ":": return field;                  // equals       → "camp"
            case "~": return  field + "_like";        // like         → "camp_like"
            case ">": return  field + "_gt";          // greater than → "camp_gt"
            case "<": return  field + "_lt";          // less than    → "camp_lt"
            case ">=":return  field + "_gte";         // >=           → "camp_gte"
            case "<=":return  field + "_lte";         // <=           → "camp_lte"
            case ">:":return  field + "_gte";         // >:           → "camp_gte"
            case "<:":return  field + "_lte";         // <:           → "camp_lte"
            default:  return  field + "_" + operator;
        }
    }
    
    /**
     * Parsea un filtro Oracle en String a un Map de clave-valor.
     * Soporta:
     *   - campo is null
     *   - campo is not null
     *   - campo:'valor'
     *   - campo:valor
     *   - campo operator valor (=, !=, <, >, <=, >=)
     *   - operadores AND / OR (ignorados como separadores)
     */
    public static Map<String, Object> parseOracleFilter(String filter) {
        Map<String, Object> result = new LinkedHashMap<>();

        if (filter == null || filter.isBlank()) {
            return result;
        }

        // Eliminar paréntesis externos y espacios
        String cleaned = filter.trim();
        cleaned = cleaned.replaceAll("^\\(+|\\)+$", "").trim();

        // Dividir por AND / OR (case-insensitive), respetando que no estén dentro de comillas
        List<String> conditions = splitByLogicalOperators(cleaned);

        for (String condition : conditions) {
            condition = condition.trim();
            if (condition.isEmpty()) continue;

            parseCondition(condition, result);
        }

        return result;
    }

    private static void parseCondition(String condition, Map<String, Object> result) {
        // Caso: campo IS NULL
        Pattern isNullPattern = Pattern.compile("^([\\w.]+)\\s+is\\s+null$", Pattern.CASE_INSENSITIVE);
        Matcher m = isNullPattern.matcher(condition);
        if (m.matches()) {
            result.put(m.group(1), null);
            return;
        }

        // Caso: campo IS NOT NULL
        Pattern isNotNullPattern = Pattern.compile("^([\\w.]+)\\s+is\\s+not\\s+null$", Pattern.CASE_INSENSITIVE);
        m = isNotNullPattern.matcher(condition);
        if (m.matches()) {
            result.put(m.group(1), "__NOT_NULL__");
            return;
        }

        // Caso: campo:'valor' o campo:valor o campo.nested:'valor'
        Pattern colonPattern = Pattern.compile("^([\\w.]+):'?([^']*)'?$");
        m = colonPattern.matcher(condition);
        if (m.matches()) {
            result.put(m.group(1), m.group(2));
            return;
        }

        // Caso: campo operador valor (=, !=, <=, >=, <, >)
        Pattern operatorPattern = Pattern.compile("^([\\w.]+)\\s*(!=|<=|>=|=|<|>)\\s*'?([^']*)'?$");
        m = operatorPattern.matcher(condition);
        if (m.matches()) {
            result.put(m.group(1) + "[" + m.group(2) + "]", m.group(3));
            return;
        }

        // Si no encaja con nada, guardar la condición raw
        result.put("__unparsed__", condition);
    }

    /**
     * Divide la expresión por AND/OR sin romper valores entre comillas simples.
     */
    private static List<String> splitByLogicalOperators(String expression) {
        List<String> parts = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        int i = 0;

        while (i < expression.length()) {
            char c = expression.charAt(i);

            if (c == '\'') {
                inQuotes = !inQuotes;
                current.append(c);
                i++;
            } else if (!inQuotes) {
                // Comprobar AND
                if (i + 4 <= expression.length() &&
                        expression.substring(i, i + 4).equalsIgnoreCase(" AND") &&
                        (i + 4 == expression.length() || !Character.isLetterOrDigit(expression.charAt(i + 4)))) {
                    parts.add(current.toString().trim());
                    current = new StringBuilder();
                    i += 4;
                }
                // Comprobar OR
                else if (i + 3 <= expression.length() &&
                        expression.substring(i, i + 3).equalsIgnoreCase(" OR") &&
                        (i + 3 == expression.length() || !Character.isLetterOrDigit(expression.charAt(i + 3)))) {
                    parts.add(current.toString().trim());
                    current = new StringBuilder();
                    i += 3;
                } else {
                    current.append(c);
                    i++;
                }
            } else {
                current.append(c);
                i++;
            }
        }

        if (!current.toString().isBlank()) {
            parts.add(current.toString().trim());
        }

        return parts;
    }
    
	public static Set<Long> listToSet(List<Long> ids) {
		Set<Long> resultat = new HashSet<Long>();
		if (ids!=null) {
			for (Long id : ids) {
				if (!resultat.contains(id)) resultat.add(id);
			}
		}
		return resultat;
	}
}