package es.caib.ripea.back.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Objecte que conté la informació dels missatges d'error de l'API REST.
 * 
 * @author Límit Tecnologies
 */
@Getter
@Setter
@RequiredArgsConstructor
public class ErrorObject {

	protected final int status;
	protected final String message;
	protected Throwable throwable;
	protected String errorClassName;
	protected String exceptionMessage;
	protected String stackTrace;
	protected String requestUri;
	protected boolean notFound;
	protected boolean sistemaExtern;
	protected boolean accessDenied;
	protected AccessDeniedSource accessDeniedSource;

	public static enum AccessDeniedSource {
		SPRING,
		EJB
	}
	
	public String getErrorClassName() {
		if (throwable!=null) {
			return throwable.getClass().getCanonicalName();
		}
		return "";
	}
	
	@Override
	public String toString() {
		return "ErrorObject [status=" + status + ", message=" + message + ", stackTrace="
				+ stackTrace + ", notFound=" + notFound + ", sistemaExtern=" + sistemaExtern + ", accessDenied="
				+ accessDenied + ", accessDeniedSource=" + accessDeniedSource + "]";
	}
}