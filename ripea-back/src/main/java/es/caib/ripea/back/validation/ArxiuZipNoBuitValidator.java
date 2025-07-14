package es.caib.ripea.back.validation;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.RequestContext;

import es.caib.ripea.back.helper.MessageHelper;

/**
* Valida que l'arxiu zip no estigui buit.
*
* @author Limit Tecnologies <limit@limit.es>
*/
public class ArxiuZipNoBuitValidator implements ConstraintValidator<ArxiuZipNoBuit, MultipartFile> {

	@Autowired
	private HttpServletRequest request;
	
	@Override
	public void initialize(final ArxiuZipNoBuit constraintAnnotation) {
	}

	@Override
	public boolean isValid(
			final MultipartFile value,
			final ConstraintValidatorContext context) {
		
		boolean valid = true;
		if (value.getSize() == 0) {
			context.buildConstraintViolationWithTemplate(
					MessageHelper.getInstance().getMessage(
							"contingut.document.form.camp.arxiu.empty", null, new RequestContext(request).getLocale()))
			.addConstraintViolation();
			valid = false;
		} else {
			valid = true;
		}
		
		if (!valid)
			context.disableDefaultConstraintViolation();
		
		return valid;
	}

}
