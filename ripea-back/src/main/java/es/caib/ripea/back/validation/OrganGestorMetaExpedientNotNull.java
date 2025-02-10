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
 * Constraint de validació que controla que s'especifiqui un organ gestor quan es crea un 
 * metaexpedient com a administrador d'organ gestor.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=OrganGestorMetaExpedientNotNullValidator.class)
public @interface OrganGestorMetaExpedientNotNull {

	String message() default "És obligatori especificar un organ gestor per a un metaexpedient";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};


}
