package es.caib.ripea.service.intf.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

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
