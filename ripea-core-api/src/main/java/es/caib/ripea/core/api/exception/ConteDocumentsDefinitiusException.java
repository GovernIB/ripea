/**
 * 
 */
package es.caib.ripea.core.api.exception;

/**
 * Excepció que es llança quan es vol esborrar un contingut que conté documents
 * amb estat definitiu.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class ConteDocumentsDefinitiusException extends RuntimeException {

	private Object objectId;
	private Class<?> objectClass;
	
	public ConteDocumentsDefinitiusException(
			Object objectId,
			Class<?> objectClass) {
		super();
		this.objectId = objectId;
		this.objectClass = objectClass;
	}
	public ConteDocumentsDefinitiusException() {
		super();
	}

	public Object getObjectId() {
		return objectId;
	}
	public Class<?> getObjectClass() {
		return objectClass;
	}

	public String getErrorInfo() {
		if (objectClass != null && objectId != null) {
			StringBuilder sb = new StringBuilder();
			sb.append("(");
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
			return "";
		}
	}

}
