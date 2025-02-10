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
 * Validació de si existeix un document digital al formulari de
 * creació/modificació de document.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidIfSeparadaValidator.class)
public @interface ValidIfSeparada {

	String message() default "La firma adjunta no existeix";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
