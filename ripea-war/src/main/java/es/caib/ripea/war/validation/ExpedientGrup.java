/**
 *
 */
package es.caib.ripea.war.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;


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
