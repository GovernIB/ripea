/**
 * 
 */
package es.caib.ripea.war.validation;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.service.MetaExpedientService;
import es.caib.ripea.war.command.MetaExpedientCommand;

/**
 * Constraint de validació que controla que no es repeteixi
 * el codi SIA de meta-expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class MetaExpedientCodiSiaNoRepetitValidator implements ConstraintValidator<MetaExpedientCodiSiaNoRepetit, Object> {


	@Autowired
	private MetaExpedientService metaExpedientService;



	@Override
	public void initialize(final MetaExpedientCodiSiaNoRepetit constraintAnnotation) {

	}

	@Override
	public boolean isValid(final Object value, final ConstraintValidatorContext context) {
		try {

			MetaExpedientCommand metaExpedientCommand = (MetaExpedientCommand) value;

			 List<MetaExpedientDto> metaExpedients = metaExpedientService.findByClassificacio(
					metaExpedientCommand.getEntitatId(),
					metaExpedientCommand.getClassificacioSia());
			if (metaExpedients == null || metaExpedients.isEmpty()) {
				return true;
			} else {
				if (metaExpedientCommand.getId() == null) {
					return false;
				} else {
					if (metaExpedients.size() > 1) {
						return false;
					} else {
						return metaExpedientCommand.getId().equals(metaExpedients.get(0).getId());
					}
				}
			}
        } catch (final Exception ex) {
        	LOGGER.error("Error al validar si el codi SIA de meta-expedient és únic", ex);
        }
        return false;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(MetaExpedientCodiSiaNoRepetitValidator.class);

}
