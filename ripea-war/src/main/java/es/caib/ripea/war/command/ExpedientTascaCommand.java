package es.caib.ripea.war.command;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import es.caib.ripea.core.api.dto.ExpedientTascaDto;
import es.caib.ripea.core.api.dto.UsuariDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;


public class ExpedientTascaCommand {
	
	@NotNull
	private Long metaExpedientTascaId;
	@NotEmpty
	private List<String> responsablesCodi;
	private String metaExpedientTascaDescripcio;
	private Date dataInici;
	private Date dataFi;
	private Date dataLimit;
	private String comentari;

	public Date getDataLimit() {
		return dataLimit;
	}
	public void setDataLimit(Date dataLimit) {
		this.dataLimit = dataLimit;
	}
	public String getMetaExpedientTascaDescripcio() {
		return metaExpedientTascaDescripcio;
	}
	public void setMetaExpedientTascaDescripcio(String metaExpedientTascaDescripcio) {
		this.metaExpedientTascaDescripcio = metaExpedientTascaDescripcio != null ? metaExpedientTascaDescripcio.trim() : null;
	}
	public Long getMetaExpedientTascaId() {
		return metaExpedientTascaId;
	}
	public void setMetaExpedientTascaId(Long metaExpedientTascaId) {
		this.metaExpedientTascaId = metaExpedientTascaId;
	}
	public List<String> getResponsablesCodi() {
		return responsablesCodi;
	}
	public void setResponsablesCodi(List<String> responsablesCodi) {
		this.responsablesCodi = responsablesCodi;
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
	public String getComentari() {
		return comentari;
	}
	public void setComentari(String comentari) {
		this.comentari = comentari != null ? comentari.trim() : null;
	}
	
	public static ExpedientTascaCommand asCommand(ExpedientTascaDto dto) {
		ExpedientTascaCommand command = ConversioTipusHelper.convertir(
				dto,
				ExpedientTascaCommand.class);
		command.setMetaExpedientTascaId(dto.getMetaExpedientTasca().getId());
		command.setMetaExpedientTascaDescripcio(dto.getMetaExpedientTasca().getDescripcio());
		for (UsuariDto responsable : dto.getResponsables()) {
			command.getResponsablesCodi().add(responsable.getCodi());
			
		}
		return command;
	}
	public static ExpedientTascaDto asDto(ExpedientTascaCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				ExpedientTascaDto.class);
	}
	
	
}
