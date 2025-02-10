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
 * Constraint de validació que controla que no es repeteixi
 * el codi de meta-document.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=CodiMetaExpedientTascaNoRepetitValidator.class)
public @interface CodiMetaExpedientTascaNoRepetit {

	String message() default "El procediment ja conté una tasca amb aquest codi";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	String campId();

	String campCodi();

	String campEntitatId();

	String campMetaExpedientId();

}
