/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import lombok.Getter;
import lombok.Setter;


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
	private String metaExpedientNom;
	private Date dataDarrerIntent;
	private boolean expedientArxiuPropagat;
	
	private boolean annex;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
