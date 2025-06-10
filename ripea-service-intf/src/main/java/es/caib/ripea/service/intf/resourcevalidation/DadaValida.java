package es.caib.ripea.service.intf.resourcevalidation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = DadaValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DadaValida {
    String message() default "{es.caib.ripea.service.intf.resourcevalidation.DadaValida}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String[] fields() default {"text"};
}