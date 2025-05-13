package es.caib.ripea.service.intf.base.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotació per a configurar un camp d'un recurs de l'API REST.
 * 
 * @author Límit Tecnologies
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ResourceField {

	String type() default "";
	boolean enumType() default false;
	String descriptionField() default "";
	boolean onChangeActive() default false;
	String springFilter() default "";
	String[] namedQueries() default {};

}
