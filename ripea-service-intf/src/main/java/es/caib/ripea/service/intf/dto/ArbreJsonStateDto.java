package es.caib.ripea.service.intf.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArbreJsonStateDto  implements Serializable {

	private boolean selected;
	
	private static final long serialVersionUID = -3584278288156238728L;

}
