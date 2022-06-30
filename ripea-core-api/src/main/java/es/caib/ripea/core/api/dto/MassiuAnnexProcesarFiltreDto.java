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
public class MassiuAnnexProcesarFiltreDto implements Serializable {

	private String nom;
	private String numero;
	private Date dataInici;
	private Date dataFi;
	private MassiuAnnexEstatProcessamentEnumDto estatProcessament;
	private Long metaExpedientId;
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
