/**
 * 
 */
package es.caib.ripea.war.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.war.command.ViaFirmaEnviarCommand;
import es.caib.ripea.war.helper.MessageHelper;

/**
 * Constraint de validació que controla alguns camps del formulari de viaFirma
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ViaFirmaValidator implements ConstraintValidator<ViaFirma, ViaFirmaEnviarCommand> {

	@Autowired
	private AplicacioService aplicacioService;
	
	@Override
	public void initialize(final ViaFirma constraintAnnotation) {
	}

	@Override
	public boolean isValid(final ViaFirmaEnviarCommand value, final ConstraintValidatorContext context) {
		ViaFirmaEnviarCommand command = (ViaFirmaEnviarCommand)value;
		boolean valid = true;
			
		if (isViaFirmaDispositiusEnabled() && (command.getDispositiuViaFirma() == null || command.getDispositiuViaFirma().isEmpty())) {
			context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("NotEmpty"))
			.addNode("dispositiuViaFirma")
			.addConstraintViolation();
			valid = false;
		}
		if (command.isValidateCodeEnabled() && (command.getValidateCode() == null || command.getValidateCode().isEmpty())) {
			context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("NotEmpty"))
			.addNode("validateCode")
			.addConstraintViolation();
			valid = false;
		}
		if (!valid)
			context.disableDefaultConstraintViolation();
		return valid;
	}
	
	private boolean isViaFirmaDispositiusEnabled() {
		return Boolean.parseBoolean(aplicacioService.propertyFindByNom("es.caib.ripea.plugin.viafirma.caib.dispositius.enabled"));
	}
}
