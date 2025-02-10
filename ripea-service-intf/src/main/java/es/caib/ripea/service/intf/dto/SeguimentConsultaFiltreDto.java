package es.caib.ripea.service.intf.dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Date;

@Getter @Setter
public class SeguimentConsultaFiltreDto implements Serializable {
	
	private Long expedientId;
	private Long metaExpedientId;
	private String servei;
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