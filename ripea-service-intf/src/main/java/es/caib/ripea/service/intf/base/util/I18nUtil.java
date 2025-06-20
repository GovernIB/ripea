package es.caib.ripea.service.intf.base.util;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;

import java.lang.reflect.Field;

/**
 * Utilitats per a traduir missatges.
 * 
 * @author Límit Tecnologies
 */
public class I18nUtil {

	public static String getI18nEnumDescription(
			Field field,
			String enumValue,
			MessageSource messageSource) {
		try {
			String i18nKey = field.getDeclaringClass().getName() + "." + field.getName() + "." + enumValue;
			return messageSource.getMessage(
					i18nKey,
					null,
					LocaleContextHolder.getLocale());
		} catch (NoSuchMessageException ex) {
			try {
				Class<?> fieldType;
				if (field.getType().isArray()) {
					fieldType = field.getType().getComponentType();
				} else {
					fieldType = field.getType();
				}
				String i18nKey = fieldType.getName() + "." + enumValue;
				return messageSource.getMessage(
						i18nKey,
						null,
						LocaleContextHolder.getLocale());
			} catch (NoSuchMessageException ex2) {
				return enumValue;
			}
		}
	}

}
