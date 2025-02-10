/**
 * 
 */
package es.caib.ripea.back.validation;


import es.caib.ripea.back.command.ContingutMoureCopiarEnviarCommand;
import es.caib.ripea.back.helper.ExpedientHelper;
import es.caib.ripea.back.helper.MessageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.support.RequestContext;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Constraint de validació que controla que s'ha seleccionat un destí en la vista d'arbre
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DestiNotEmptyValidator implements ConstraintValidator<DestiNotEmpty, ContingutMoureCopiarEnviarCommand> {
	
	@Autowired
	private HttpServletRequest request;
	@Autowired
	private ExpedientHelper expedientHelper;
	
	@Override
	public void initialize(final DestiNotEmpty constraintAnnotation) {
	}

	@Override
	public boolean isValid(final ContingutMoureCopiarEnviarCommand value, final ConstraintValidatorContext context) {
		ContingutMoureCopiarEnviarCommand command = (ContingutMoureCopiarEnviarCommand)value;
		boolean valid = true;
			
		if (expedientHelper.isVistaArbreMoureDocuments(request) && command.getDestiId() == null) {
			context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("NotEmpty", null, new RequestContext(request).getLocale()))
			.addNode("estructuraCarpetesJson")
			.addConstraintViolation();
			valid = false;
		}
		
		if (!valid)
			context.disableDefaultConstraintViolation();
		
		return valid;
	}

}
