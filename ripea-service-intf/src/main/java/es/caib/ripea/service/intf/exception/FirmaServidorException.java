/**
 * 
 */
package es.caib.ripea.service.intf.exception;

import lombok.Getter;

@Getter
@SuppressWarnings("serial")
public class FirmaServidorException extends RuntimeException {

	private String documentNom;
	private String errorMsg;

	public FirmaServidorException(
			String documentNom,
			String errorMsg) {
		super(errorMsg);
		this.documentNom = documentNom;
		this.errorMsg = errorMsg;
	}



}
