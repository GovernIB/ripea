package es.caib.ripea.service.intf.resourcevalidation;

import es.caib.ripea.service.intf.model.ExpedientResource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
@RequiredArgsConstructor
public class ExpedientValidator implements ConstraintValidator<ExpedientValid, ExpedientResource>{

	@Override
	public boolean isValid(ExpedientResource resource, ConstraintValidatorContext context) {
        boolean valid = true;

        if (resource.isGestioAmbGrupsActiva() && resource.getGrup() == null){
            context
                    .buildConstraintViolationWithTemplate("{javax.validation.constraints.NotNull.message}")
                    .addPropertyNode(ExpedientResource.Fields.grup)
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            valid = false;
        }

        return valid;
	}
}