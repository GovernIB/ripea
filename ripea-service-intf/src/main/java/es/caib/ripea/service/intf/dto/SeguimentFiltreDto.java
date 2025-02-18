/**
 * 
 */
package es.caib.ripea.service.intf.dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Date;


/**
 * Informació del filtre de seguiment.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class SeguimentFiltreDto implements Serializable {

	
	private String expedientNom;
	private String documentNom;
	private Date dataEnviamentInici;
	private Date dataEnviamentFinal;
	private Date dataInici;
	private Date dataFinal;
	private DocumentEnviamentEstatEnumDto portafirmesEstat;
	private TascaEstatEnumDto tascaEstat;
	private String responsableCodi;
	private Long metaExpedientTascaId;
	private Long metaExpedientId;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	private static final long serialVersionUID = -139254994389509932L;

}
