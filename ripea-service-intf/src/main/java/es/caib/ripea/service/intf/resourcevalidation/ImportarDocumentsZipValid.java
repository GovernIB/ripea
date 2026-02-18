package es.caib.ripea.service.intf.resourcevalidation;

import es.caib.ripea.service.intf.model.ExpedientResource.ImportarDocumentsForm;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ImportarDocumentsZipValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ImportarDocumentsZipValid {
    String message() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String[] fields() default {};
}