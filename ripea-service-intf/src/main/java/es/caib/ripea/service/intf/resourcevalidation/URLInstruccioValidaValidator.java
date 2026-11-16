package es.caib.ripea.service.intf.resourcevalidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * Valida el format de la URL d'instrucció. Font única de la regla, compartida
 * per la interfície REACT (URLInstruccioResource) i la JSP (URLInstruccioCommand).
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class URLInstruccioValidaValidator implements ConstraintValidator<URLInstruccioValida, String> {

	private static final Pattern URL_PATTERN = Pattern.compile("^(http|https)://.*\\[(ENI)\\]$");

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null || value.isEmpty()) {
			return true;
		}
		return isValidURL(value);
	}

	public static boolean isValidURL(String url) {
		return url != null && URL_PATTERN.matcher(url).matches();
	}

}
