package es.caib.ripea.service.intf.resourcevalidation;

import es.caib.ripea.service.intf.model.ExpedientResource.ImportarDocumentsForm;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ImportarDocumentValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ImportarDocumentValid {
    String message() default "{javax.validation.constraints.NotNull.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String[] fields() default {ImportarDocumentsForm.Fields.tipusImportacio};
}