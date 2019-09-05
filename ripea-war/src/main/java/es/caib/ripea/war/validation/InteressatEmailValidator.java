/**
 * 
 */
package es.caib.ripea.war.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.caib.ripea.war.command.InteressatCommand;
import es.caib.ripea.war.helper.MessageHelper;

/**
 * Constraint de validació que controla que camp email és obligatori si està habilitada l'entrega a la Direcció Electrònica Hablitada (DEH)
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class InteressatEmailValidator implements ConstraintValidator<InteressatEmail, Object> {


	@Override
	public void initialize(final InteressatEmail constraintAnnotation) {
	}

	@Override
	public boolean isValid(final Object value, final ConstraintValidatorContext context) {
		try {
			
			InteressatCommand interessat = (InteressatCommand)value;
			boolean valid = true;
			
			if (interessat.getEntregaDeh() && (interessat.getEmail() == null || interessat.getEmail().isEmpty())) {
				context
					.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage("interessat.form.valid.email"))
					.addNode("email")
					.addConstraintViolation();
				valid = false;
			}

			return valid;
		} catch (final Exception ex) {
        	LOGGER.error("Ha d'informar de la provincia i el municipi quan el país és Espanya", ex);
        	return false;
        }
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(InteressatEmailValidator.class);

}
