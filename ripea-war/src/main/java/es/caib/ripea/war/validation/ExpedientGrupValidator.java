/**
 * 
 */
package es.caib.ripea.war.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.caib.ripea.war.command.ExpedientCommand;


/**
 * Constraint de validació que un camp grup es obligatori quan gestioAmbGrupsActiva == true
 * el codi d'entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientGrupValidator implements ConstraintValidator<ExpedientGrup, ExpedientCommand> {
	

	@Override
	public void initialize(final ExpedientGrup constraintAnnotation) {

	}

	@Override
	public boolean isValid(final ExpedientCommand expedientCommand, final ConstraintValidatorContext context) {
		try {
			if (expedientCommand.isGestioAmbGrupsActiva() && expedientCommand.getGrupId() == null) {
				return false;
			} else {
				return true;
			}

        } catch (final Exception ex) {
        	LOGGER.error("Error al validar si el grup es mandatori", ex);
        	return false;
        }
	}

	
	private static final Logger LOGGER = LoggerFactory.getLogger(ExpedientGrupValidator.class);

}
