/**
 * 
 */
package es.caib.ripea.back.validation;

import es.caib.ripea.service.intf.dto.MetaExpedientTascaDto;
import es.caib.ripea.service.intf.service.ExpedientTascaService;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

/**
 * Constraint de validació que controla que no es repeteixi
 * el codi de meta-document.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CodiMetaExpedientTascaNoRepetitValidator implements ConstraintValidator<CodiMetaExpedientTascaNoRepetit, Object> {

	private String campId;
	private String campCodi;
	private String campEntitatId;
	private String campMetaExpedientId;

	@Autowired
	private ExpedientTascaService expedientTascaService;



	@Override
	public void initialize(final CodiMetaExpedientTascaNoRepetit constraintAnnotation) {
		this.campId = constraintAnnotation.campId();
		this.campCodi = constraintAnnotation.campCodi();
		this.campEntitatId = constraintAnnotation.campEntitatId();
		this.campMetaExpedientId = constraintAnnotation.campMetaExpedientId();
	}

	@Override
	public boolean isValid(final Object value, final ConstraintValidatorContext context) {
		try {
			final String codi = BeanUtils.getProperty(value, campCodi);
			final String id = BeanUtils.getProperty(value, campId);
			final Long entitatId = new Long(BeanUtils.getSimpleProperty(value, campEntitatId));
			final Long metaExpedientId = new Long(BeanUtils.getSimpleProperty(value, campMetaExpedientId));
			
			if (codi == null || codi.isEmpty()) {
				return true;
			}

			List<MetaExpedientTascaDto> tasquesMetaExp = expedientTascaService.findAmbMetaExpedient(entitatId, 
																									metaExpedientId);
			
			// creació
			if (id == null)
			{
				for (MetaExpedientTascaDto tasca: tasquesMetaExp)
				{
					if (tasca.getCodi().equals(codi))
					{
						return false;
					}
				}	
				
			// edició
			}else {
				for (MetaExpedientTascaDto tasca: tasquesMetaExp)
				{
					if (tasca.getCodi().equals(codi) && !id.equals(tasca.getId().toString()))
					{
						return false;
					}
				}
			}
			
			return true;
			
        } catch (final Exception ex) {
        	LOGGER.error("Error al validar si el codi de meta-document és únic", ex);
        }
        return false;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(CodiMetaExpedientTascaNoRepetitValidator.class);

}
