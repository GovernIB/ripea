/**
 * 
 */
package es.caib.ripea.back.validation;

import es.caib.ripea.back.command.MetaDocumentCommand;
import es.caib.ripea.service.intf.dto.MetaDocumentFirmaFluxTipusEnumDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

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
		
		if (metaDocument.isFirmaPortafirmesActiva() && metaDocument.getPortafirmesFluxTipus().equals(MetaDocumentFirmaFluxTipusEnumDto.SIMPLE) && !metaDocument.isComu() && (metaDocument.getPortafirmesResponsables() == null || metaDocument.getPortafirmesResponsables().length==0))
			return false;
		else 
			return true;
        
	}

}
