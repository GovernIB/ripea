/**
 *
 */
package es.caib.ripea.war.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import es.caib.ripea.war.command.DocumentCommand;
import es.caib.ripea.war.command.DocumentCommand.DocumentFisicOrigenEnum;
import es.caib.ripea.war.helper.MessageHelper;

/**
 * Valida que l'arxiu no estigui buit.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ArxiuNoBuitValidator implements ConstraintValidator<ArxiuNoBuit, DocumentCommand> {

	@Override
	public void initialize(final ArxiuNoBuit constraintAnnotation) {
	}

	@Override
	public boolean isValid(
			final DocumentCommand value,
			final ConstraintValidatorContext context) {
		boolean valid = true;
		if (!(!value.getOrigen().equals(DocumentFisicOrigenEnum.DISC) && (value.getArxiu() == null || value.getArxiu().isEmpty()))) {
			context.buildConstraintViolationWithTemplate(
					MessageHelper.getInstance().getMessage(
							"contingut.expedient.relacionar.validacio.mateix"))
			.addNode("arxiu")
			.addConstraintViolation();
			valid = false;
		} else {
			valid = true;
		}
		
		if (!valid)
			context.disableDefaultConstraintViolation();
		
		return valid;
	}

}
