package es.caib.ripea.service.helper;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component("serviceMessageHelper")
public class MessageHelper implements MessageSourceAware {

	private static Locale messagesLocale;
	private MessageSource messageSource;

	public static void setCurrentLocale(Locale locale) {
		MessageHelper.messagesLocale=locale;
	}
	
	private Locale getLocale(Locale locale) {
		if (MessageHelper.messagesLocale!=null) { return MessageHelper.messagesLocale; }
		if (locale!=null) { return locale; }
		return LocaleContextHolder.getLocale();
	}
	
	public String getMessage(String[] keys, Object[] vars, Locale locale) {
		String msg = "???" + (keys.length > 0 ? keys[keys.length-1] : "") + "???";
		boolean found = false;
		int i = 0;
		while( ! found && i < keys.length) {		
			try {
				msg = messageSource.getMessage(
						keys[i],
						vars,
						getLocale(locale));
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
		try {
			return messageSource.getMessage(
					key,
					vars,
					getLocale(locale));
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