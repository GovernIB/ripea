package es.caib.ripea.core.api.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArbreJsonStateDto  implements Serializable {

	private boolean selected;
	
	private static final long serialVersionUID = -3584278288156238728L;

}
