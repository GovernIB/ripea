package es.caib.ripea.service.intf.resourcevalidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import es.caib.ripea.service.intf.model.MetaExpedientResource;

public class MetaExpedientValidator implements ConstraintValidator<MetaExpedientValid, MetaExpedientResource> {

	@Override
	public boolean isValid(MetaExpedientResource resource, ConstraintValidatorContext context) {

		boolean valid = true;

		//Si el procediment no és comú, l'òrgan gestor és obligatori
		if (!resource.isProcedimentComu() && resource.getOrganGestor() == null) {
			context
				.buildConstraintViolationWithTemplate("{javax.validation.constraints.NotNull.message}")
				.addPropertyNode(MetaExpedientResource.Fields.organGestor)
				.addConstraintViolation()
				.disableDefaultConstraintViolation();
			valid = false;
		}

		return valid;
	}
}
