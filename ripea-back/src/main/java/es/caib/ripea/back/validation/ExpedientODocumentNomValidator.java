/**
 * 
 */
package es.caib.ripea.back.validation;

import es.caib.ripea.back.command.ContenidorCommand;
import es.caib.ripea.service.intf.service.AplicacioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


/**
 * Constraint de validació que controla que el nom d'un expedient o d'un document no conté un punt
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientODocumentNomValidator implements ConstraintValidator<ExpedientODocumentNom, Object> {
	
	@Autowired
	private AplicacioService aplicacioService;
	
	@Override
	public void initialize(ExpedientODocumentNom constraintAnnotation) {
	}

	@Override
	public boolean isValid(final Object value, final ConstraintValidatorContext context) {
		try {
			
			ContenidorCommand contenidorCommand = (ContenidorCommand) value;
			if (contenidorCommand.getNom().contains(".") && ! isPermesPuntsNomExpedient()) {
				return false;
			} else {
				return true;
			}

        } catch (final Exception ex) {
        	LOGGER.error("Error al validar si el nom d'un expedient o d'un document no conté un punt", ex);
        	return false;
        }
	}

	private boolean isPermesPuntsNomExpedient() {
		return Boolean.parseBoolean(aplicacioService.propertyFindByNom("es.caib.ripea.expedient.permetre.punts"));
	}
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ExpedientODocumentNomValidator.class);

}
