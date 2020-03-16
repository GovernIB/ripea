/**
 * 
 */
package es.caib.ripea.war.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import es.caib.ripea.core.api.dto.MetaDocumentFirmaFluxTipusEnumDto;
import es.caib.ripea.war.command.MetaDocumentCommand;

/**
 * Constraint de validació que controla que no es repeteixi
 * el codi de meta-document.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class FluxIdNotEmptyValidator implements ConstraintValidator<FluxIdNotEmpty, MetaDocumentCommand> {


	@Override
	public void initialize(final FluxIdNotEmpty constraintAnnotation) {
	}

	@Override
	public boolean isValid(final MetaDocumentCommand metaDocument, final ConstraintValidatorContext context) {
		
		if (metaDocument.isFirmaPortafirmesActiva() && metaDocument.getPortafirmesFluxTipus().equals(MetaDocumentFirmaFluxTipusEnumDto.PORTAFIB) && (metaDocument.getPortafirmesFluxId() == null || metaDocument.getPortafirmesFluxId().isEmpty()))
			return false;
		else 
			return true;
        
	}

}
