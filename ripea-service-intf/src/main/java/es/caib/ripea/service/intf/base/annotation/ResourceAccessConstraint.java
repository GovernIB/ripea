package es.caib.ripea.service.intf.base.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import es.caib.ripea.service.intf.dto.ExtendedPermissionEnum;

/**
 * Anotació per a configurar les restriccions d'accés a un recurs.
 * 
 * @author Limit Tecnologies
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ResourceAccessConstraint {

	ResourceAccessConstraintType type();
	String[] roles() default {};
	ExtendedPermissionEnum[] grantedPermissions() default {};

	enum ResourceAccessConstraintType {
		PERMIT_ALL,
		AUTHENTICATED,
		ROLE,
		CUSTOM
	}
}
