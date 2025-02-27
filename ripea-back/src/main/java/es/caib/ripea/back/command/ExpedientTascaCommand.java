package es.caib.ripea.back.command;

import es.caib.ripea.back.helper.ConversioTipusHelper;
import es.caib.ripea.service.intf.dto.ExpedientTascaDto;
import es.caib.ripea.service.intf.dto.PrioritatEnumDto;
import es.caib.ripea.service.intf.dto.UsuariDto;
import javax.validation.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExpedientTascaCommand {
	
	@NotNull
	private Long metaExpedientTascaId;
	@NotEmpty
	private List<String> responsablesCodi;
	private String metaExpedientTascaDescripcio;
	private Date dataInici;
	private Date dataFi;
	private Date dataLimit;
	@SuppressWarnings("unused")
	private String dataLimitString;
	@Size(max=256)
	private String comentari;
	private Integer duracio = 10;
	private PrioritatEnumDto prioritat = PrioritatEnumDto.B_NORMAL;
	@Size(max=256)
	private String titol;
	@Size(max=1024)
	private String observacions;
	private List<String> observadorsCodi;
	
	public String getDataLimitString() {
		if (dataLimit != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			return sdf.format(this.dataLimit);
		} else {
			return "";
		}
	}
	
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
	public String getTitol() {
		return titol;
	}
	public void setTitol(String titol) {
		this.titol = titol;
	}
	public String getObservacions() {
		return observacions;
	}
	public void setObservacions(String observacions) {
		this.observacions = observacions;
	}
	public List<String> getObservadorsCodi() {
		return observadorsCodi;
	}
	public void setObservadorsCodi(List<String> observadorsCodi) {
		this.observadorsCodi = observadorsCodi;
	}
	
	public PrioritatEnumDto getPrioritat() { return prioritat; }
	public void setPrioritat(PrioritatEnumDto prioritat) { this.prioritat = prioritat; }
	public Integer getDuracio() { return duracio; }
	public void setDuracio(Integer duracio) { this.duracio = duracio; }
	
	public static ExpedientTascaCommand asCommand(ExpedientTascaDto dto) {
		ExpedientTascaCommand command = ConversioTipusHelper.convertir(
				dto,
				ExpedientTascaCommand.class);
		command.setMetaExpedientTascaId(dto.getMetaExpedientTasca().getId());
		command.setMetaExpedientTascaDescripcio(dto.getMetaExpedientTasca().getDescripcio());
		if (command.getResponsablesCodi() == null && !dto.getResponsables().isEmpty()) command.setResponsablesCodi(new ArrayList<String>());
		for (UsuariDto responsable : dto.getResponsables()) {
			command.getResponsablesCodi().add(responsable.getCodi());
			
		}
		
		if (dto.getObservadors() != null && !dto.getObservadors().isEmpty()) {
			for (UsuariDto observador : dto.getObservadors()) {
				command.getObservadorsCodi().add(observador.getCodi());
				
			}
		}
		
		return command;
	}
	public static ExpedientTascaDto asDto(ExpedientTascaCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				ExpedientTascaDto.class);
	}
}