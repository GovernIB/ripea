/**
 * 
 */
package es.caib.ripea.core.api.exception;

/**
 * Excepció que es llança per errors validant un objecte o el seu estat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class ValidationException extends RuntimeException {

	private Object objectId;
	private Class<?> objectClass;
	private String error;

	public ValidationException(
			Object objectId,
			Class<?> objectClass,
			String error) {
		super(error);
		this.objectId = objectId;
		this.objectClass = objectClass;
		this.error = error;
	}
	public ValidationException(
			String error) {
		super(error);
		this.error = error;
	}

	public Object getObjectId() {
		return objectId;
	}
	public Class<?> getObjectClass() {
		return objectClass;
	}
	public String getError() {
		return error;
	}

	public String getErrorInfo() {
		if (objectClass != null && objectId != null) {
			StringBuilder sb = new StringBuilder();
			sb.append(error);
			sb.append(" (");
			if (objectClass != null)
				sb.append(objectClass.getClass().getName());
			else
				sb.append("null");
			sb.append("#");
			if (objectId != null)
				sb.append(objectId.toString());
			else
				sb.append("null");
			sb.append(")");
			return sb.toString();
		} else {
			return error;
		}
	}

}
