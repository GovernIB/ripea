/**
 * 
 */
package es.caib.ripea.service.intf.dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Date;


/**
 * Informació del filtre de seguiment.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class SeguimentArxiuPendentsFiltreDto implements Serializable {

	private String elementNom;
	private Long expedientId;
	private Long metaExpedientId;
	private Date dataDarrerIntent;
	private Date dataCreacioInici;
	private Date dataCreacioFi;
	

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	private static final long serialVersionUID = -139254994389509932L;

}
