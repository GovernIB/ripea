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

import es.caib.ripea.core.api.dto.MetaDocumentDto;
import es.caib.ripea.core.api.service.MetaDocumentService;

/**
 * Constraint de validació que controla que no es repeteixi
 * el codi de meta-document.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CodiMetaDocumentNoRepetitValidator implements ConstraintValidator<CodiMetaDocumentNoRepetit, Object> {

	private String campId;
	private String campCodi;
	private String campEntitatId;
	private String campMetaExpedientId;

	@Autowired
	private MetaDocumentService metaDocumentService;



	@Override
	public void initialize(final CodiMetaDocumentNoRepetit constraintAnnotation) {
		this.campId = constraintAnnotation.campId();
		this.campCodi = constraintAnnotation.campCodi();
		this.campEntitatId = constraintAnnotation.campEntitatId();
		this.campMetaExpedientId = constraintAnnotation.campMetaExpedientId();
	}

	@Override
	public boolean isValid(final Object value, final ConstraintValidatorContext context) {
		try {
			final String codi = BeanUtils.getProperty(value, campCodi);
			if (codi != null && !codi.isEmpty()) {
				final String id = BeanUtils.getProperty(value, campId);
				final Long entitatId = new Long(BeanUtils.getSimpleProperty(value, campEntitatId));
				final Long metaExpedientId = new Long(BeanUtils.getSimpleProperty(value, campMetaExpedientId));
				MetaDocumentDto metaDocument = metaDocumentService.findByCodi(
						entitatId,
						metaExpedientId,
						codi);
				if (metaDocument == null) {
					return true;
				} else {
					if (id == null)
						return false;
					else
						return id.equals(metaDocument.getId().toString());
				}
			} else {
				return true;
			}
        } catch (final Exception ex) {
        	LOGGER.error("Error al validar si el codi de meta-document és únic", ex);
        }
        return false;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(CodiMetaDocumentNoRepetitValidator.class);

}
