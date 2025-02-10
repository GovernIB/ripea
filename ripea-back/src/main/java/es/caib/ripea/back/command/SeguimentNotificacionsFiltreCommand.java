/**
 * 
 */
package es.caib.ripea.back.command;

import es.caib.ripea.back.helper.ConversioTipusHelper;
import es.caib.ripea.service.intf.dto.NotificaEnviamentTipusEnumDto;
import es.caib.ripea.service.intf.dto.NotificacioSeguimentEstatEnumDto;
import es.caib.ripea.service.intf.dto.SeguimentNotificacionsFiltreDto;
import es.caib.ripea.service.intf.utils.Utils;
import lombok.Getter;
import lombok.ToString;

import java.util.Date;


@Getter
@ToString
public class SeguimentNotificacionsFiltreCommand {


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
	
	
	public static SeguimentNotificacionsFiltreCommand asCommand(SeguimentNotificacionsFiltreDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				SeguimentNotificacionsFiltreCommand.class);
	}
	
	public static SeguimentNotificacionsFiltreDto asDto(SeguimentNotificacionsFiltreCommand command){
		SeguimentNotificacionsFiltreDto dto = ConversioTipusHelper.convertir(
				command,
				SeguimentNotificacionsFiltreDto.class);
		return dto;
	}

	public void setDocumentNom(String documentNom) {
		this.documentNom = Utils.trim(documentNom);
	}
	public void setDataInici(Date dataInici) {
		this.dataInici = dataInici;
	}
	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}
	public void setExpedientId(Long expedientId) {
		this.expedientId = expedientId;
	}
	public void setEnviamentTipus(NotificaEnviamentTipusEnumDto enviamentTipus) {
		this.enviamentTipus = enviamentTipus;
	}
	public void setConcepte(String concepte) {
		this.concepte = Utils.trim(concepte);
	}
	public void setInteressat(String interessat) {
		this.interessat = Utils.trim(interessat);
	}
	public void setOrganId(Long organId) {
		this.organId = organId;
	}
	public void setProcedimentId(Long procedimentId) {
		this.procedimentId = procedimentId;
	}
	public void setNotificacioEstat(NotificacioSeguimentEstatEnumDto notificacioEstat) {
		this.notificacioEstat = notificacioEstat;
	}
	public void setNomesAmbError(boolean nomesAmbError) {
		this.nomesAmbError = nomesAmbError;
	}
	

}
