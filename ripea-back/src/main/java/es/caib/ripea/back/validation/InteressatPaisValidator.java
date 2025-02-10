/**
 * 
 */
package es.caib.ripea.back.validation;

import es.caib.ripea.back.command.InteressatCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Constraint de validació que controla que no es repeteixi
 * l'interessat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class InteressatPaisValidator implements ConstraintValidator<InteressatPais, Object> {


	@Override
	public void initialize(final InteressatPais constraintAnnotation) {
	}

	@Override
	public boolean isValid(final Object value, final ConstraintValidatorContext context) {
		try {
			
			@SuppressWarnings("unused")
			InteressatCommand interessat = (InteressatCommand)value;
			boolean valid = true;
			
			if (!valid)
				context.disableDefaultConstraintViolation();
			return valid;
		} catch (final Exception ex) {
        	LOGGER.error("Ha d'informar de la provincia i el municipi quan el país és Espanya", ex);
        	return false;
        }
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(InteressatPaisValidator.class);

}
