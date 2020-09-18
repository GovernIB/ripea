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
 * el nom d'un expedient i el seu tipus
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=ExpedientNomUniqueValidator.class)
public @interface ExpedientNomUnique {

	String message() default "Ja existeix un altre expedient amb el mateix tipus i nom";

	Class<?>[] groups() default { };

	Class<? extends Payload>[] payload() default {};

	String campId();
	
	String campPareId();
	
	String campMetaExpedientId();

	String campNom();

	String campEntitatId();
}
