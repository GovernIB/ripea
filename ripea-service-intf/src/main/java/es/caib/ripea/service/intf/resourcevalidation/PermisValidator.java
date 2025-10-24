package es.caib.ripea.service.intf.resourcevalidation;

import es.caib.ripea.service.intf.model.AclSidResource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
@RequiredArgsConstructor
public class PermisValidator implements ConstraintValidator<PermisValid, AclSidResource.DeletePermisionFormAction>{

	@Override
	public boolean isValid(AclSidResource.DeletePermisionFormAction resource, ConstraintValidatorContext context) {
        boolean valid = true;

        if (!AclSidResource.ClassType.ENTITY.equals(resource.getClassType())
                && resource.getObjectId() == null
        ){
            context
                    .buildConstraintViolationWithTemplate("{javax.validation.constraints.NotNull.message}")
                    .addPropertyNode(AclSidResource.DeletePermisionFormAction.Fields.objectId)
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            valid = false;
        }

        return valid;
	}
}