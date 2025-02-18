/**
 * 
 */
package es.caib.ripea.service.intf.dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Date;



@Getter @Setter
public class MassiuAnnexProcesarFiltreDto implements Serializable {

	private String nom;
	private String numero;
	private Date dataInici;
	private Date dataFi;
	private MassiuAnnexEstatProcessamentEnumDto estatProcessament;
	private Long metaExpedientId;
	private Long expedientId;
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
