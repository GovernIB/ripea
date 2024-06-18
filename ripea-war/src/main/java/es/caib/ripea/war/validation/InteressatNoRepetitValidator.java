/**
 * 
 */
package es.caib.ripea.war.validation;

import es.caib.ripea.core.api.dto.InteressatDto;
import es.caib.ripea.core.api.service.ExpedientInteressatService;
import es.caib.ripea.war.command.InteressatCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Constraint de validació que controla que no es repeteixi
 * l'interessat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class InteressatNoRepetitValidator implements ConstraintValidator<InteressatNoRepetit, Object> {

	@Autowired
	private ExpedientInteressatService expedientInteressatService;



	@Override
	public void initialize(final InteressatNoRepetit constraintAnnotation) {
	}

	@Override
	public boolean isValid(final Object value, final ConstraintValidatorContext context) {
		try {
			
			InteressatCommand interessat = (InteressatCommand)value;
			if (interessat.getId() != null)
				return true;
			InteressatDto interessatDto = expedientInteressatService.findByExpedientAndDocumentNum(interessat.getDocumentNum(), interessat.getExpedientId());
			return interessatDto == null;
			
		} catch (final Exception ex) {
        	LOGGER.error("Error al comprovar si l'interessat ja està donat d'alta", ex);
        	return false;
        }
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(InteressatNoRepetitValidator.class);

}
