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
			
			InteressatCommand interessat = (InteressatCommand)value;
			boolean valid = true;
			
			if (interessat.getPais() != null) {
				if ("724".equals(interessat.getPais())) {
					if (interessat.getProvincia() == null || "".equals(interessat.getProvincia())) {
						context
							.buildConstraintViolationWithTemplate(
									MessageHelper.getInstance().getMessage("interessat.form.valid.provincia"))
							.addNode("provincia")
							.addConstraintViolation();
						valid = false;
					}
					if (interessat.getMunicipi() == null || "".equals(interessat.getMunicipi())) {
						context
							.buildConstraintViolationWithTemplate(
									MessageHelper.getInstance().getMessage("interessat.form.valid.municipi"))
							.addNode("municipi")
							.addConstraintViolation();
						valid = false;
					}
				}
			}
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
