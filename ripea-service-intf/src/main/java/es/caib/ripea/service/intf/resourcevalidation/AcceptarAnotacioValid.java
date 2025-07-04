package es.caib.ripea.service.intf.resourcevalidation;

import es.caib.ripea.service.intf.model.ExpedientPeticioResource;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AcceptarAnotacioValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AcceptarAnotacioValid {
    String message() default "{javax.validation.constraints.NotNull.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String[] fields() default {ExpedientPeticioResource.AcceptarAnotacioForm.Fields.annexos};
}