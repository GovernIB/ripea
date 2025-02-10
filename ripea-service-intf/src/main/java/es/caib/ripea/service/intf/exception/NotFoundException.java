/**
 * 
 */
package es.caib.ripea.service.intf.exception;

/**
 * Excepció que es llança quan l'objecte especificat no existeix.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class NotFoundException extends RuntimeException {

	private Object objectId;
	private Class<?> objectClass;
	
	public NotFoundException(
			Object objectId,
			Class<?> objectClass) {
		super(getExceptionMessage(objectId, objectClass));
		this.objectId = objectId;
		this.objectClass = objectClass;
	}

	public Object getObjectId() {
		return objectId;
	}

	public Class<?> getObjectClass() {
		return objectClass;
	}

	public static String getExceptionMessage(
			Object objectId,
			Class<?> objectClass) {
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
		return sb.toString();
	}

}
