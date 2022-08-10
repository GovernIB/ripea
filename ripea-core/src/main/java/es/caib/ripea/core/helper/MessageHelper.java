/**
 * 
 */
package es.caib.ripea.core.helper;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Helper per a mostrar missatges multiidioma.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class MessageHelper implements MessageSourceAware {

	private static ThreadLocal<Locale> currentLocale = new ThreadLocal<>();

	public static void setCurrentLocale(Locale locale) {
		MessageHelper.currentLocale.set(locale);
	}


	private MessageSource messageSource;


	public String getMessage(String[] keys, Object[] vars, Locale locale) {
		String msg = "???" + (keys.length > 0 ? keys[keys.length-1] : "") + "???";
		boolean found = false;
		int i = 0;
		if (locale == null) {
			locale = MessageHelper.currentLocale.get();
		}
		while( ! found && i < keys.length) {		
			try {
				msg = messageSource.getMessage(
						keys[i],
						vars,
						locale);
				found = true;
			} catch (NoSuchMessageException ex) {
				i++;
			}
		}
		if( ! found ) {
			String key = keys[keys.length-1]; 
			if (key.startsWith("enum.")){
				msg = key.substring(key.lastIndexOf(".") + 1);
			}			
		}
		return msg;
	}
	public String getMessage(String key, Object[] vars, Locale locale) {
		if (locale == null) {
			locale = MessageHelper.currentLocale.get();
		}
		try {
			return messageSource.getMessage(
					key,
					vars,
					locale);
		} catch (NoSuchMessageException ex) {
			if (key.startsWith("enum.")){
				return key.substring(key.lastIndexOf(".") + 1);
			}
			return "???" + key + "???";
		}
	}
	public String getMessage(String key, Object[] vars) {
		return getMessage(key, vars, null);
	}
	public String getMessage(String key) {
		return getMessage(key, null, null);
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

}
