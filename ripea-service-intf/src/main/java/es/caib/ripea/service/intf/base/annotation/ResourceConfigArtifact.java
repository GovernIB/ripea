/**
 * 
 */
package es.caib.ripea.service.intf.base.annotation;

import es.caib.ripea.service.intf.base.model.ResourceArtifactType;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotació per a configurar un artefacte.
 * 
 * @author Limit Tecnologies
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ResourceConfigArtifact {

	ResourceArtifactType type();
	String code();
	Class<? extends Serializable> formClass() default Serializable.class;

}
