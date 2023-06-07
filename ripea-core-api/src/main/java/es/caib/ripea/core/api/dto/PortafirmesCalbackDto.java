package es.caib.ripea.core.api.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PortafirmesCalbackDto implements Serializable {

	private long portafirmesId;
	private int estat;
	private PortafirmesCallbackEstatEnumDto callbackEstat;
	private String motiuRebuig;
	private String administrationId;
	private String name;
	
	private int version;
	private Date eventDate;
	private String applicationID;
	private String entityID;
	
	private String title;
	private String additionalInformation;
	private String custodyURL;
	
	private static final long serialVersionUID = 1L;
}
