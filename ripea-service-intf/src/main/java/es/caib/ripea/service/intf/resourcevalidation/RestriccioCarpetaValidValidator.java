package es.caib.ripea.service.intf.resourcevalidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.stereotype.Component;

import es.caib.ripea.service.intf.model.CarpetaResource;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RestriccioCarpetaValidValidator implements ConstraintValidator<RestriccioCarpetaValid, CarpetaResource> {

    private void addViolation(ConstraintValidatorContext context, String field, String message) {
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(field)
                .addConstraintViolation();
    }
    private void notNullViolation(ConstraintValidatorContext context, String field) {
        addViolation(context, field, "{javax.validation.constraints.NotNull.message}");
    }

    @Override
    public boolean isValid(CarpetaResource resource, ConstraintValidatorContext context) {
        boolean valid = true;
        context.disableDefaultConstraintViolation();

        if (resource.getRestringida() != null && resource.getRestringida()) {
            if (resource.getMotiuRestriccio() == null || resource.getMotiuRestriccio().isBlank()) {
                notNullViolation(context, CarpetaResource.Fields.motiuRestriccio);
                valid = false;
            }
            if (resource.getRestriccions() == null || resource.getRestriccions().isEmpty()) {
                notNullViolation(context, CarpetaResource.Fields.restriccions);
                valid = false;
            }
        }

        return valid;
    }
}