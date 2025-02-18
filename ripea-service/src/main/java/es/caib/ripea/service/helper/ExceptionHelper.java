package es.caib.ripea.service.helper;

import org.apache.commons.lang3.exception.ExceptionUtils;

public class ExceptionHelper {

	/**
	 * Defineix el nombre d'excepcions anidades que vols comprovar
	 */
	private static final int N_CHECKED_NESTED_EXCEPTIONS = 3;
	
	public static boolean isExceptionOrCauseInstanceOf(Exception e, Class<? extends Exception> exceptionClass) {
		
		return isExceptionOrCauseInstanceOf(e, exceptionClass, N_CHECKED_NESTED_EXCEPTIONS);
	}
	
	public static boolean isExceptionOrCauseInstanceOf(Exception e, Class<? extends Exception> exceptionClass, int nCheckedNestedExceptions) {
		int i = 0;
		boolean isTheException = exceptionClass.isInstance(e);
		Throwable t = e;
		while (i < nCheckedNestedExceptions && !isTheException && t.getCause() != null)
		{
			t = t.getCause();
			isTheException = exceptionClass.isInstance(t);
			i++;
		}
		
		return isTheException;
	}
	
	
	public static Throwable findThrowableInstance(Exception e, Class<? extends Exception> exceptionClass, int nCheckedNestedExceptions) {
		int i = 0;
		Throwable exception = null;
		boolean isTheException = exceptionClass.isInstance(e);
		if (isTheException) {
			exception = e;
		}
		Throwable t = e;
		while (i < nCheckedNestedExceptions && !isTheException && t.getCause() != null)
		{
			t = t.getCause();
			isTheException = exceptionClass.isInstance(t);
			i++;
			
			if (isTheException) {
				exception = t;
			}
			
		}
		return exception;
	}
	
	
	public static Throwable getRootCauseOrItself(Throwable e) {
		return ExceptionUtils.getRootCause(e) != null ? ExceptionUtils.getRootCause(e) : e;
	}
	
	
	public static Exception getRootCauseException(Throwable e) {
		Throwable throwable = getRootCauseOrItself(e);
		return throwable != null ? (Exception) throwable : null;
	}
	
}
