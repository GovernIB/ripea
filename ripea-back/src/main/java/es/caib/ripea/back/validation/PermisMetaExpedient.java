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
 * Constraint de validació que controla que l'òrgan gestor sigui obligatori per al permís d'un procediment
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=PermisMetaExpedientValidator.class)
public @interface PermisMetaExpedient {

	String message() default "Error en la validació del permís.";

	Class<?>[] groups() default { };

	Class<? extends Payload>[] payload() default {};


}
