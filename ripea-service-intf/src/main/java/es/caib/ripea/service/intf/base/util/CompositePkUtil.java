package es.caib.ripea.service.intf.base.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.caib.ripea.service.intf.base.exception.CompositePkParsingException;

import java.io.Serializable;
import java.util.Base64;
import java.util.UUID;

/**
 * Utilitats per a serialitzar i deserialitzar claus primàries compostes.
 * 
 * @author Límit Tecnologies
 */
public class CompositePkUtil {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	public static <CPK extends Serializable> CPK getCompositePkFromSerializedId(
			String id,
			Class<?> pkClass) {
		if (id != null) {
			try {
				byte[] idBase64 = id.getBytes();
				return (CPK)objectMapper.readValue(
						new String(Base64.getDecoder().decode(idBase64)),
						pkClass);
			} catch (JsonProcessingException ex) {
				throw new CompositePkParsingException(id, null, pkClass, ex);
			}
		} else {
			return null;
		}
	}

	public static String getSerializedIdFromCompositePk(Serializable pk) {
		if (pk != null) {
			try {
				byte[] idBase64 = Base64.getEncoder().encode(objectMapper.writeValueAsString(pk).getBytes());
				return new String(idBase64);
			} catch (JsonProcessingException ex) {
				throw new CompositePkParsingException(null, pk, pk.getClass(), ex);
			}
		} else {
			return null;
		}
	}

	public static boolean isCompositePkClass(Class<?> clazz) {
		return !(String.class.isAssignableFrom(clazz) ||
				Number.class.isAssignableFrom(clazz) ||
				UUID.class.isAssignableFrom(clazz));
	}

}
