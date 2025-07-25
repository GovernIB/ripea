package es.caib.ripea.service.intf.resourcevalidation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import es.caib.ripea.service.intf.model.DocumentResource.ViaFirmaForm;

@Documented
@Constraint(validatedBy = ViaFirmaValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ViaFirmaValid {
	String message() default "{javax.validation.constraints.NotNull.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String[] fields() default {ViaFirmaForm.Fields.tipusDestinatari};
}
