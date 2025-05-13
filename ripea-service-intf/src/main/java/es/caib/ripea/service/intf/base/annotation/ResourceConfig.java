package es.caib.ripea.service.intf.base.annotation;

import es.caib.ripea.service.intf.base.model.Resource;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotació per a configurar un recurs de l'API REST.
 * 
 * @author Límit Tecnologies
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ResourceConfig {

	@AliasFor("name")
	String value() default "";
	@AliasFor("value")
	String name() default "";
	String resourceDescription() default "";
	String resourceDescriptionI18n() default "";
	String descriptionField() default "";
	String orderField() default "";
	ResourceSort[] defaultSortFields() default {};
	String[] quickFilterFields() default {};
	Class<? extends Resource> parentEntity() default Resource.class;
	ResourceConfigArtifact[] artifacts() default {};

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	@interface ResourceSort {
		String field() default "";
		org.springframework.data.domain.Sort.Direction direction() default org.springframework.data.domain.Sort.Direction.ASC;
	}

}
