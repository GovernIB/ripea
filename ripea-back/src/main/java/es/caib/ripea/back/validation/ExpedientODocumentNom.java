/**
 *
 */
package es.caib.ripea.back.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Constraint de validació que controla que el nom d'un expedient o d'un document no conté un punt
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=ExpedientODocumentNomValidator.class)
public @interface ExpedientODocumentNom {

	String message() default "El nom no pot contenir un punt";

	Class<?>[] groups() default { };

	Class<? extends Payload>[] payload() default {};


}
