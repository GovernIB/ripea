/**
 * 
 */
package es.caib.ripea.war.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.caib.ripea.war.command.ContenidorCommand;


/**
 * Constraint de validació que controla que el nom d'un expedient o d'un document no conté un punt
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientODocumentNomValidator implements ConstraintValidator<ExpedientODocumentNom, Object> {
	
	@Override
	public void initialize(ExpedientODocumentNom constraintAnnotation) {
	}

	@Override
	public boolean isValid(final Object value, final ConstraintValidatorContext context) {
		try {
			
			ContenidorCommand contenidorCommand = (ContenidorCommand) value;
			if (contenidorCommand.getNom().contains(".")) {
				return false;
			} else {
				return true;
			}

        } catch (final Exception ex) {
        	LOGGER.error("Error al validar si el nom d'un expedient o d'un document no conté un punt", ex);
        	return false;
        }
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(ExpedientODocumentNomValidator.class);

}
