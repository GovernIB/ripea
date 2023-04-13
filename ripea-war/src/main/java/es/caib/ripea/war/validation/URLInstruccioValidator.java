/**
 * 
 */
package es.caib.ripea.war.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.support.RequestContext;

import es.caib.ripea.war.command.URLInstruccioCommand;
import es.caib.ripea.war.helper.MessageHelper;

/**
 * Constraint de validació que controla que camp url tingui el format correcte
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class URLInstruccioValidator implements ConstraintValidator<URLInstruccio, URLInstruccioCommand> {

    @Autowired
    private HttpServletRequest request;

	@Override
	public void initialize(final URLInstruccio constraintAnnotation) {
	}

	@Override
	public boolean isValid(URLInstruccioCommand value, final ConstraintValidatorContext context) {
		try {
			
			boolean valid = true;
			String url = value.getUrl();
			
			if (url != null && !url.isEmpty() && ! isValidURL(url)) {
				context
					.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage("url.instruccio.url.valid", null, new RequestContext(request).getLocale()))
					.addNode("url")
					.addConstraintViolation();
				valid = false;
			}
			if (!valid)
				context.disableDefaultConstraintViolation();
			return valid;
		} catch (final Exception ex) {
        	LOGGER.error("Error al validar la URL d'instrucció", ex);
        	return false;
        }
	}

	public static boolean isValidURL(String url) {
		String regex = "^(http|https)://.*\\[(ENI)\\]$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(url);
		return matcher.matches();
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(URLInstruccioValidator.class);

}
