package es.caib.ripea.service.intf.resourcevalidation;

import es.caib.ripea.service.intf.model.DocumentResource;
import es.caib.ripea.service.intf.model.ExpedientResource;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = MassiveImportDocValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MassiveImportDocValid {
    String message() default "{es.caib.ripea.service.intf.resourcevalidation.MassiveImportDocValid}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String[] fields() default {ExpedientResource.MassiveImportDocsAction.Fields.documents};
}