package es.caib.ripea.service.intf.resourcevalidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import es.caib.ripea.service.intf.model.DadaResource;

public class DadaValidator implements ConstraintValidator<DadaValida, DadaResource>{

	@Override
	public boolean isValid(DadaResource resource, ConstraintValidatorContext context) {
        String value = resource.getNullFieldNamebyTipus();
        if (value!=null) {
            context
            .buildConstraintViolationWithTemplate("{es.caib.ripea.service.intf.resourcevalidation.DadaValida."+value+"}")
            .addPropertyNode(value)
            .addConstraintViolation()
            .disableDefaultConstraintViolation();
            return false;
        }
		return true;
	}

}