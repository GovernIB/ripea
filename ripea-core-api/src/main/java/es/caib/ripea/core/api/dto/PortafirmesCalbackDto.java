package es.caib.ripea.core.api.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PortafirmesCalbackDto implements Serializable {

	private long documentId;
	private int estat;
	private PortafirmesCallbackEstatEnumDto callbackEstat;
	private String motiuRebuig;
	private String administrationId;
	private String name;
	
	private static final long serialVersionUID = 1L;
}
