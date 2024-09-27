/**
 * 
 */
package es.caib.ripea.war.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Constraint de validació que controla que no es doni d'alta dues
 * vegades el mateix interesat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=RepresentantNotSameInteressatValidator.class)
public @interface RepresentantNotSameInteressat {

	String message() default "Ja existeix un altre interessat donat d''alta amb aquestes dades";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
