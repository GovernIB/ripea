package es.caib.ripea.war.helper;

import lombok.Data;

@Data
public class JsonResponse {
	private Object data;
	private boolean error;
	private String errorMsg;
	
	public JsonResponse(Object data) {
		super();
		this.data = data;
	}
	
	public JsonResponse(
			boolean error,
			String errorMsg) {
		this.error = error;
		this.errorMsg = errorMsg;
	}
	
	
}
