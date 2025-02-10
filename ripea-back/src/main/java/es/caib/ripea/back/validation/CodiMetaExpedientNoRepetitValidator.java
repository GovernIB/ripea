/**
 * 
 */
package es.caib.ripea.back.validation;

import es.caib.ripea.service.intf.dto.MetaExpedientDto;
import es.caib.ripea.service.intf.service.MetaExpedientService;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Constraint de validació que controla que no es repeteixi
 * el codi de meta-expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CodiMetaExpedientNoRepetitValidator implements ConstraintValidator<CodiMetaExpedientNoRepetit, Object> {

	private String campId;
	private String campCodi;
	private String campEntitatId;

	@Autowired
	private MetaExpedientService metaExpedientService;



	@Override
	public void initialize(final CodiMetaExpedientNoRepetit constraintAnnotation) {
		this.campId = constraintAnnotation.campId();
		this.campCodi = constraintAnnotation.campCodi();
		this.campEntitatId = constraintAnnotation.campEntitatId();
	}

	@Override
	public boolean isValid(final Object value, final ConstraintValidatorContext context) {
		try {
			final String id = BeanUtils.getProperty(value, campId);
			final String codi = BeanUtils.getProperty(value, campCodi);
			final Long entitatId = new Long(BeanUtils.getSimpleProperty(value, campEntitatId));
			MetaExpedientDto metaExpedient = metaExpedientService.findByEntitatCodi(
					entitatId,
					codi);
			if (metaExpedient == null) {
				return true;
			} else {
				if (id == null)
					return false;
				else
					return id.equals(metaExpedient.getId().toString());
			}
        } catch (final Exception ex) {
        	LOGGER.error("Error al validar si el codi de meta-expedient és únic", ex);
        }
        return false;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(CodiMetaExpedientNoRepetitValidator.class);

}
