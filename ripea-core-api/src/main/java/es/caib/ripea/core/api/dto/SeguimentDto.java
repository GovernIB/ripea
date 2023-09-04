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
public class SeguimentDto implements Serializable {

	private Long id;
	private Long documentId;
	private Long expedientId;
	private String expedientNom;
	private String documentNom;
	private Date dataEnviament;
	private DocumentEnviamentEstatEnumDto portafirmesEstat;
	private DocumentNotificacioEstatEnumDto notificacioEstat;
	private String notificacioIdentificador;
	private String destinataris;
	

	private String tascaNom;
	private Date data;
	private String responsablesNom;
	private String responsableActualNom; //usuari qui ha iniciat la tasca
	private TascaEstatEnumDto tascaEstat;
	

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
