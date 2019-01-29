/**
 * 
 */
package es.caib.ripea.war.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.caib.ripea.war.command.MetaDocumentCommand;

/**
 * Constraint de validació que controla que no es repeteixi
 * el codi de meta-document.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ResponsableNotEmptyValidator implements ConstraintValidator<ResponsableNotEmpty, MetaDocumentCommand> {


	@Override
	public void initialize(final ResponsableNotEmpty constraintAnnotation) {
	}

	@Override
	public boolean isValid(final MetaDocumentCommand metaDocument, final ConstraintValidatorContext context) {
		
		if (metaDocument.isFirmaPortafirmesActiva() && (metaDocument.getPortafirmesResponsables() == null || metaDocument.getPortafirmesResponsables().length==0))
			return false;
		else 
			return true;
        
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(ResponsableNotEmptyValidator.class);

}
