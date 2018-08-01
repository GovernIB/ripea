/**
 * 
 */
package es.caib.ripea.war.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.service.ExpedientService;
import es.caib.ripea.war.command.ExpedientRelacionarCommand;
import es.caib.ripea.war.helper.MessageHelper;

/**
 * Validació de la relació d'un expedient amb altres expedients.
 * Valida:
 * - Que no es relacioni amb ell mateix
 * - Que no estigui ja relacionat
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientRelacionarValidator implements ConstraintValidator<ExpedientRelacionar, ExpedientRelacionarCommand> {
	
	@Autowired
	private ExpedientService expedientService;
	
	@Override
	public void initialize(final ExpedientRelacionar anotacio) {
	}

	@Override
	public boolean isValid(
			final ExpedientRelacionarCommand command,
			final ConstraintValidatorContext context) {
		boolean valid = true;
		// Comprova que no es relacioni l'expedient amb ell mateix
		if (command.getExpedientId().equals(command.getRelacionatId())) {
			context.buildConstraintViolationWithTemplate(
					MessageHelper.getInstance().getMessage(
							"contingut.expedient.relacionar.validacio.mateix"))
			.addNode("relacionatId")
			.addConstraintViolation();				
			valid = false;
		}
		// Comprova que no estigui ja relacionat
		for (ExpedientDto relacionat: expedientService.relacioFindAmbExpedient(
				command.getEntitatId(),
				command.getExpedientId())) 
			if (command.getRelacionatId().equals(relacionat.getId())){
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage(
								"contingut.expedient.relacionar.validacio.repetida")).
				addNode("relacionatId").
				addConstraintViolation();				
				valid = false;
				break;
			}
		if (!valid)
			context.disableDefaultConstraintViolation();
		return valid;
	}

}
