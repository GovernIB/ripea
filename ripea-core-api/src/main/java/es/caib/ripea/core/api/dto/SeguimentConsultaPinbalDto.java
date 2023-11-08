/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import lombok.Getter;
import lombok.Setter;



@Getter @Setter
public class SeguimentConsultaPinbalDto implements Serializable {

	private Long id;
	private Long documentId;
	private Long expedientId;
	private ConsultaPinbalEstatEnumDto estat;
	private String error;
	private MetaDocumentPinbalServeiEnumDto servei;
	private String expedientNumeroTitol;
	private String documentTitol;
	private String procedimentCodiNom;
	private String createdBy;
	private Date createdDate;
	

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = 1;

}
