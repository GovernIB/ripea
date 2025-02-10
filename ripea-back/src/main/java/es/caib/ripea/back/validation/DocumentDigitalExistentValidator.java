/**
 * 
 */
package es.caib.ripea.back.validation;

import es.caib.ripea.back.command.DocumentCommand;
import es.caib.ripea.back.command.DocumentCommand.DocumentFisicOrigenEnum;
import es.caib.ripea.back.helper.MessageHelper;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validació de si existeix un document digital al formulari de
 * creació/modificació de document.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DocumentDigitalExistentValidator implements ConstraintValidator<DocumentDigitalExistent, DocumentCommand> {

	@Override
	public void initialize(final DocumentDigitalExistent anotacio) {
	}

	@Override
	public boolean isValid(
			final DocumentCommand command,
			final ConstraintValidatorContext context) {
		boolean valid = true;
		if (DocumentFisicOrigenEnum.DISC.equals(command.getOrigen())) {
			if (command.getId() == null) {
				valid = command.getArxiu() != null && !command.getArxiu().isEmpty();
				if (!valid) {
					context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage("ArxiuNoBuit"))
					.addNode("arxiu")
					.addConstraintViolation();
				}
			}
		} else if (DocumentFisicOrigenEnum.ESCANER.equals(command.getOrigen())) {
//						
//			if (command.getEscanejatTempId() == null || command.getEscanejatTempId().isEmpty()) {
//				context.buildConstraintViolationWithTemplate(
//						MessageHelper.getInstance().getMessage("ArxiuNoBuit"))
//				.addNode("escanejatTempId")
//				.addConstraintViolation();
//				valid = false;
//			}
		}
		if (!valid)
			context.disableDefaultConstraintViolation();
		return valid;
	}

}
