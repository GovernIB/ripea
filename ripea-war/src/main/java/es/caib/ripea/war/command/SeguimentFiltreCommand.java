/**
 * 
 */
package es.caib.ripea.war.command;

import java.util.Date;

import es.caib.ripea.core.api.dto.DocumentEnviamentEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentNotificacioEstatEnumDto;
import es.caib.ripea.core.api.dto.SeguimentFiltreDto;
import es.caib.ripea.core.api.dto.TascaEstatEnumDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;
import lombok.Getter;
import lombok.ToString;

/**
 * Command per al manteniment d'organs.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@ToString
public class SeguimentFiltreCommand {


	private String expedientNom;
	private String documentNom;
	private Date dataEnviamentInici;
	private Date dataEnviamentFinal;
	private Date dataInici;
	private Date dataFinal;
	private DocumentEnviamentEstatEnumDto portafirmesEstat;
	private DocumentNotificacioEstatEnumDto notificacioEstat;
	private TascaEstatEnumDto tascaEstat;
	private String responsableCodi;
	private Long metaExpedientTascaId;
	
	
	public static SeguimentFiltreCommand asCommand(SeguimentFiltreDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				SeguimentFiltreCommand.class);
	}
	
	public static SeguimentFiltreDto asDto(SeguimentFiltreCommand command){
		SeguimentFiltreDto dto = ConversioTipusHelper.convertir(
				command,
				SeguimentFiltreDto.class);
		return dto;
	}

	public void setExpedientNom(String expedientNom) {
		this.expedientNom = expedientNom != null ? expedientNom.trim() : null;
	}
	public void setDocumentNom(String documentNom) {
		this.documentNom = documentNom != null ? documentNom.trim() : null;
	}
	public void setDataEnviamentInici(Date dataEnviamentInici) {
		this.dataEnviamentInici = dataEnviamentInici;
	}
	public void setDataEnviamentFinal(Date dataEnviamentFinal) {
		this.dataEnviamentFinal = dataEnviamentFinal;
	}
	public void setPortafirmesEstat(DocumentEnviamentEstatEnumDto portafirmesEstat) {
		this.portafirmesEstat = portafirmesEstat;
	}
	public void setNotificacioEstat(DocumentNotificacioEstatEnumDto notificacioEstat) {
		this.notificacioEstat = notificacioEstat;
	}
	public void setTascaEstat(TascaEstatEnumDto tascaEstat) {
		this.tascaEstat = tascaEstat;
	}
	public void setResponsableCodi(String responsableCodi) {
		this.responsableCodi = responsableCodi;
	}
	public void setMetaExpedientTascaId(Long metaExpedientTascaId) {
		this.metaExpedientTascaId = metaExpedientTascaId;
	}
	public void setDataInici(Date dataInici) {
		this.dataInici = dataInici;
	}
	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}
	

}
