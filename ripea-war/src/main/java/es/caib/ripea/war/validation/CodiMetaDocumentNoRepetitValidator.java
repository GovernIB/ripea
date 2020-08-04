/**
 * 
 */
package es.caib.ripea.war.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import es.caib.ripea.core.api.dto.MetaDocumentDto;
import es.caib.ripea.core.api.service.MetaDocumentService;
import es.caib.ripea.war.command.MetaDocumentCommand;

/**
 * Constraint de validació que controla que no es repeteixi
 * el codi de meta-document.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CodiMetaDocumentNoRepetitValidator implements ConstraintValidator<CodiMetaDocumentNoRepetit, MetaDocumentCommand> {


	@Autowired
	private MetaDocumentService metaDocumentService;



	@Override
	public void initialize(final CodiMetaDocumentNoRepetit constraintAnnotation) {	}

	@Override
	public boolean isValid(final MetaDocumentCommand item, final ConstraintValidatorContext context) {
		try {
			final String codi = item.getCodi();
			if (codi != null && !codi.isEmpty()) {
				final Long id = item.getId();
				final Long entitatId = item.getEntitatId();
				final Long metaExpedientId = item.getMetaExpedientId();
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
						return id.equals(metaDocument.getId());
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
