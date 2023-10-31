/**
 * 
 */
package es.caib.ripea.war.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import es.caib.ripea.core.api.dto.ContingutDto;
import es.caib.ripea.core.api.service.ContingutService;
import es.caib.ripea.war.command.CarpetaCommand;
import es.caib.ripea.war.helper.MessageHelper;

/**
 * Constraint de validació que controla que no es repeteixi
 * el nom del document dins un contenidor.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class NomCarpetaNoRepetitValidator implements ConstraintValidator<NomCarpetaNoRepetit, Object> {

	@Autowired
	private ContingutService contingutService;


	@Override
	public void initialize(final NomCarpetaNoRepetit constraintAnnotation) {
	}

	@Override
	public boolean isValid(
			final Object obj, 
			final ConstraintValidatorContext context) {
		try {
			Long entitatId = ((CarpetaCommand) obj).getEntitatId();
			Long pareId = ((CarpetaCommand) obj).getPareId();
			Long id = ((CarpetaCommand) obj).getId();
			String nom = ((CarpetaCommand) obj).getNom();
			
			boolean valid = true;
			ContingutDto contingutPare = contingutService.findAmbIdUser(
					entitatId, 
					pareId, 
					true, 
					false,
					false, null, null);
			
			for (ContingutDto contingut: contingutPare.getFills()) {
				if (contingut.isCarpeta() && !contingut.isEsborrat()) {
					if (contingut.getNom().equals(nom)) {
						if (id == null || id != contingut.getId()) {
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

	private static final Logger LOGGER = LoggerFactory.getLogger(NomCarpetaNoRepetitValidator.class);

}