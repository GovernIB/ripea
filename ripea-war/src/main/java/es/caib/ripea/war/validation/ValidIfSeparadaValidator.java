/**
 * 
 */
package es.caib.ripea.war.validation;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.support.RequestContext;

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

	private static final String SESSION_ATTRIBUTE_FRIMA = "ContingutDocumentController.session.firma";
	@Autowired
	private HttpServletRequest request;
	
	@Override
	public void initialize(final ValidIfSeparada firmaSeparada) {
	}

	@Override
	public boolean isValid(
			final DocumentCommand command,
			final ConstraintValidatorContext context) {
		boolean valid = true;
		if (command.isAmbFirma() && DocumentTipusFirmaEnumDto.SEPARAT.equals(command.getTipusFirma())) {
			if (command.getId() == null) {
				valid = (command.getFirma() != null && !command.getFirma().isEmpty()) || request.getSession().getAttribute(SESSION_ATTRIBUTE_FRIMA) != null;
				if (!valid) {
					context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage("FirmaNoBuida", null, new RequestContext(request).getLocale()))
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
