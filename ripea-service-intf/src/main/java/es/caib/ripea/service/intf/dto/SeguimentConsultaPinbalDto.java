package es.caib.ripea.service.intf.dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Date;

@Getter @Setter
public class SeguimentConsultaPinbalDto implements Serializable {

	private Long id;
	private Long documentId;
	private Long expedientId;
	private ConsultaPinbalEstatEnumDto estat;
	private String error;
	private String servei;
	private String expedientNumeroTitol;
	private String documentTitol;
	private String procedimentCodiNom;
	private String createdBy;
	private Date createdDate;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = 5103130449650386298L;
}