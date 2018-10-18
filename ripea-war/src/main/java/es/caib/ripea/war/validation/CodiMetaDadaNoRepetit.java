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
 * Constraint de validació que controla que no es repeteixi
 * el codi de meta-dada.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=CodiMetaDadaNoRepetitValidator.class)
public @interface CodiMetaDadaNoRepetit {

	String message() default "Ja existeix una altra meta-dada amb aquest codi";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	String campId();

	String campCodi();

	String campEntitatId();

	String campMetaNodeId();

}
