/**
 * 
 */
package es.caib.ripea.war.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import es.caib.ripea.war.command.MetaDocumentCommand;

/**
 * Constraint de validació que controla que no es repeteixi
 * el codi de meta-document.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PortafirmesDocumentTipusNotEmptyValidator implements ConstraintValidator<PortafirmesDocumentTipusNotEmpty, MetaDocumentCommand> {


	@Override
	public void initialize(final PortafirmesDocumentTipusNotEmpty constraintAnnotation) {
	}

	@Override
	public boolean isValid(final MetaDocumentCommand metaDocument, final ConstraintValidatorContext context) {
		
		if (metaDocument.isFirmaPortafirmesActiva() && (metaDocument.getPortafirmesDocumentTipus() == null || metaDocument.getPortafirmesDocumentTipus().isEmpty()))
			return false;
		else 
			return true;
        
	}

}
