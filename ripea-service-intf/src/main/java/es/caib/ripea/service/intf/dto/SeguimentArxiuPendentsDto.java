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
 * Informació del filtre d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class SeguimentArxiuPendentsDto implements Serializable {

	private Long id;
	private Long expedientId;
	private String elementNom;
	private String expedientNumeroNom;
	private String metaExpedientCodiNom;
	private Date dataDarrerIntent;
	private Date createdDate;
	private boolean expedientArxiuPropagat;
	
	private boolean annex;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
