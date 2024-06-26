/**
 * 
 */
package es.caib.ripea.war.validation;

import es.caib.ripea.core.api.dto.InteressatDto;
import es.caib.ripea.core.api.service.ExpedientInteressatService;
import es.caib.ripea.war.command.InteressatCommand;
import es.caib.ripea.war.helper.MessageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.support.RequestContext;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Constraint de validació que controla que no es repeteixi
 * l'interessat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class InteressatNoRepetitValidator implements ConstraintValidator<InteressatNoRepetit, InteressatCommand> {

	@Autowired
	private ExpedientInteressatService expedientInteressatService;
	@Autowired
	private HttpServletRequest request;


	@Override
	public void initialize(final InteressatNoRepetit constraintAnnotation) {
	}

	@Override
	public boolean isValid(final InteressatCommand interessat, final ConstraintValidatorContext context) {
		try {
			
//			InteressatCommand interessat = (InteressatCommand)value;
			if (interessat.getId() != null)
				return true;
			InteressatDto interessatDto = expedientInteressatService.findByExpedientAndDocumentNum(interessat.getDocumentNum(), interessat.getExpedientId());
			if (interessatDto != null) {
				context.buildConstraintViolationWithTemplate(
								MessageHelper.getInstance().getMessage("InteressatNoRepetit", null, new RequestContext(request).getLocale()))
						.addNode("documentNum")
						.addConstraintViolation();
				context.disableDefaultConstraintViolation();
			}
			return interessatDto == null;
			
		} catch (final Exception ex) {
        	LOGGER.error("Error al comprovar si l'interessat ja està donat d'alta", ex);
        	return false;
        }
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(InteressatNoRepetitValidator.class);

}
