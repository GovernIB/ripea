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
public class SeguimentDto implements Serializable {

	private Long id;
	private Long documentId;
	private Long expedientId;
	private String expedientNom;
	private String documentNom;
	private Date dataEnviament;
	private DocumentEnviamentEstatEnumDto portafirmesEstat;
	private DocumentNotificacioEstatEnumDto notificacioEstat;
	private String enviamentDatatEstat;
	private boolean error;
	private String notificacioIdentificador;
	private String destinataris;
	
	private String organ;
	private String procediment;
	private String concepte;

	private String tascaNom;
	private Date data;
	private Date dataFinalitzacio;
	private String responsablesNom;
	private String responsableActualNom; //usuari qui ha iniciat la tasca
	private TascaEstatEnumDto tascaEstat;
	

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
