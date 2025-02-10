/**
 *
 */
package es.caib.ripea.back.validation;

import es.caib.ripea.back.command.DocumentCommand;
import es.caib.ripea.back.command.DocumentCommand.DocumentFisicOrigenEnum;
import es.caib.ripea.back.helper.MessageHelper;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Valida que l'arxiu no estigui buit.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ArxiuNoBuitValidator implements ConstraintValidator<ArxiuNoBuit, DocumentCommand> {

	private static final String SESSION_ATTRIBUTE_DOCUMENT = "ContingutDocumentController.session.document";
	@Autowired
	private HttpServletRequest request;
	
	@Override
	public void initialize(final ArxiuNoBuit constraintAnnotation) {
	}

	@Override
	public boolean isValid(
			final DocumentCommand value,
			final ConstraintValidatorContext context) {

		request.getSession().getAttribute(SESSION_ATTRIBUTE_DOCUMENT);
		
		boolean valid = true;
		if ((value.getOrigen().equals(DocumentFisicOrigenEnum.DISC) && (value.getArxiu() == null || value.getArxiu().isEmpty()) && request.getSession().getAttribute(SESSION_ATTRIBUTE_DOCUMENT) == null)) {
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
