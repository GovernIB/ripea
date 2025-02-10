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
 * Constraint de validació que controla que
 * nom del codi de meta-dada es valid.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=CodiMetaDadaNomValidValidator.class)
public @interface CodiMetaDadaNomValid {

	String message() default "Valor del camp codi no es pot constar de caràcters que no poden formar part del correcte identificador java i no es pot començar amb lletra majúscula";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};


}
