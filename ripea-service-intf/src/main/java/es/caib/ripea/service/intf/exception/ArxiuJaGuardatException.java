/**
 * 
 */
package es.caib.ripea.service.intf.exception;


@SuppressWarnings("serial")
public class ArxiuJaGuardatException extends RuntimeException {

	
	public ArxiuJaGuardatException(){
		super();
	}
	
	public ArxiuJaGuardatException(String msg){
		super(msg);
	}
}
