/**
 * 
 */
package es.caib.ripea.back.command;

import es.caib.ripea.back.helper.ConversioTipusHelper;
import es.caib.ripea.service.intf.dto.DocumentEnviamentEstatEnumDto;
import es.caib.ripea.service.intf.dto.SeguimentFiltreDto;
import es.caib.ripea.service.intf.dto.TascaEstatEnumDto;
import lombok.Getter;
import lombok.ToString;

import java.util.Date;

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
