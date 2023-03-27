/**
 * 
 */
package es.caib.ripea.war.validation;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.support.RequestContext;

import es.caib.ripea.war.command.MetaDadaCommand;
import es.caib.ripea.war.helper.MessageHelper;

/**
 * Constraint de validació que controla que el valor del camp nom metadada arxiu estigui informat si la metadada és enviable
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ValorMetaDadaArxiuValidator implements ConstraintValidator<ValorMetaDadaArxiu, MetaDadaCommand> {
	
	@Autowired
	private HttpServletRequest request;
	
	@Override
	public void initialize(ValorMetaDadaArxiu constraintAnnotation) {
		
	}

	@Override
	public boolean isValid(
			MetaDadaCommand value,
			ConstraintValidatorContext context) {
		
		boolean metadadaArxiuEmpty = (value.getMetadadaArxiu() == null || value.getMetadadaArxiu().isEmpty());
		
		if (value.isEnviable() && metadadaArxiuEmpty) {
 			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(
					MessageHelper.getInstance().getMessage("metadada.form.camp.metadada.arxiu.validator", null, new RequestContext(request).getLocale()))
			.addNode("metadadaArxiu")
			.addConstraintViolation();
			return false;
		}
		return true;
	}

}
