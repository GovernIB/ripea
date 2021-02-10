/**
 * 
 */
package es.caib.ripea.war.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import es.caib.ripea.war.command.MetaDadaCommand;

/**
 * Constraint de validació que controla que
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
	 * Checks if the @param nameToCheck consists of characters that may be part of a Java identifier 
	 * 
	 * @param nameToCheck
	 * @return
	 */
	private static boolean checkIfNameIsValidPartJava(String nameToCheck) {
		
		boolean nameValid = true;
		for (int i = 0; i < nameToCheck.length(); i++) {
			int codePoint = nameToCheck.codePointAt(i);
			
			if (i == 0 && Character.isUpperCase(codePoint)) {
				nameValid = false;
				break;
			}
			
			if (!Character.isJavaIdentifierPart(codePoint)) {
				nameValid = false;
				break;
			}
		}
		return nameValid;
	}






}
