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
 * el nom d'un expedient i el seu tipus
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=ExpedientNomUniqueValidator.class)
public @interface ExpedientNomUnique {

	String message() default "Ja existeix un altre expedient amb el mateix títol per aquest procediment";

	Class<?>[] groups() default { };

	Class<? extends Payload>[] payload() default {};

	String campId();
	
	String campPareId();
	
	String campMetaExpedientId();

	String campNom();

	String campEntitatId();
	
	String campOrganGestorId();
}
