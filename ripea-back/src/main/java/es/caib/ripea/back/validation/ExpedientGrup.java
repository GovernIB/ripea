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
 * Constraint de validació que controla que un camp grup es obligatori quan gestioAmbGrupsActiva == true
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=ExpedientGrupValidator.class)
public @interface ExpedientGrup {

	String message() default "Camp grup es obligatori";

	Class<?>[] groups() default { };

	Class<? extends Payload>[] payload() default {};


}
