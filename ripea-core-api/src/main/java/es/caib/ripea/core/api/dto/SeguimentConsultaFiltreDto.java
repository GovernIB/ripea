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
public class SeguimentConsultaFiltreDto implements Serializable {

	
	private Long expedientId;
	private Long metaExpedientId;
	private MetaDocumentPinbalServeiEnumDto servei;
	private String createdByCodi;
	private Date dataInici;
	private Date dataFinal;
	private ConsultaPinbalEstatEnumDto estat;
	

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	private static final long serialVersionUID = -139254994389509932L;

}
