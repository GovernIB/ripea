package es.caib.ripea.service.intf.resourcevalidation;

import es.caib.ripea.service.intf.model.DocumentResource.NewDocPinbalForm;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DocPinbalValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DocPinbalValid {
    String message() default "{javax.validation.constraints.NotNull.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String[] fields() default {NewDocPinbalForm.Fields.codiServeiPinbal};
}