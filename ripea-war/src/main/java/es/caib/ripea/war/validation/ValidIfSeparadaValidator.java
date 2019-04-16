/**
 * 
 */
package es.caib.ripea.war.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import es.caib.ripea.core.api.dto.DocumentTipusFirmaEnumDto;
import es.caib.ripea.war.command.DocumentCommand;
import es.caib.ripea.war.helper.MessageHelper;

/**
 * Validació de si existeix un document digital al formulari de
 * creació/modificació de document.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ValidIfSeparadaValidator implements ConstraintValidator<ValidIfSeparada, DocumentCommand> {

	@Override
	public void initialize(final ValidIfSeparada firmaSeparada) {
	}

	@Override
	public boolean isValid(
			final DocumentCommand command,
			final ConstraintValidatorContext context) {
		boolean valid = true;
		if (DocumentTipusFirmaEnumDto.SEPARAT.equals(command.getTipusFirma())) {
			if (command.getId() == null) {
				valid = command.getFirma() != null && !command.getFirma().isEmpty();
				if (!valid) {
					context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage("FirmaNoBuida"))
					.addNode("firma")
					.addConstraintViolation();
				}
			}
		}
		if (!valid)
			context.disableDefaultConstraintViolation();
		return valid;
	}

}
