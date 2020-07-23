/**
 * 
 */
package es.caib.ripea.war.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.service.ExpedientService;


/**
 * Constraint de validació que controla que no es repeteixi
 * el codi d'entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientNomUniqueValidator implements ConstraintValidator<ExpedientNomUnique, Object> {
	
	private String campId;
	private String campMetaExpedientId;
	private String campNom;
	private String campEntitatId;
	private String campPareId;
	
	@Autowired
	private ExpedientService expedientService;

	@Override
	public void initialize(final ExpedientNomUnique constraintAnnotation) {
		this.campId = constraintAnnotation.campId();
		this.campMetaExpedientId = constraintAnnotation.campMetaExpedientId();
		this.campNom = constraintAnnotation.campNom();
		this.campEntitatId = constraintAnnotation.campEntitatId();
		this.campPareId = constraintAnnotation.campPareId();
	}

	@Override
	public boolean isValid(final Object value, final ConstraintValidatorContext context) {
		try {
			final Long id = Long.valueOf(BeanUtils.getProperty(value, campId));
			final Long metaExpedientId = Long.valueOf(BeanUtils.getProperty(value, campMetaExpedientId));
			final String nom = BeanUtils.getProperty(value, campNom);
			final Long entitatId = Long.valueOf(BeanUtils.getProperty(value, campEntitatId));
			final Long pareId = Long.valueOf(BeanUtils.getProperty(value, campPareId));

			ExpedientDto expedient = expedientService.findByMetaExpedientAndPareAndNomAndEsborrat(entitatId, 
																								  metaExpedientId, 
																								  pareId, nom, 0);
			if (expedient != null) {
				return false;
			}
        } catch (final Exception ex) {
        	LOGGER.error("Error al validar si el codi d'entitat és únic", ex);
        	return false;
        }
        return true;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(ExpedientNomUniqueValidator.class);

}
