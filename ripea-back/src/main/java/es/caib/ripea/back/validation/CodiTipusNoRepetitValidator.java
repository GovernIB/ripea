/**
 * 
 */
package es.caib.ripea.back.validation;

import es.caib.ripea.service.intf.dto.TipusDocumentalDto;
import es.caib.ripea.service.intf.service.TipusDocumentalService;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Constraint de validació que controla que no es repeteixi
 * el codi d'entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CodiTipusNoRepetitValidator implements ConstraintValidator<CodiTipusNoRepetit, Object> {

	private String campId;
	private String campCodi;
	private String campEntitatId;
	
	@Autowired
	private TipusDocumentalService tipusDocumentalService;



	@Override
	public void initialize(final CodiTipusNoRepetit constraintAnnotation) {
		this.campId = constraintAnnotation.campId();
		this.campCodi = constraintAnnotation.campCodi();
		this.campEntitatId = constraintAnnotation.campEntitatId();
	}

	@Override
	public boolean isValid(final Object value, final ConstraintValidatorContext context) {
		try {
			final String id = BeanUtils.getProperty(value, campId);
			final String codi = BeanUtils.getProperty(value, campCodi);
			final Long idEntitat = Long.valueOf(BeanUtils.getProperty(value, campEntitatId));
			
			TipusDocumentalDto tipusDocumental = tipusDocumentalService.findByCodiAndEntitat(codi, idEntitat);
			if (tipusDocumental == null) {
				return true;
			} else {
				if (id == null)
					return false;
				else
					return id.equals(tipusDocumental.getId().toString());
			}
        } catch (final Exception ex) {
        	LOGGER.error("Error al validar si el codi d'entitat és únic", ex);
        }
        return false;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(CodiTipusNoRepetitValidator.class);

}
