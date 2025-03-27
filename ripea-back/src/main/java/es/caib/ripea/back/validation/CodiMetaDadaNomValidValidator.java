package es.caib.ripea.back.validation;

import es.caib.ripea.back.command.MetaDadaCommand;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CodiMetaDadaNomValidValidator implements ConstraintValidator<CodiMetaDadaNomValid, MetaDadaCommand> {

	@Override
	public void initialize(CodiMetaDadaNomValid constraintAnnotation) {}

	@Override
	public boolean isValid(MetaDadaCommand value, ConstraintValidatorContext context) {
		return checkIfNameIsValidPartJava(value.getCodi());
	}

	private static boolean checkIfNameIsValidPartJava(String nameToCheck) {
		
		boolean nameValid = true;
		for (int i = 0; i < nameToCheck.length(); i++) {
			
			int codePoint = nameToCheck.codePointAt(i);
			
			//Les dues primeres lletres han de ser minúscules
			if (i < 2 && Character.isUpperCase(codePoint)) {
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