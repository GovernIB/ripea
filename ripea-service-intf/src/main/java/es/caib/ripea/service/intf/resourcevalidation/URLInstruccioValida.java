package es.caib.ripea.service.intf.resourcevalidation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Constraint de validació que controla que la URL d'instrucció tingui el format
 * correcte: ha de començar per http:// o https:// i acabar amb [ENI].
 * Els valors nuls o buits es consideren vàlids (usar @NotEmpty si és obligatori).
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = URLInstruccioValidaValidator.class)
public @interface URLInstruccioValida {

	String message() default "{url.instruccio.url.valid}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
