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
@Target(ElementType.TYPE)
public @interface ResourceField {

	public String type() default "";
	public boolean enumType() default false;
	public String descriptionField() default "";
	public boolean onChangeActive() default false;
	public String title() default "";
	public String titleI18n() default "";
	public String helperText() default "";
	public String helperTextI18n() default "";
	public String springFilter() default "";
	public String[] namedQueries() default {};

}
