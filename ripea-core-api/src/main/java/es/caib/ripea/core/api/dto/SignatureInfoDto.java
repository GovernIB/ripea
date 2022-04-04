/**
 * 
 */
package es.caib.ripea.core.api.dto;

import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class SignatureInfoDto  {

	private boolean isSigned;
	private boolean error = false;
	private String errorMsg = null;
	
	public SignatureInfoDto(
			boolean isSigned,
			boolean error,
			String errorMsg) {
		this.isSigned = isSigned;
		this.error = error;
		this.errorMsg = errorMsg;
	}
	
	public SignatureInfoDto(boolean isSigned) {
		this.isSigned = isSigned;
	}
	
	
	

}
