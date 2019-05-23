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

import es.caib.ripea.core.api.dto.MetaDadaDto;
import es.caib.ripea.core.api.service.MetaDadaService;
import es.caib.ripea.war.command.MetaDadaCommand;

/**
 * Constraint de validació que controla que no es repeteixi
 * nom del codi de meta-dada es valid.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CodiMetaDadaNomValidValidator implements ConstraintValidator<CodiMetaDadaNomValid, MetaDadaCommand> {

	@Override
	public void initialize(CodiMetaDadaNomValid constraintAnnotation) {
		
	}

	@Override
	public boolean isValid(
			MetaDadaCommand value,
			ConstraintValidatorContext context) {
		
		return checkIfNameIsValidPartJava(value.getCodi());
	}
	
	
	/**
	 * Checks if the @param nameToCheck consists of characters that may be part of a Java identifier as others than the first character
	 * 
	 * @param nameToCheck
	 * @return
	 */
	private static boolean checkIfNameIsValidPartJava(String nameToCheck) {
		
		boolean nameValid = true;
		for (int i = 0; i < nameToCheck.length(); i++) {
			int codePoint = nameToCheck.codePointAt(i);
			if (!Character.isJavaIdentifierPart(codePoint)) {
				nameValid = false;
				break;
			}
		}
		return nameValid;
	}






}
