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
 * Constraint de validació que controla alguns camps del formulari d'importació
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=ImportacioValidator.class)
public @interface Importacio {

	String message() default "Aquest camp és obligatori";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
