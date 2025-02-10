package es.caib.ripea.service.intf.dto;

import java.io.Serializable;

/**
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum PermissionEnumDto implements Serializable {
	READ,
	WRITE,
	CREATE,
	DELETE,
	ADMINISTRATION,
	STATISTICS
}
