package es.caib.ripea.war.command;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import es.caib.ripea.core.api.dto.ExpedientTascaDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;


public class ExpedientTascaCommand {
	
	@NotNull
	private Long metaExpedientTascaId;
	@NotEmpty
	private String responsableCodi;
	private String metaExpedientTascaDescripcio;
	private Date dataInici;
	private Date dataFi;
	
	
	
	public String getMetaExpedientTascaDescripcio() {
		return metaExpedientTascaDescripcio;
	}
	public void setMetaExpedientTascaDescripcio(String metaExpedientTascaDescripcio) {
		this.metaExpedientTascaDescripcio = metaExpedientTascaDescripcio;
	}
	public Long getMetaExpedientTascaId() {
		return metaExpedientTascaId;
	}
	public void setMetaExpedientTascaId(Long metaExpedientTascaId) {
		this.metaExpedientTascaId = metaExpedientTascaId;
	}
	public String getResponsableCodi() {
		return responsableCodi;
	}
	public void setResponsableCodi(String responsableCodi) {
		this.responsableCodi = responsableCodi;
	}
	public Date getDataInici() {
		return dataInici;
	}
	public void setDataInici(Date dataInici) {
		this.dataInici = dataInici;
	}
	public Date getDataFi() {
		return dataFi;
	}
	public void setDataFi(Date dataFi) {
		this.dataFi = dataFi;
	}
	
	public static ExpedientTascaCommand asCommand(ExpedientTascaDto dto) {
		ExpedientTascaCommand command = ConversioTipusHelper.convertir(
				dto,
				ExpedientTascaCommand.class);
		command.setMetaExpedientTascaId(dto.getMetaExpedientTasca().getId());
		command.setMetaExpedientTascaDescripcio(dto.getMetaExpedientTasca().getDescripcio());
		command.setResponsableCodi(dto.getResponsable().getCodi());
		return command;
	}
	public static ExpedientTascaDto asDto(ExpedientTascaCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				ExpedientTascaDto.class);
	}
	
	
}
