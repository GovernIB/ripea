/**
 * 
 */
package es.caib.ripea.back.validation;


import es.caib.ripea.back.command.ContingutMoureCopiarEnviarCommand;
import es.caib.ripea.back.helper.ExpedientHelper;
import es.caib.ripea.back.helper.MessageHelper;
import es.caib.ripea.service.intf.dto.ContingutDto;
import es.caib.ripea.service.intf.service.ContingutService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.support.RequestContext;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Constraint de validació que controla que s'ha seleccionat un destí en la vista d'arbre
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DestiNotEmptyValidator implements ConstraintValidator<DestiNotEmpty, ContingutMoureCopiarEnviarCommand> {
	
	@Autowired private HttpServletRequest request;
	@Autowired private ExpedientHelper expedientHelper;
	@Autowired private ContingutService contingutService;
	
	@Override
	public void initialize(final DestiNotEmpty constraintAnnotation) {
	}

	@Override
	public boolean isValid(final ContingutMoureCopiarEnviarCommand value, final ConstraintValidatorContext context) {
		ContingutMoureCopiarEnviarCommand command = (ContingutMoureCopiarEnviarCommand)value;
		boolean valid = true;
			
		if (expedientHelper.isVistaArbreMoureDocuments(request) && command.getDestiId() == null) {
			context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("NotEmpty", null, new RequestContext(request).getLocale()))
			.addNode("estructuraCarpetesJson")
			.addConstraintViolation();
			valid = false;
		}
		
		if (command.getOrigenId()!=null) {
		
			ContingutDto origen = contingutService.getBasicInfo(command.getOrigenId(), false);
			
			if ("COPIAR".equals(command.getAccio()) && origen.isCarpeta()) {
				context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("MateixDesti", null, new RequestContext(request).getLocale()))
				.addNode("estructuraCarpetesJson")
				.addConstraintViolation();
				valid = false;
			}
			
			Long pareIdContingutOrigen = contingutService.getPareId(command.getOrigenId());
			
			if (pareIdContingutOrigen!=null && pareIdContingutOrigen.equals(command.getDestiId())) {
				context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("MateixDesti", null, new RequestContext(request).getLocale()))
				.addNode("estructuraCarpetesJson")
				.addConstraintViolation();
				valid = false;
			}
			
			if (valid) {
				List<ContingutDto> fills = contingutService.getFillsBasicInfo(command.getDestiId());
				for (ContingutDto contingut: fills) {
					if (contingut.isDocument()) {
						if (contingut.getNom().equals(origen.getNom())) {
							if (command.getOrigenId() == null || !command.getOrigenId().equals(contingut.getId())) {
								context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("NomDocumentNoRepetit", null, new RequestContext(request).getLocale()))
								.addNode("estructuraCarpetesJson")
								.addConstraintViolation();
								valid = false;
								break;
							}
						}
					}
				}
			}
		}
		
		if (!valid)
			context.disableDefaultConstraintViolation();
		
		return valid;
	}

}
