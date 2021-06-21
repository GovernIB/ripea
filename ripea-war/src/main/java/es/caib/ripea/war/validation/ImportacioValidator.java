/**
 * 
 */
package es.caib.ripea.war.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import es.caib.ripea.core.api.dto.TipusDestiEnumDto;
import es.caib.ripea.war.command.ImportacioCommand;
import es.caib.ripea.war.helper.MessageHelper;

/**
 * Constraint de validació que controla alguns camps del formulari d'importació
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ImportacioValidator implements ConstraintValidator<Importacio, ImportacioCommand> {
	
	@Override
	public void initialize(final Importacio constraintAnnotation) {
	}

	@Override
	public boolean isValid(final ImportacioCommand value, final ConstraintValidatorContext context) {
		ImportacioCommand command = (ImportacioCommand)value;
		boolean valid = true;
			
		if (command.getDestiTipus().equals(TipusDestiEnumDto.CARPETA_NOVA) && command.getCarpetaNom().isEmpty()) {
			context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("NotEmpty"))
			.addNode("carpetaNom")
			.addConstraintViolation();
			valid = false;
		}
		if (!valid)
			context.disableDefaultConstraintViolation();
		return valid;
	}
}
