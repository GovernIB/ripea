/**
 * 
 */
package es.caib.ripea.back.validation;

import es.caib.ripea.back.command.URLInstruccioCommand;
import es.caib.ripea.back.helper.MessageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.support.RequestContext;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import es.caib.ripea.service.intf.resourcevalidation.URLInstruccioValidaValidator;

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
		return URLInstruccioValidaValidator.isValidURL(url);
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(URLInstruccioValidator.class);

}
