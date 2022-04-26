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
 * el codi SIA de meta-expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=MetaExpedientCodiSiaNoRepetitValidator.class)
public @interface MetaExpedientCodiSiaNoRepetit {

	String message() default "Ja existeix un altre procediment amb aquest codi SIA";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};


}
