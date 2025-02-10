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
 * Constraint de validació que controla que el nombre de
 * document d'identitat sigui vàlid. Els tipus de document
 * suportats son: NIF, DNI, NIE i CIF. 
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=DocumentIdentitatValidator.class)
public @interface DocumentIdentitat {

	String message() default "Número de document incorrecte";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
