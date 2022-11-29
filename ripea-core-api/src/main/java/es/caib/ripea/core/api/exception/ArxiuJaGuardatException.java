/**
 * 
 */
package es.caib.ripea.core.api.exception;


@SuppressWarnings("serial")
public class ArxiuJaGuardatException extends RuntimeException {

	
	public ArxiuJaGuardatException(){
		super();
	}
	
	public ArxiuJaGuardatException(String msg){
		super(msg);
	}
}
