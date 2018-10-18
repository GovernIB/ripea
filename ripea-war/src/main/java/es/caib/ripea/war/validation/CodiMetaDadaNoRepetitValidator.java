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

import es.caib.ripea.core.api.dto.MetaDadaDto;
import es.caib.ripea.core.api.service.MetaDadaService;

/**
 * Constraint de validació que controla que no es repeteixi
 * el codi de meta-dada.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CodiMetaDadaNoRepetitValidator implements ConstraintValidator<CodiMetaDadaNoRepetit, Object> {

	private String campId;
	private String campCodi;
	private String campEntitatId;
	private String campMetaNodeId;

	@Autowired
	private MetaDadaService metaDadaService;



	@Override
	public void initialize(final CodiMetaDadaNoRepetit constraintAnnotation) {
		this.campId = constraintAnnotation.campId();
		this.campCodi = constraintAnnotation.campCodi();
		this.campEntitatId = constraintAnnotation.campEntitatId();
		this.campMetaNodeId = constraintAnnotation.campMetaNodeId();
	}

	@Override
	public boolean isValid(final Object value, final ConstraintValidatorContext context) {
		try {
			final String id = BeanUtils.getProperty(value, campId);
			final String codi = BeanUtils.getProperty(value, campCodi);
			final Long entitatId = new Long(BeanUtils.getSimpleProperty(value, campEntitatId));
			final Long metaNodeId = new Long(BeanUtils.getSimpleProperty(value, campMetaNodeId));
			MetaDadaDto metaDada = metaDadaService.findByCodi(
					entitatId,
					metaNodeId,
					codi);
			if (metaDada == null) {
				return true;
			} else {
				if (id == null)
					return false;
				else
					return id.equals(metaDada.getId().toString());
			}
        } catch (final Exception ex) {
        	LOGGER.error("Error al validar si el codi de meta-dada és únic", ex);
        }
        return false;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(CodiMetaDadaNoRepetitValidator.class);

}
