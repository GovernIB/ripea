/**
 * 
 */
package es.caib.ripea.war.validation;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.support.RequestContext;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.service.MetaExpedientService;
import es.caib.ripea.war.command.PermisCommand;
import es.caib.ripea.war.helper.EntitatHelper;
import es.caib.ripea.war.helper.MessageHelper;


/**
 * Constraint de validació que controla que l'òrgan gestor sigui obligatori per al permís d'un procediment
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PermisMetaExpedientValidator implements ConstraintValidator<PermisMetaExpedient, PermisCommand> {
	
	@Autowired
	private HttpServletRequest request;
	@Autowired
	private MetaExpedientService metaExpedientService;
	

	@Override
	public void initialize(final PermisMetaExpedient constraintAnnotation) {
	}

	@Override
	public boolean isValid(final PermisCommand permisCommand, final ConstraintValidatorContext context) {
		boolean valid = true;
		try {			
			String urlOperativa = URLDecoder.decode(request.getRequestURI().split("/")[2], StandardCharsets.UTF_8.name());
			if ("metaExpedient".equals(urlOperativa)) { //permís d'un metaExpedient
				String metaExpedientId = URLDecoder.decode(request.getRequestURI().split("/")[3], StandardCharsets.UTF_8.name());
				EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
				MetaExpedientDto metaExpedient = metaExpedientService.findById(entitatActual.getId(), Long.valueOf(metaExpedientId));
				
				if (metaExpedient.isComu() && permisCommand.getOrganGestorId() == null) {
					valid = false;
					context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage("metaexpedient.permis.validator.organ.gestor", null, new RequestContext(request).getLocale()))
					.addNode("organGestorId")
					.addConstraintViolation();
				}
			}

        } catch (final Exception ex) {
        	LOGGER.error("Error al validar si el òrgan gestor es obligatori", ex);
        	return false;
        }
		
		if (!valid)
			context.disableDefaultConstraintViolation();
        return valid;
	}

	
	private static final Logger LOGGER = LoggerFactory.getLogger(PermisMetaExpedientValidator.class);

}
