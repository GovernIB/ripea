package es.caib.ripea.service.intf.base.annotation;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import es.caib.ripea.service.intf.base.model.ResourceArtifactType;

/**
 * Anotació per a configurar un artefacte.
 * 
 * @author Limit Tecnologies
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ResourceArtifact {

	ResourceArtifactType type();
	String code();
	boolean requiresId() default false;
	Class<? extends Serializable> formClass() default Serializable.class;
	ResourceAccessConstraint[] accessConstraints() default {};

}
