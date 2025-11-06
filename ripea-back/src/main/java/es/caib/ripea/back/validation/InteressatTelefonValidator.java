package es.caib.ripea.back.validation;

import es.caib.ripea.back.command.InteressatCommand;
import es.caib.ripea.back.helper.MessageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.support.RequestContext;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class InteressatTelefonValidator implements ConstraintValidator<InteressatTelefon, Object> {

    @Autowired
    private HttpServletRequest request;

	@Override
	public void initialize(final InteressatTelefon constraintAnnotation) {
	}

	@Override
	public boolean isValid(final Object value, final ConstraintValidatorContext context) {
		try {
			
			InteressatCommand interessat = (InteressatCommand)value;
			boolean valid = true;
			if (interessat!=null && interessat.getTelefon()!=null && !"".equals(interessat.getTelefon())) {
				String telefon = interessat.getTelefon().trim();
	            if (telefon.length()<9 || telefon.length()>20) {
					context
					.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage("javax.validation.constraints.Size.message.alternativ", new Object[] { 9, 20 }, new RequestContext(request).getLocale()))
					.addNode("telefon")
					.addConstraintViolation();
					valid = false;
	            }
			}
			return valid;
		} catch (final Exception ex) {
        	LOGGER.error("Error al validar interessat telefon", ex);
        	return false;
        }
	}
	private static final Logger LOGGER = LoggerFactory.getLogger(InteressatTelefonValidator.class);
}