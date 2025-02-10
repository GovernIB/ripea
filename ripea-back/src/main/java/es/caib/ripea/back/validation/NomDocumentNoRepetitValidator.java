/**
 * 
 */
package es.caib.ripea.back.validation;

import es.caib.ripea.back.command.DocumentCommand;
import es.caib.ripea.back.command.DocumentGenericCommand;
import es.caib.ripea.back.helper.MessageHelper;
import es.caib.ripea.service.intf.dto.ContingutDto;
import es.caib.ripea.service.intf.service.ContingutService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

/**
 * Constraint de validació que controla que no es repeteixi
 * el nom del document dins un contenidor.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class NomDocumentNoRepetitValidator implements ConstraintValidator<NomDocumentNoRepetit, Object> {

	@Autowired
	private ContingutService contingutService;


	@Override
	public void initialize(final NomDocumentNoRepetit constraintAnnotation) {
	}

	@Override
	public boolean isValid(
			final Object obj, 
			final ConstraintValidatorContext context) {
		try {
			Long entitatId = obj instanceof DocumentCommand ? ((DocumentCommand) obj).getEntitatId() : ((DocumentGenericCommand) obj).getEntitatId();
			Long pareId = obj instanceof DocumentCommand ? ((DocumentCommand) obj).getPareId() : ((DocumentGenericCommand) obj).getPareId();
			Long id = obj instanceof DocumentCommand ? ((DocumentCommand) obj).getId() : ((DocumentGenericCommand) obj).getId();
			String nom = obj instanceof DocumentCommand ? ((DocumentCommand) obj).getNom() : ((DocumentGenericCommand) obj).getNom();
			
			boolean valid = true;
			List<ContingutDto> fills = contingutService.getFillsBasicInfo(pareId);
			
			for (ContingutDto contingut: fills) {
				if (contingut.isDocument()) {
					if (contingut.getNom().equals(nom)) {
						if (id == null || !id.equals(contingut.getId())) {
							context.disableDefaultConstraintViolation();
							context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("NomDocumentNoRepetit"))
								.addNode("nom")
								.addConstraintViolation();
							valid = false;
							break;
						}
							
					}
				}
			}
			
			return valid;
        } catch (final Exception ex) {
        	LOGGER.error("Error al validar si el nom del document és únic", ex);
        }
        return false;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(NomDocumentNoRepetitValidator.class);

}
