package es.caib.ripea.core.api.dto;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class SeguimentNotificacionsFiltreDto implements Serializable {

	
	private Long expedientId;
	private String documentNom;
	private Date dataInici;
	private Date dataFinal;
	private NotificacioSeguimentEstatEnumDto notificacioEstat;
	
	private NotificaEnviamentTipusEnumDto enviamentTipus;
	private String concepte;
	private String interessat;
	private Long organId;
	private Long procedimentId;
	
	private boolean nomesAmbError;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	private static final long serialVersionUID = -139254994389509932L;

}
