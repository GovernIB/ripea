/**
 * 
 */
package es.caib.ripea.service.intf.dto;

import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.exception.PermissionDeniedException;
import es.caib.ripea.service.intf.exception.ValidationException;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.Serializable;
import java.util.Date;


/**
 * Excepció llençada per un servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExcepcioLogDto implements Serializable {

	private Long index;
	private EntitatDto entitat;
	private Date data = new Date();
	private Class<?> tipus;
	private Object objectId;
	private Class<?> objectClass;
	private String param1;
	private String param2;
	private String message;
	private String stacktrace;



	public ExcepcioLogDto(Throwable exception) {
		if (exception instanceof NotFoundException) {
			this.objectId = ((NotFoundException)exception).getObjectId();
			this.objectClass = ((NotFoundException)exception).getObjectClass();
		} else if (exception instanceof PermissionDeniedException) {
			this.objectId = ((PermissionDeniedException)exception).getObjectId();
			this.objectClass = ((PermissionDeniedException)exception).getObjectClass();
			this.param1 = ((PermissionDeniedException)exception).getUserName();
			this.param2 = ((PermissionDeniedException)exception).getPermissionName();
		} else if (exception instanceof ValidationException) {
			this.objectId = ((ValidationException)exception).getObjectId();
			this.objectClass = ((ValidationException)exception).getObjectClass();
			this.param1 = ((ValidationException)exception).getError();
		}
		this.setTipus(exception.getClass());
		this.setMessage(exception.getMessage());
		this.setStacktrace(ExceptionUtils.getStackTrace(exception));
	}

	public Long getIndex() {
		return index;
	}
	public void setIndex(Long index) {
		this.index = index;
	}
	public EntitatDto getEntitat() {
		return entitat;
	}
	public void setEntitat(EntitatDto entitat) {
		this.entitat = entitat;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public Class<?> getTipus() {
		return tipus;
	}
	public Object getObjectId() {
		return objectId;
	}
	public void setObjectId(Object objectId) {
		this.objectId = objectId;
	}
	public Class<?> getObjectClass() {
		return objectClass;
	}
	public void setObjectClass(Class<?> objectClass) {
		this.objectClass = objectClass;
	}
	public String getParam1() {
		return param1;
	}
	public void setParam1(String param1) {
		this.param1 = param1;
	}
	public String getParam2() {
		return param2;
	}
	public void setParam2(String param2) {
		this.param2 = param2;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getStacktrace() {
		return stacktrace;
	}
	public void setStacktrace(String stacktrace) {
		this.stacktrace = stacktrace;
	}
	public void setTipus(Class<?> tipus) {
		this.tipus = tipus;
	}

	private static final long serialVersionUID = -139254994389509932L;

}
