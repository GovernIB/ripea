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
 * Validació de la relació d'un expedient amb altres expedients.
 * Valida:
 * - Que no es relacioni amb ell mateix
 * - Que no estigui ja relacionat
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Constraint(validatedBy = ExpedientRelacionarValidator.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE  })
@Retention(RetentionPolicy.RUNTIME)
public @interface ExpedientRelacionar {

	String message() default "Error en la validació de la relació";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
