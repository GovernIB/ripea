/**
 * 
 */
package es.caib.ripea.core.api.exception;

import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Excepció que es llança quan l'usuari no te permisos per accedir a un objecte.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class PermissionDeniedException extends RuntimeException {

	private Object objectId;
	private Class<?> objectClass;
	private String userName;
	private String permissionName;
	private String extendedInfo;
	
	public PermissionDeniedException(
			Object objectId,
			Class<?> objectClass,
			String userName,
			String permissionName,
			String extendedInfo) {
		super(getExceptionMessage(
				objectId,
				objectClass,
				userName,
				permissionName,
				extendedInfo));
		this.objectId = objectId;
		this.objectClass = objectClass;
		this.userName = userName;
		this.permissionName = permissionName;
		this.extendedInfo = extendedInfo;
	}
	
	public PermissionDeniedException(
			Object objectId,
			Class<?> objectClass,
			String userName,
			String permissionName) {
		super(getExceptionMessage(
				objectId,
				objectClass,
				userName,
				permissionName,
				null));
		this.objectId = objectId;
		this.objectClass = objectClass;
		this.userName = userName;
		this.permissionName = permissionName;
	}
	
	public PermissionDeniedException(
			Object objectId,
			Class<?> objectClass,
			String permissionName) {
		super(getExceptionMessage(
				objectId,
				objectClass,
				SecurityContextHolder.getContext().getAuthentication().getName(),
				permissionName,
				null));
		this.objectId = objectId;
		this.objectClass = objectClass;
		this.userName = SecurityContextHolder.getContext().getAuthentication().getName();
		this.permissionName = permissionName;
	}

	public PermissionDeniedException(
			Class<?> objectClass,
			Object objectId,
			String permissionName,
			String extendedInfo) {
		super(getExceptionMessage(
				objectId,
				objectClass,
				SecurityContextHolder.getContext().getAuthentication().getName(),
				permissionName,
				extendedInfo));
		this.objectId = objectId;
		this.objectClass = objectClass;
		this.userName = SecurityContextHolder.getContext().getAuthentication().getName();
		this.permissionName = permissionName;
		this.extendedInfo = extendedInfo;
	}
	
	public Object getObjectId() {
		return objectId;
	}
	public Class<?> getObjectClass() {
		return objectClass;
	}
	public String getUserName() {
		return userName;
	}
	public String getPermissionName() {
		return permissionName;
	}
	public String getExtendedInfo() {
		return extendedInfo;
	}

	public static String getExceptionMessage(
			Object objectId,
			Class<?> objectClass,
			String userName,
			String permissionName,
			String extendedInfo) {
		StringBuilder sb = new StringBuilder();
		if (objectClass != null)
			sb.append(objectClass.getName());
		else
			sb.append("null");
		sb.append("#");
		if (objectId != null)
			sb.append(objectId.toString());
		else
			sb.append("null");
		sb.append(", ");
		sb.append(userName);
		sb.append(", ");
		sb.append(permissionName);
		if (extendedInfo != null)
			sb.append(": " + extendedInfo);
		return sb.toString();
	}

}
