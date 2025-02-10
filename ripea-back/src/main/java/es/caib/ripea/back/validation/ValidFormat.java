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
 * Constraint de validació que controla el format dels camps concepte i descripció d'una notificació
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=ValidFormatValidator.class)
public @interface ValidFormat {
	
	String message() default "Error en validar el format dels camps concepte i descripció";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
