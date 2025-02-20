package es.caib.ripea.service.intf.base.service;

import org.springframework.security.access.PermissionEvaluator;

/**
 * Definició del servei d'avaluació de permisos.
 * 
 * @author Límit Tecnologies
 */
public interface PermissionEvaluatorService extends PermissionEvaluator {

	public enum RestApiOperation {
		CREATE,
		UPDATE,
		PATCH,
		DELETE,
		GET_ONE,
		FIND,
		ARTIFACT,
		ACTION,
		REPORT
	}

}
